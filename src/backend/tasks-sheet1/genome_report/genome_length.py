#!/usr/bin/env python3
import urllib.request
import re

def genome_length_method(organism):

    all_gen_len_list = []
    with urllib.request.urlopen('ftp://ftp.ncbi.nlm.nih.gov/genomes/GENOME_REPORTS/prokaryotes.txt') as f:
        file = f.read().decode('utf-8')
    
    for entry in file.split('\n')[1:]:
        entry = list(entry.split('\t'))
        for regex in organism:
            if re.match(regex, entry[0]) and entry[15] == 'Complete Genome':
                gen_len_entry = [entry[0], entry[6]]
                all_gen_len_list.append(gen_len_entry)

    return all_gen_len_list

if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='finding keywords in file for acs')
    parser.add_argument('--organism', nargs='+')
    args = parser.parse_args()
    
    result = genome_length_method(args.organism)
    for len_and_genome in result:
        print(len_and_genome[0] + '\t' + len_and_genome[1])