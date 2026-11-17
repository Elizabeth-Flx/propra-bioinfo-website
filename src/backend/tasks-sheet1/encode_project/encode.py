#!/usr/bin/env python3
import os
from collections import defaultdict

#tsv to list of lists
def tsv_to_list(file):
    tsv = file.readlines()
    result = [line.split(',') for line in tsv]
    #the first line only contains the titles of the columns
    result = result[1:] 
    return result

def encode_method_type(tsv_list, output_dir):
    type_count_dict = {}
    #counting all experiments
    for entries in tsv_list:
        entry = entries[0]
        if entry not in type_count_dict:
            type_count_dict[entry] = 1
        else: 
            type_count_dict[entry] = type_count_dict[entry] + 1

    file_name = 'exptypes.tsv'
    #write in given directory
    with open(os.path.join(output_dir, file_name), 'w') as f:
        for key,entry in type_count_dict.items():
            f.write(str(key) + '\t' + str(entry) + '\n')

def encode_method_antibody(tsv_list, output_dir):
    #create set to eliminate duplicate antibodies
    celltype_antibody_dict = defaultdict(set)
    celltype_antibody_count_dict = {}
    for entries in tsv_list:
        possible_celltype = entries[1]
        possible_antibody = entries[2].split(' ')[0]
        celltype_antibody_dict[possible_celltype]
        #select only Antibodies for the chip experiment
        if possible_antibody.startswith('Antibody=') and entries[0] == 'ChIP-seq':
            antibody = possible_antibody.strip('Antibody=')
            celltype_antibody_dict[possible_celltype].add(antibody)
    
    #get the number of unique antibodies
    for key in celltype_antibody_dict:
        celltype_antibody_count_dict[key] = len(celltype_antibody_dict[key])

    file_name = 'antibodies.tsv'
    with open(os.path.join(output_dir, file_name), 'w') as f:
        for key,entry in celltype_antibody_count_dict.items():
            f.write(str(key) + '\t' + str(entry) + '\n')

def encode_method_chip_rna_seq(tsv_list, output_dir):
    #lists for all celltypes and dccs for chip and rna
    chip_dcc_dict = defaultdict(list)
    rna_dcc_dict = defaultdict(list)
    #iterate threw rows, select wanted celltypes and dccs into lists
    for row in tsv_list:
        experiment = row[0]
        celltype = row[1]
        possible_antibody = row[2].split(' ')[0]
        dcc = row[9]
        if not dcc:
            continue
        if experiment == 'ChIP-seq' and possible_antibody == 'Antibody=H3K27me3':
            chip_dcc_dict[celltype].append(dcc)
        elif experiment == 'RNA-seq':
            rna_dcc_dict[celltype].append(dcc)
    #filter only celltypes occurring in both
    both_found = chip_dcc_dict.keys() & rna_dcc_dict.keys()

    file_name = 'chip_rna_seq.tsv'
    with open(os.path.join(output_dir, file_name), 'w') as f:
        f.write('cell line' + '\t' + 'RNAseq Accession' + '\t' + 'ChIPseq Accession' + '\n')
        for celltype in both_found:
            #sort lexicographical
            rna_dcc = rna_dcc_dict[celltype]
            rna_dcc.sort()
            chip_dcc = chip_dcc_dict[celltype]
            chip_dcc.sort()
            f.write(celltype + '\t' + ','.join(rna_dcc) + '\t' + ','.join(chip_dcc) + '\n')


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='finding keywords in file for acs')
    parser.add_argument('--output',type = str)
    parser.add_argument('--input', type=argparse.FileType("r"), help='Data file')

    args = parser.parse_args()
    data = tsv_to_list(args.input)
    
    encode_method_type(data, args.output)
    encode_method_antibody(data, args.output)
    encode_method_chip_rna_seq(data, args.output)
    