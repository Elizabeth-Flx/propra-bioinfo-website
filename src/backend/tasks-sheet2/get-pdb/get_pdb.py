#!/usr/bin/env python3

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


if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("--id", type=str, help="PDB ID")
    parser.add_argument(
        "--output", type=argparse.FileType("w"), help="Output file")
    parser.add_argument("--fasta", action='store_true')

    args = parser.parse_args()

    output = download(args.id, args.fasta)

    if args.output:
        args.output.write(output)
    else:
        print(output)
