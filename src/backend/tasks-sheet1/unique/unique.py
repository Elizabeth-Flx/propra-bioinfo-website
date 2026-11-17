#!/usr/bin/env python3

import argparse
from collections import defaultdict
import matplotlib.pyplot as plt

parser = argparse.ArgumentParser(description='Find unique lines in a file')
parser.add_argument('--fasta', type=argparse.FileType('r'),
                    help='Fasta file to read')
parser.add_argument('--k', type=int, nargs='+', help='K-mer size(s) to use')
parser.add_argument('--start', type=int, default=0)

args = parser.parse_args()

header, record = "", []
records = dict()

for line in args.fasta:
    line = line.rstrip()
    if not line:
        continue
    if line.startswith(">"):
        if record:
            records[header] = "".join(record)
        header = line[1:]
        record = []
    else:
        record.append(line)
records[header] = "".join(record)


def kmerize(seq, k):
    kmers = []
    for i in range(len(seq) - k + 1):
        kmers.append(seq[i:i+k])
    return kmers


k_unique = {}
total_genes = len(records)

for k in args.k:
    kmer_sequences = defaultdict(list)
    for header, seq in records.items():
        if args.start:
            kmer = seq[args.start:args.start+k]
            if len(kmer) == k:
                kmer_sequences[kmer].append(header)
        else:
            for kmer in set(kmerize(seq[args.start:], k)):
                kmer_sequences[kmer].append(header)
    filtered = {k: v for k, v in kmer_sequences.items() if len(v) == 1}

    unique_records = set([v[0] for v in filtered.values()])

    k_unique[k] = len(unique_records)

for k, v in k_unique.items():
    print(f"{k}\t{v}")

key_unique_list = list(k_unique.keys())
unique_list_percentages = []
for key in key_unique_list:
    unique_list_percentages.append(k_unique[key]/total_genes)
key_unique_list = [str(x) for x in key_unique_list]
plot_percentages = plt.bar(key_unique_list, unique_list_percentages)
i = 0
for p in plot_percentages:
    width = p.get_width()
    height = p.get_height()
    x, y = p.get_xy()
    plt.text(x+width/2,
             y+height*1.01,
             str(round(unique_list_percentages[i]*100, 2))+'%',
             ha='center',
             weight='bold', )
    i += 1
plt.ylim((0, 1))
plt.savefig("plot.png", bbox_inches="tight",)
