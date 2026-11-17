#!/usr/bin/env python3
import _pyio as _io
from genome2orf import find_orfs
from dna2mrna import transcribed_orf
from mrna2aa import prot_seq


# fasta-parser
def fasta_parser(fasta):
    if isinstance(fasta,str):
        fasta = fasta.split("\n")

    header, record = "", []
    records = dict()
    for line in fasta:
        line = line.rstrip()
        if not line:
            continue
        if line.startswith(">"):
            if record:
                records[header] = "".join(record)
            headers = line.split(sep=" ")
            header = headers[0]
            record = []
        else:
            record.append(line)
    records[header] = "".join(record)
    return records


def tsv_to_list(tsv):
    tsv = tsv.readlines()
    result = [[val for val in line.split("\t")] for line in tsv ]
    result = result[1:] # the first line only contains the titles of the columns and is not relevant for getting the orfs
    return result


def express_gene(fasta, features):
    if isinstance(fasta, str):
        fasta = _io.StringIO(fasta)
    if isinstance(features, str):
        features = _io.StringIO(features)
    return prot_seq(fasta_parser(transcribed_orf(fasta_parser(find_orfs(fasta_parser(fasta), tsv_to_list(features))))))
    

if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Add fasta of genome and features-file of genome')
    parser.add_argument('--organism', type=argparse.FileType("r"), help='genome-fasta')
    parser.add_argument('--features', type=argparse.FileType("r"), help='genome-features')

    args = parser.parse_args()

    print(express_gene(args.organism, args.features))