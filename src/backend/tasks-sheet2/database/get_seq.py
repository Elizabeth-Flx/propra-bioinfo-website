#!/usr/bin/env python3

import os
import pymysql
import argparse 

def get_seq(source: str, id:str, attributes: str ):
    db=pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de",user="bioprakt13",password=os.environ["DB_PASSWORD"],database="bioprakt13")
    cursor= db.cursor()
    seq_keys = ""
    for seq_key in id:
        if source == "protein_seq":
            if seq_keys == "":
                seq_keys +='db_key="%s"' % (seq_key)
            else:
                seq_keys += ' or db_key="%s"' % (seq_key)
        if source == "nucleotide_seq":
            if seq_keys == "":
                seq_keys +='nseq_key="%s"' % (seq_key)
            else:
                seq_keys += ' or nseq_key="%s"' % (seq_key)       
    if attributes:
        attr_str = ""
        for attribute in attributes:
            if attr_str == "":
                attr_str += attribute
            else:
                attr_str += ","+attribute
        cursor.execute("select "+attr_str+" from "+source+" join alignment_seq on db_key = protein_code"+" where "+seq_keys)
        result = {}

        for row in cursor.fetchall():
            result[row[0]] = row[1:]
    else:    
        cursor.execute("select db_key, psequence from "+source+" where "+seq_keys)
        result = {}

        for row in cursor.fetchall():
            result[row[0]] = row[1]
    
        
    db.close()
    return result

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--id",type=str, help="ID des Datenpunkts in der Datenbak",default="",nargs="+")
    parser.add_argument("--source",type=str,help="Datenbank-Quelle für Datenpunkt angeben",default="")
    parser.add_argument("--output",type=str,help="Output Path",required=False)
    parser.add_argument("--attr",type=str,help="Specify returned attributes",required=False, nargs="+")
    args = parser.parse_args()

    if args.output:
        with open(args.output,"w+") as f:
            f.write(get_seq(args.source,args.id, args.get_ss))
    else:
        print(get_seq(args.source,args.id, args.get_ss))