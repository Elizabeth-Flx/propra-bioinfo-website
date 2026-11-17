#!/usr/bin/env python3
import os
import argparse
import pymysql

# fasta-parser from excercise dna2rna
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

# dict for fasta format
def fasta_format_dict(list_of_values_to_append_header, header):
    dict_of_header_and_value = {}
    key_string_header = '>' + header + '_'
    for i in range(len(list_of_values_to_append_header)):
        dict_of_header_and_value[key_string_header + str(i)] = list_of_values_to_append_header[i]
        
    return dict_of_header_and_value

# methode to reverse from excercise dna2rna
def reverse_complement(sequence):
    return sequence.translate(str.maketrans("ATGC","TACG"))[::-1]

# method to find all orfs in sequence
def orf_find(sequence):
    startcodon = 'ATG'
    stopcodon_list = ['TAG', 'TAA', 'TGA']
    orf_list = []
    orf_end_list = []
    for frameshift in range(3):
        for i in range(frameshift, len(sequence), 3):
            #[i:i+x] endexclusive!!
            if sequence[i:i+3] == startcodon:
                orf_start = i
                j = i+3

                while j < len(sequence)-2:
                    if sequence[j:j+3] in stopcodon_list:
                        orf_end = j+2
                        orf_entry = sequence[orf_start:orf_end+1]
                        if orf_entry and orf_end not in orf_end_list:
                            orf_end_list.append(orf_end)
                            orf_list.append(orf_entry)
                        break
                    else:
                        j += 3
    return orf_list


# method to search for all orfs
def orf_finder_method(fasta, db_bool):
    fasta_element_dict = fasta_parser(fasta)
    all_orfs_for_one_seq = []
    all_orfs_dict_fasta = {}
    for header,dna_strand_seq in fasta_element_dict.items():
        dna_other_strand_seq = reverse_complement(dna_strand_seq)
        #finding orfs for both strands
        orfs_for_dna_strand = orf_find(dna_strand_seq)
        orfs_for_other_dna_strand = orf_find(dna_other_strand_seq)
        #appending orfs to list for both strands
        all_orfs_for_one_seq.extend([orf for orf in orfs_for_dna_strand])
        all_orfs_for_one_seq.extend([orf for orf in orfs_for_other_dna_strand])
        #getting fasta format
        all_orfs_dict_fasta = dict(fasta_format_dict(all_orfs_for_one_seq, header), **all_orfs_dict_fasta)
    
    if db_bool:
        #einfüge_template: https://www.tutorialspoint.com/python/python_database_access.htm
        # Open database connection
        db_selfmade = pymysql.connect(
            #host='mysql2-ext.bio.ifi.lmu.de', 
            host="localhost", port=3307,user='bioprakt13', password=os.environ["DB_PASSWORD"], database='bioprakt13')
        # prepare a cursor object using cursor() method
        cursor = db_selfmade.cursor()

        print(len(all_orfs_dict_fasta.items()))

        for sequence_key, sequence in all_orfs_dict_fasta.items():
            sequence_key = sequence_key.strip('>')
            # Prepare SQL query to INSERT a record into the database.
            sql = "INSERT INTO nucleotide_seq(nseq_key, nsequence) \
            VALUES ('%s', '%s')" % (sequence_key, sequence)
            print(sql)
            try:
                # Execute the SQL command
                cursor.execute(sql)
                # Commit your changes in the database
                db_selfmade.commit()
            except:
                # Rollback in case there is any error
                db_selfmade.rollback()
        # disconnect from server
        db_selfmade.close()
    return all_orfs_dict_fasta

    
if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Add a fasta-file')
    parser.add_argument("--fasta", type=argparse.FileType('r'), required=True)
    parser.add_argument('--db', action= 'store_true')
    parser.add_argument('--output', type=argparse.FileType('w'), required=False)
    args = parser.parse_args()
    all_orfs_dict_fasta = orf_finder_method(args.fasta, args.db)

    if args.output:
        for key,entry in all_orfs_dict_fasta.items():
            args.output.write(str(key) + '\n' + str(entry) + '\n')
    else:
        for key,entry in all_orfs_dict_fasta.items():
                print(str(key) + '\n' + str(entry))