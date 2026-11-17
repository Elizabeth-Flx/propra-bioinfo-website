#!/usr/bin/env python3
import argparse
import re

# Read a fasta file and return a single sequence (ignores lines beginning with >)
def read_fasta_file(fasta_file):
    f = fasta_file.read()
    sequence = ''.join([line if not line.startswith('>') else ' ' for line in f.split('\n')])
    return sequence


# Calc expected incidence of a pattern in a genome using equal probabilities
def calc_exp_simple(pattern, genome):
    pattern_len = len(pattern)
    genome_len = len(genome) - genome.count(' ')
    return (genome_len - pattern_len + 1) * (1 / 4 ** pattern_len)

# Calc probabilities of nucleotides in the genome
def calc_rH(genome):
    genome_len = len(genome) - genome.count(' ')
    rH = {
        'A': genome.count('A') / genome_len,
        'C': genome.count('C') / genome_len,
        'G': genome.count('G') / genome_len,
        'T': genome.count('T') / genome_len
    }
    return rH

# Calc expected incidence of a pattern in a genome using probabilities of nucleotides in the genome
def calc_exp_rH(pattern, genome, rH):
    prob_pattern = 1
    for char in pattern:
        prob_pattern *= rH[char]
    genome_len = len(genome) - genome.count(' ')
    return (genome_len - len(pattern) + 1) * prob_pattern

# Calc incidence of a pattern in a genome (overlapping)
def calc_inc(pattern, genome):
    matches = re.finditer('(?=(%s))' % (pattern), genome)
    return len(tuple(matches))

# Calc the ratio of the observed incidence to the expected incidence
def calc_fc(pattern, genome, rH):
    return calc_inc(pattern, genome) / calc_exp_rH(pattern, genome, rH)



def write_results_to_txt(sequences, genome_seq):
    with open("results.txt", 'w') as f:

        # Probability of nucleotides in the genome
        rH = calc_rH(genome_seq)
        f.write("A: %05.2f%% C: %05.2f%% G: %05.2f%% T: %05.2f%%\n\n" % (rH['A']*100, rH['C']*100, rH['G']*100, rH['T']*100))
        
        # Information regarding each pattern in the genome 
        for pattern in sequences:
            exp_simple = calc_exp_simple(pattern, genome_seq)
            exp_rH = calc_exp_rH(pattern, genome_seq, rH)
            inc = calc_inc(pattern, genome_seq)
            fc = calc_fc(pattern, genome_seq, rH)

            f.write("%s:\t%s\t%s\t%s\t%03.1f\n" % (pattern, int(exp_simple+.5), int(exp_rH+.5), int(inc+.5), fc))



if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--sequence", nargs='+', type=str)
    parser.add_argument("--genome", type=argparse.FileType('r'))

    args = vars(parser.parse_args())

    sequences   = args['sequence']
    genome_file = args['genome']

    genome_seq = read_fasta_file(genome_file)

    # Output data to file
    #write_results_to_txt(sequences, genome_seq)

    for seq in sequences:
        print("%s: %s" % (seq, calc_inc(seq, genome_seq)))
