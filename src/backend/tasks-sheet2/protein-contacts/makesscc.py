#!/usr/bin/env python3

from collections import defaultdict
import matplotlib.pyplot as plt
import os

amino_dict = {'CYS': 'C', 'ASP': 'D', 'SER': 'S', 'GLN': 'Q', 'LYS': 'K',
              'ILE': 'I', 'PRO': 'P', 'THR': 'T', 'PHE': 'F', 'ASN': 'N',
              'GLY': 'G', 'HIS': 'H', 'LEU': 'L', 'ARG': 'R', 'TRP': 'W',
              'ALA': 'A', 'VAL': 'V', 'GLU': 'E', 'TYR': 'Y', 'MET': 'M'}


def download(pdb_id: str, fasta: bool = False):
    import requests

    if fasta:
        # Download file in FASTA format.
        url = f"https://www.rcsb.org/fasta/entry/{pdb_id}/download"
    else:
        # Download PDB file.
        # Format documentation: https://en.wikipedia.org/wiki/Protein_Data_Bank_(file_format)
        url = f"https://files.rcsb.org/download/{pdb_id}.pdb"

    response = requests.get(url)
    return response.text


class Residue:
    def __init__(self, line: str) -> None:
        self.chain_id = line[21]
        self.amino_acid = amino_dict[line[17:20].strip()]
        self.residue_id = int(line[22:26])
        self.local_contacts = set()
        self.global_contacts = set()

        self.atom_id = int(line[6:11])
        self.atom_x = float(line[30:38])
        self.atom_y = float(line[38:46])
        self.atom_z = float(line[46:54])
        self.atom_element = line[76:78].strip()

    def distance(self, other) -> float:
        return ((self.atom_x - other.atom_x)**2 + (self.atom_y - other.atom_y)**2 + (self.atom_z - other.atom_z)**2)**0.5

    def is_within(self, other, max_distance: float) -> bool:
        x_dist = abs(self.atom_x - other.atom_x)
        y_dist = abs(self.atom_y - other.atom_y)
        z_dist = abs(self.atom_z - other.atom_z)

        if x_dist > max_distance or y_dist > max_distance or z_dist > max_distance:
            return False

        return self.distance(other) <= max_distance

    def is_local(self, other, max_local: int) -> bool:
        return self.chain_id == other.chain_id and abs(self.residue_id - other.residue_id) < max_local

    def add_contact(self, other, max_distance: float, max_local: int):
        if not self.is_within(other, max_distance):
            return

        if self.is_local(other, max_local):
            self.local_contacts.add(other)
            other.local_contacts.add(self)
        else:
            self.global_contacts.add(other)
            other.global_contacts.add(self)

    def add_contacts(self, others, max_distance: float, max_local: int):
        for other in others:
            self.add_contact(other, max_distance, max_local)

    def __hash__(self) -> int:
        return self.residue_id


def create_sscc(id: str, atom_type: str,  max_distance: float, max_local: int):
    pdb_content = download(id)

    residues = []

    secondary_structures = defaultdict(lambda: "C")

    for i, line in enumerate(pdb_content.splitlines()):
        if line.startswith("ENDMDL"):
            break

        if line.startswith("HELIX"):
            start = int(line[21:25])
            end = int(line[33:37])
            chain = line[19].strip()

            for i in range(start, end + 1):
                secondary_structures[chain + str(i)] = "H"
            continue

        if line.startswith("SHEET"):
            start = int(line[22:26])
            end = int(line[33:37])
            chain = line[21].strip()

            for i in range(start, end + 1):
                secondary_structures[chain + str(i)] = "E"
            continue

        if line.startswith("ATOM"):
            a_type = line[12:16].strip()

            if a_type != atom_type:
                continue

            if residues:
                prev = residues[-1]
                prev.add_contacts(residues[:-1], max_distance, max_local)

            residues.append(Residue(line))
    residues[-1].add_contacts(residues[:-1], max_distance, max_local)

    contact_numbers = "chain\tpos\tserial\taa\tss\tglobal\tlocal\n"
    for residue in residues:
        chain_accession = residue.chain_id + str(residue.residue_id)
        contact_numbers += f"{residue.chain_id}\t{residue.residue_id}\t{residue.atom_id}\t{residue.amino_acid}\t{secondary_structures[chain_accession]}\t{len(residue.global_contacts)}\t{len(residue.local_contacts)}\n"

    contact_matrix = "\t" + "\t".join([residue.chain_id + str(residue.residue_id)
                                       for residue in residues]) + "\n"

    distances = []
    for residue_outer in residues:
        contact_matrix += residue_outer.chain_id + str(residue_outer.residue_id) + "\t" + "\t".join(
            ["1" if residue_inner in (residue_outer.local_contacts | residue_outer.global_contacts) else "0" for residue_inner in residues]) + "\n"
        distances.append([residue_outer.distance(residue_inner)
                         for residue_inner in residues])

    plt.imshow(distances)

    return contact_numbers, contact_matrix


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument("--id", type=str, help="PDB ID")
    parser.add_argument("--distance", type=float,
                        help="Distance cutoff", required=True)
    parser.add_argument("--type", type=str, help="Atom type")
    parser.add_argument("--length", type=int, required=True,
                        help="Max distance for local contacts")
    parser.add_argument("--output", type=str, help="Output directory")

    args = parser.parse_args()

    contact_numbers, contact_matrix = create_sscc(
        args.id, args.type, args.distance, args.length)

    if args.output:
        os.makedirs(args.output, exist_ok=True)
        with open(os.path.join(args.output, "contact_numbers.sscc"), "w+") as f:
            f.write(contact_numbers)
        with open(os.path.join(args.output, "contact_matrix.tsv"), "w+") as f:
            f.write(contact_matrix)
        plt.savefig(os.path.join(args.output, "distance_matrix.png"))
    else:
        print(contact_numbers)