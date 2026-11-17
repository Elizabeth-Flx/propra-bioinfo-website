#!/usr/bin/env python3
import _pyio as _io
import re

# fasta-parser
def fasta_parser(fasta):
    header, record = "", []
    records = dict()
    for line in fasta:
        line = line.rstrip()
        if not line:
            continue
        if line.startswith(">"):
            if record:
                records[header] = "".join(record)
            header = line
            record = []
        else:
            record.append(line)
    records[header] = "".join(record)
    return records


def get_crispr(fasta_dict):
    result = ""
    for locus_tag, sequence in fasta_dict.items():
        crispr_seqs = re.finditer(r"(?=(.{21}GG))",sequence)
        for match in crispr_seqs:
            result += locus_tag + "\t" + str(match.start()+1)+"\n"
            result += match.group(1) + "\n"
    return result

if __name__ == '__main__':
    import argparse
    parser = argparse.ArgumentParser(description='Add a fasta - file')
    parser.add_argument('--fasta', type=argparse.FileType("r"), help='fasta-file')

    args = parser.parse_args()

    print(get_crispr(fasta_parser(args.fasta)))