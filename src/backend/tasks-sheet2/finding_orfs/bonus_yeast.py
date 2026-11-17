#!/usr/bin/env python3
from collections import defaultdict


def finding_overlap(gff):

    gene_list = set()
    mRNA_id_to_parent_dict = defaultdict(list)
    for line in gff:
        if line[0] == '>':
            break
        elif line[0] != '#' and len(line) >=8:
            columns_of_one_line = line.strip().split('\t')
            #print(columns_of_one_line)
            #print(columns_of_one_line[2])
            # defining what we are looking for 
            clasification = str(columns_of_one_line[2])
            #print(clasification)
            orf_start = int(columns_of_one_line[3])
            orf_stop = int(columns_of_one_line[4])
            id_to_compare = columns_of_one_line[8].split(';')[0].strip('ID=')
            strand = columns_of_one_line[6]
            # creating entries in dict to compare afterwards
            #for all genes
            if clasification == 'gene':
                gene_id = id_to_compare
                gene_list.add(gene_id)
            #for all mRNAs and their specific gene
            elif clasification == 'mRNA':
                mRNA_id = id_to_compare
                parent_gene = columns_of_one_line[8].split(';')[2].strip('ID=')
                mRNA_id_to_parent_dict[parent_gene].append([int(orf_start), int(orf_stop), mRNA_id, strand])
            
    #go threw all nRNA entry of each gene and see if they overlap
    for gene in gene_list:
        #compare all transcripts for same gene
        for orf in mRNA_id_to_parent_dict[gene]:
            for other_orf in mRNA_id_to_parent_dict:
                #Ungleichheit prüfen
                if orf[2] != other_orf[2]:
                    #Prüfen ob überlappen
                    if orf[0] < other_orf[0] and other_orf[0] < orf[1] and orf[1] < other_orf[1]:
                        #Prüfen ob same strand
                        if orf[3] == other_orf[3]:
                            return True
    return False


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Add a fasta-file, histogram, range for orfs, ggf bins')
    parser.add_argument("--fasta", type=argparse.FileType('r'), required=True)
    args = parser.parse_args()
    print(finding_overlap(args.fasta))
    
    
#Solution: False: there are no overlaps
