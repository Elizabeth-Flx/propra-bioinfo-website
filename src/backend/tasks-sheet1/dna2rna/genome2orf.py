#!/usr/bin/python3

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
            header = headers[0]
            record = []
        else:
            record.append(line)
    records[header] = "".join(record)
    return records

# function that converts a tsv to a list containing the lines
def tsv_to_list(tsv):
    tsv = tsv.readlines()
    result = [[val for val in line.split("\t")] for line in tsv ]
    result = result[1:] # the first line only contains the titles of the columns and is not relevant for getting the orfs
    return result

def reverse_complement(sequence):
    return sequence.translate(str.maketrans("ATGC","TACG"))[::-1]

# function that returns the orfs by using a genome fasta-, features-file
# format of feature-file: 0 - Klasse, 6 - Locus Tag, 7 - Start, 8 - Ende, 9 - Strang
def find_orfs(genome_file,features_file):
    result = ""
    for gene_feature in features_file:
        genomic_accession = ">" + gene_feature[6]
        locus_tag = ">" + gene_feature[16]
        sequence = ""
        if gene_feature[0] == "CDS":
            if gene_feature[9] == "+":
                sequence += genome_file[genomic_accession][int(gene_feature[7])-1:int(gene_feature[8])]
            if gene_feature[9] == "-":
                sequence += reverse_complement(genome_file[genomic_accession][int(gene_feature[7])-1:int(gene_feature[8])])
            result += locus_tag +"\n" + sequence + "\n"        
    return result
    
# main
if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Add a fasta-file(genome of organism) and a feature-file(standard format)')
    parser.add_argument("--organism", type=argparse.FileType('r'), required=True)
    parser.add_argument("--features", type=argparse.FileType('r'), required=True)
    args = parser.parse_args()
    print(find_orfs(fasta_parser(args.organism), tsv_to_list(args.features)))
