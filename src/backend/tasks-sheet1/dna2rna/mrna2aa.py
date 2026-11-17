#!/usr/bin/env python3
import argparse
from pathlib import PosixPath
import _pyio as _io

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
            headers = line.split(sep=" ")
            header = headers[0].lstrip(">")
            record = []
        else:
            record.append(line)
    records[header] = "".join(record)
    return records


def prot_seq(fasta):
    aa_table = {
        'AUA':'I', 'AUC':'I', 'AUU':'I', 'AUG':'M',
        'ACA':'T', 'ACC':'T', 'ACG':'T', 'ACU':'T',
        'AAC':'N', 'AAU':'N', 'AAA':'K', 'AAG':'K',
        'AGC':'S', 'AGU':'S', 'AGA':'R', 'AGG':'R',                
        'CUA':'L', 'CUC':'L', 'CUG':'L', 'CUU':'L',
        'CCA':'P', 'CCC':'P', 'CCG':'P', 'CCU':'P',
        'CAC':'H', 'CAU':'H', 'CAA':'Q', 'CAG':'Q',
        'CGA':'R', 'CGC':'R', 'CGG':'R', 'CGU':'R',
        'GUA':'V', 'GUC':'V', 'GUG':'V', 'GUU':'V',
        'GCA':'A', 'GCC':'A', 'GCG':'A', 'GCU':'A',
        'GAC':'D', 'GAU':'D', 'GAA':'E', 'GAG':'E',
        'GGA':'G', 'GGC':'G', 'GGG':'G', 'GGU':'G',
        'UCA':'S', 'UCC':'S', 'UCG':'S', 'UCU':'S',
        'UUC':'F', 'UUU':'F', 'UUA':'L', 'UUG':'L',
        'UAC':'Y', 'UAU':'Y', 'UGC':'C', 'UGU':'C', 
        'UGG':'W',
    }
    stop_codons = ["UAA","UAG","UGA"]
    result = ""
    for id, sequence in fasta.items():
        if id.startswith(">"):
            aa_seq = id+"\n"
        else:    
            aa_seq = ">"+id +"\n"
        
        for i in range(0, len(sequence)-3+1, 3):
            if sequence[i:i+3] not in stop_codons and sequence[i:i+3] in aa_table:
                aa_seq = aa_seq + aa_table[sequence[i:i+3]]  
            else:
                if(sequence[i:i+3]not in stop_codons and sequence[i:i+3] not in aa_table):
                    aa_seq = ""
                break
        if(len(sequence)%3 != 0):
            aa_seq = ""              
        result+= aa_seq+ "\n"
    return result    


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Add a fasta-file')
    parser.add_argument("--fasta", type=argparse.FileType('r'), required=True)
    args = parser.parse_args()
    print(prot_seq(fasta_parser(args.fasta)))