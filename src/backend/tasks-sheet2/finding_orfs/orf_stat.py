#!/usr/bin/env python3
import argparse
from orf_finder import orf_finder_method
import matplotlib.pyplot as plt
import numpy as np

def orf_stat_method(fasta_file, histogram_path, bins_int, lower_int, upper_int):
    orfs_of_specific_lengths = []
    all_orfs_dict = orf_finder_method(fasta_file, db_bool=False)
    for orf in all_orfs_dict.values():
        #all orfs of len for aas
        if len(orf)//3 in range(lower_int, upper_int):
            orfs_of_specific_lengths.append(orf)
    print(len(orfs_of_specific_lengths))

    #if histogram to plot: need hist_path and bins
    if histogram_path and bins_int:
        len_list = []
        #generating list of all lengths of orfs
        for orf in orfs_of_specific_lengths:
            len_list.append(len(orf)//3)
        len_list = np.array(len_list)
        bins = []
        for i in range(lower_int, upper_int + 1, bins_int):
            bins.append(i)

        #Histogram generating
        plt.close()
        plt.hist(len_list, bins=bins)
        plt.xlabel("number of AAs")
        plt.ylabel("number of occurrencies")
        plt.title('ORFs of specific length')
        plt.savefig(histogram_path)


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Add a fasta-file, histogram, range for orfs, ggf bins')
    parser.add_argument("--fasta", type=argparse.FileType('r'), required=True)
    parser.add_argument('--histogram', required=False)
    parser.add_argument('--bins', type=int, required=False)
    parser.add_argument('--lower', type=int)
    parser.add_argument('--upper', type=int)
    args = parser.parse_args()
    orf_stat_method(args.fasta, args.histogram, args.bins, args.lower, args.upper)