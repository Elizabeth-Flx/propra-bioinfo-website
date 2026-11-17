#!/usr/bin/env python3
import math
import argparse
import requests
import os

def download_molecule_image(output_path, pbd_id):
    jmol_path = r".\files\Jmol.jar"
    jmol_script_path = r".\files\jmol_script.txt"

    script = "load https://files.rcsb.org/view/%s.pdb; " % (pbd_id)
    script += "cartoon only; "
    script += "select helix; color orange; select sheet; color cyan; "
    script += "write PNG %s" % (output_path + "/%s.png" % (pbd_id))

    fmol_script_file = open(jmol_script_path, 'w')
    fmol_script_file.write(script)
    fmol_script_file.close()
    os.system("%s -n -s %s" % (jmol_path, jmol_script_path))

def pbd_id_to_string(pbd_id):
    url = "https://files.rcsb.org/view/%s.pdb" % (pbd_id)
    response = requests.get(url)
    pdb_text = response.text
    return pdb_text

def extract_xyz(line):
    x = float(line[30:38].strip())
    y = float(line[38:46].strip())
    z = float(line[46:54].strip())
    return (x,y,z)

def euklidean_distance_xyz(coords_1, coords_2):
    delta_x_sqr = (coords_1[0]-coords_2[0]) ** 2
    delta_y_sqr = (coords_1[1]-coords_2[1]) ** 2
    delta_z_sqr = (coords_1[2]-coords_2[2]) ** 2
    return math.sqrt(delta_x_sqr+delta_y_sqr+delta_z_sqr)


def calc_distance_ends(pdb_text, atom_type):
    lines_of_type = [line for line in pdb_text.split('\n') if line.startswith("ATOM") and line[13:15] == atom_type]

    first = lines_of_type[0]
    last = lines_of_type[-1]
    first_coords = extract_xyz(first)
    last_coords = extract_xyz(last)

    return euklidean_distance_xyz(first_coords, last_coords)

def calc_box_edges(pbd_text):
    atoms = [line for line in pbd_text.split('\n') if line.startswith("ATOM")]
    x_vals = []
    y_vals = []
    z_vals = []
    for line in atoms:
        coords = extract_xyz(line)
        x_vals.append(coords[0])
        y_vals.append(coords[1])
        z_vals.append(coords[2])
    delta_x = max(x_vals) - min(x_vals)
    delta_y = max(y_vals) - min(y_vals)
    delta_z = max(z_vals) - min(z_vals)
    
    return delta_x, delta_y, delta_z

def calc_proportion_2nd_struct(pbd_text):
    # secondary_structures should be a set in order to prevent double counting, but in order to reproduce the required output, we use a list
    secondary_structures = []
    all_acids = set()

    for i, line in enumerate(pdb_text.splitlines()):
        if line.startswith("ENDMDL"):
            break 

        if line.startswith("HELIX"):
            start = int(line[21:25])
            end = int(line[33:37])
            chain = line[19].strip()

            secondary_structures.extend([chain + str(i) for i in range(start, end + 1)])
            continue

        if line.startswith("SHEET"):
            start = int(line[22:26])
            end = int(line[33:37])
            chain = line[21].strip()

            secondary_structures.extend([chain + str(i) for i in range(start, end + 1)])
            continue

        if line.startswith("ATOM"):
            chain = line[21]
            res_id = line[22:26].strip()
            all_acids.add(chain + res_id)

    return float(len(secondary_structures)) / len(all_acids)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Visualize multiple data sets with IDs.')
    parser.add_argument('--id', nargs='+', required=True, help='IDs of data sets to visualize')
    parser.add_argument('--output', help='Output directory for pictures')
    args = parser.parse_args()

    ids = args.id
    output_dir = args.output

    for id_ in ids:
        pdb_text = pbd_id_to_string(id_)
        print(f"{id_}\tAnteil AS in Sekundaerstruktur\t{calc_proportion_2nd_struct(pdb_text):.4f}")
        print(f"{id_}\tAbstand C_alpha\t{calc_distance_ends(pdb_text, 'CA'):.4f}")
        print(f"{id_}\tAbstand C_beta\t{calc_distance_ends(pdb_text, 'CB'):.4f}")
        edge_x, edge_y, edge_z = calc_box_edges(pdb_text)
        print(f"{id_}\tX-Groesse\t{edge_x:.4f}")
        print(f"{id_}\tY-Groesse\t{edge_y:.4f}")
        print(f"{id_}\tZ-Groesse\t{edge_z:.4f}")
        print(f"{id_}\tVolumen\t{edge_x * edge_y * edge_z:.4f}")

    if output_dir:
        for id in ids:
            download_molecule_image(output_dir, id)
