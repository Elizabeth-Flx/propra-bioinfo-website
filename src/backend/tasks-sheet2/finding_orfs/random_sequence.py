#!/usr/bin/env python3
import argparse
import random

#calculation of existing gc_content
#def gc_counter(seq):
def gc_counter(seq):
    
    all_seqs = len(seq)
    gc_number = 0
    for nuc in seq:
        if nuc == 'C' or nuc == 'G':
            gc_number +=1
    if gc_number != 0:
        return gc_number/all_seqs
    else: 
        return 1

def random_seq_generating(length_int, gc_content):
    if gc_content != 0:
        random_sequence = 'C'
    else:
        random_sequence = 'A'
    if gc_content >= 0 and  gc_content <= 1:
        count = 0
        while count <= length_int-2:
            #appendance depending of existing gc_content and wanted gc_content
            if  gc_counter(random_sequence) < gc_content:
                random_sequence+= random.choice('CG')
            else:
                random_sequence+= random.choice('AT')
            count +=1
    nucleotide_list = list(random_sequence) 
    random.shuffle(nucleotide_list)
    return ''.join(nucleotide_list)

if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='getting the length and gc_content for a random seq')
    parser.add_argument('--length', type=int)
    parser.add_argument('--gc', type=float)
    parser.add_argument('--name', type=str, required=False)
    args = parser.parse_args()

    #for fasta format
    if args.name:
        name = args.name
        print('>' + name)
    print(random_seq_generating(args.length, args.gc))