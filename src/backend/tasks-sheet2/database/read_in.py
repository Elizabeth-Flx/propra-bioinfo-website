import os
import argparse
import pymysql

parser = argparse.ArgumentParser()
parser.add_argument('--fasta', type=str)

args = parser.parse_args()

fasta = args.fasta

def add_double_quote(string):
    result = '"%s"' % string
    return result

with open(fasta, "r") as f:
    content = f.read()
    splitted = content.split(">")
    protein_sequences = {}
    for block in splitted:
        block = block.split("\n")
        if block[0] != "":
            protein_sequences[block[0]] = block[1]

for header, sequence in protein_sequences.items():
    db=pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de",user="bioprakt13",password=os.environ["DB_PASSWORD"],database="bioprakt13")
    cursor= db.cursor()
    characteristics = header.split("|")
    organism_name = add_double_quote(characteristics[3].split("(")[0])
    length = add_double_quote(str(len(sequence)))
    db_source = add_double_quote("PDB")
    db_key = add_double_quote(characteristics[0].split("_")[0])
    print("INSERT INTO protein_seq(organism_name, length, db_source, db_key, psequence) VALUES("+organism_name+","+length+","+db_source+","+db_key+","+add_double_quote(sequence)+")")
    cursor.execute("INSERT INTO protein_seq(organism_name, length, db_source, db_key, psequence) VALUES("+organism_name+","+length+","+db_source+","+db_key+","+add_double_quote(sequence)+")")
    for row in cursor.fetchall():
        print(row)
    db.close()  
