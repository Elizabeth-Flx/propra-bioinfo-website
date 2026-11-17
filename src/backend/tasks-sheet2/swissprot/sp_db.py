#!/usr/bin/env python3
import os
import _pyio as io
import pymysql
from collections import defaultdict


def swissprot_to_entries(input_file):
    raw_text = input_file.read()
    sequence_entries = []

    for block in raw_text.split('//\n')[:-1]:
        row = []
        sequence_entry = defaultdict(lambda: "")

        for line in block.split('\n'):
            sequence_entry[line[:2]] += line[5:].strip()

        row.append(sequence_entry['AC'].split(';')[0])      # AD (Accession number) -> db_key           0
        row.append("swissprot")                             #                       -> db_source        1
        row.append(sequence_entry['OS'][:-1])               # OS (Organism species) -> organism_name    2
        row.append(sequence_entry['  '].replace(' ', ''))   #    (sequence data)    -> psequence        3
        row.append(len(row[3]))                             #                       -> length           4

        keywords = [kw.strip() for kw in sequence_entry['KW'][:-1].split(';')]
        row.append(keywords)

        sequence_entries.append(row)

    return sequence_entries

def send_data_to_server(sequence_entries):
    data_base = pymysql.connect(
                        #host="mysql2-ext.bio.ifi.lmu.de",
                        host="localhost",
                        port=3307,
                        user='bioprakt13',
                        password=os.environ["DB_PASSWORD"],
                        database='bioprakt13')
    try:
        cursor = data_base.cursor()
        for entry in sequence_entries:
            sql = 'INSERT INTO protein_seq(db_key, db_source, organism_name, psequence, length) VALUES("%s", "%s", "%s", "%s", %d)' % tuple(entry[:5])
            cursor.execute(sql)
            data_base.commit()
            for keyword in entry[5]:
                sql = 'INSERT INTO protein_kw(kw, db_key) VALUES("%s", "%s")' % (keyword, entry[0])
                cursor.execute(sql)
            
        data_base.commit()
    except:
        data_base.rollback()
    data_base.close()



if __name__ == "_main_":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument(
        '--swissprot', type=argparse.FileType("r"), help='Data file')

    args = parser.parse_args()

    sequence_entries = swissprot_to_entries(args.swissprot)
    send_data_to_server(sequence_entries)