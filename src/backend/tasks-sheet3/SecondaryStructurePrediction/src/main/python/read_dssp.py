import argparse
from collections import defaultdict
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

def get_seclib(id_list: list, input_path: str):
    available_ids = []

    result = ""

    if input_path.endswith(".db"):
        with open(input_path,"r") as f:
            dssp_file = f.readlines()
        for line in dssp_file:
            if line.startswith(">") and line in id_list:
                result += line
                available_ids.append(line)
                relevant_id = True
            elif line.startswith(">") and line not in id_list:
                relevant_id = False
                continue
            if relevant_id and line.startswith("AS"):
                result += line
            elif relevant_id and line.startswith("SS"):
                result += line + "\n"

    if input_path.endswith("DSSP"):
        for dir in os.listdir(input_path):
            dirpath = os.path.join(input_path,dir)
            if(os.path.isdir(dirpath)):
                for file_name in os.listdir(dirpath):
                    if(file_name.split('.')[0] in id_list):
                        file_path = os.path.join(dirpath,file_name)
                        with open(dirpath+'/'+file_name,"r") as f:
                            content = f.readlines()
                            header = content[2][62:66]
                            available_ids.append(header.lower())
                            per_residue_table = content[28:] # 28 (zero-based) is the line where the per-residue table starts
                            grouped_chains = defaultdict(str)
                            ss_chains = defaultdict(str)
                            for line in per_residue_table:
                                residue_and_chain = line[11:14].split(' ')
                                structure = line[16]
                                grouped_chains[residue_and_chain[0]] += residue_and_chain[1]
                                if structure == " ":
                                    structure = "C"
                                ss_chains[residue_and_chain[0]] += structure
                            for chain, sequence in grouped_chains.items():
                                if chain != '' and len(sequence)>5:
                                    id = ">"+ header.lower() + chain.lower()
                                    result += id + "\n"
                                    result += "AS " + sequence + "\n"
                                    result += "SS " + ss_chains[chain] + "\n" + "\n"
                    

    for header in id_list:
        if header not in available_ids:
            pdb_id = header.rstrip("\n").lstrip("> ")
            pdb_content = download(pdb_id)
            secondary_structures = defaultdict(lambda: "C")
            grouped_chains = defaultdict(str)
            ss_chains = defaultdict(str) 

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

                if line.startswith("ATOM") and line[12:16].strip() == "CA":
                    chain_and_position = line[21]+str(int(line[22:26]))
                    amino_acid_3l = line[17:20].strip()
                    ss_annotation = secondary_structures[chain_and_position]
                    amino_acid_1l = amino_dict[amino_acid_3l]
                    chain_id = line[21]

                    grouped_chains[chain_id] += amino_acid_1l
                    ss_chains[chain_id] += ss_annotation

            for chain in grouped_chains.items():
                    pdb_id = header.rstrip("\n") +chain[0].lower()
                    result+= pdb_id + "\n"
                    result+= "AS "+ chain[1] + "\n"
                    result+= "SS "+ ss_chains[chain[0]] + "\n" + "\n"
            
    return result


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--ids", type=argparse.FileType("r"), required=True)
    parser.add_argument("--output",type=str, required=False)
    parser.add_argument("--input-path",type=str, required=False)
    args = parser.parse_args()
    id_list = args.ids.readlines()
    output_path = args.output
    input_path = args.input_path

    if args.output:
        with open(args.output,"w+") as f:
            f.write(get_seclib(id_list, input_path))
    else:
        print(get_seclib(id_list,input_path))
