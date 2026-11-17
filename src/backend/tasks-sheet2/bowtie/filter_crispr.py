#!/usr/bin/env python3

import argparse
import re

def read_sam_file(sam_file):
    raw = sam_file.read()
    rows = []
    for line in raw.split('\n'):
        if not line.startswith('@'):
            rows.append(line.split('\t'))
    return rows

def find_no_off_target(sam_rows):
    off_target = []
    for line in sam_rows:
        try:
            if line[11].startswith('XM'):
                off_target.append(line[9])
        except:
            pass

    return off_target

def find_mismatch_sequence(sam_rows):
    missmatch_sequences = []
    for line in sam_rows:
        try:
            if line[11].startswith('XA') and re.match("[ATGC][01]", line[12][-2:]):
                missmatch_sequences.append(line[9])
        except:
            pass
    return missmatch_sequences

def write_list_to_fasta(sequence_list, file_path):
    text_file = open(file_path, "w")
    for i in range(len(sequence_list)):
        text_file.write(">sequence " + str(i+1) + '\n')
        text_file.write(sequence_list[i] + '\n')
    text_file.close()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Filter CRISPR-Cas off-targets')
    parser.add_argument('--sam', required=True, type=argparse.FileType('r'), help='Input SAM file')
    parser.add_argument('--no-off-targets', required=True, help='Output file for no off-targets')
    parser.add_argument('--with-mismatch', required=True, help='Output file for mismatch sequences')
    args = parser.parse_args()

    sam_file = read_sam_file(args.sam)
    off_target = find_no_off_target(sam_file)
    mismatch_sequence = find_mismatch_sequence(sam_file)

    write_list_to_fasta(off_target, args.no_off_targets)
    write_list_to_fasta(mismatch_sequence, args.with_mismatch)





