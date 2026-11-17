#!/usr/bin/env python3

import os
import pymysql

def get_seqences(id1, id2):
    #db=pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de",user="bioprakt13",password=os.environ["DB_PASSWORD"],database="bioprakt13")
    db=pymysql.connect(host="localhost",port=3307,user="bioprakt13",password=os.environ["DB_PASSWORD"],database="bioprakt13")
    cursor= db.cursor()

    sql =  "SELECT a1.alsequence, a2.alsequence\n"
    sql += "FROM alignment_seq a1, alignment_seq a2\n"
    sql += "WHERE a1.family_name = a2.family_name\n"
    sql += 'AND a1.pdb_code = "%s"\n' % (id1)
    sql += "AND a2.pdb_code = '%s'\n" % (id2)

    print(sql)

    cursor.execute(sql)

    print(cursor.fetchall())

    for row in cursor.fetchall():
        print(row)
        
    db.close()

if __name__ == "__main__":
    get_seqences("1nct", "1itb")