#!/usr/bin/env python3

import os
import pymysql

data_dir = "/mnt/biocluster/praktikum/bioprakt-ws22/Data/HOMSTRAD"


def add_double_quote(string):
    result = '"%s"' % string
    return result


class Alignment_Sequence:
    def __init__(self, block: str, tem_file):
        lines = block.splitlines()
        metadata = lines[1].replace("\\:","$")
        metadata = metadata.split(":")
        self.ss_type = metadata[0]
        self.pdb_id = metadata[1]
        self.start_index = "".join(filter(str.isdigit,metadata[2]))
        self.start_chain = metadata[3]
        self.end_index = "".join(filter(str.isdigit,metadata[4]))
        self.end_chain = metadata[5]
        self.protein_name = metadata[6].replace("$",":")
        self.protein_source = metadata[7]
        self.resolution = metadata[8]
        self.r_factor = metadata[9]
        self.sequence = "".join(lines[2:])
        self.secondary_structure = ""
        self.protein_code = lines[0]
        with open(tem_file,"r") as f:
            content = f.read()

        splitted = content.split(">")[1:]
        
        for entry in splitted:
            ss_entry = entry.split("\n")
            entry_pdb = ss_entry[0]
            entry_type = ss_entry[1]
            if self.protein_code == entry_pdb and entry_type.startswith("secondary structure"):
                secondary_structure = "".join(ss_entry[2:])
                self.secondary_structure = secondary_structure

        self.protein_code = self.protein_code.split(";")[1].upper()

        
        if self.start_index == "":
            self.start_index = add_double_quote(alignment_sequence.start_index)
        if self.end_index == "":
            self.end_index = add_double_quote(alignment_sequence.end_index)

class Alignment:
    def __init__(self, alignment_file: str, tem_file: str):
        with open(alignment_file, "r") as f:
            content = f.read()
        
        splitted = content.split(">")

        self.family = None
        self.class_ = None

        splitted_family = splitted[0].split("\n")

        for line in splitted_family:
            if line.startswith("C; family: "):
                self.family = line.lstrip("C; family: ")
            elif line.startswith("C; class: "):
                self.class_ = line.lstrip("C; class: ")

        self.alignments = [Alignment_Sequence(block,tem_file) for block in splitted[1:]]



for family_dir in os.listdir(data_dir):
    alignment_dir_path = os.path.join(data_dir, family_dir)
    if not os.path.isdir(alignment_dir_path):
        continue
    alignment_file = None
    for file in os.listdir(alignment_dir_path):
        if file.lower() == family_dir.lower()+".ali":
            alignment_file = os.path.join(alignment_dir_path,file)
        if file.lower() == family_dir.lower()+".tem":
            tem_file = os.path.join(alignment_dir_path,file)

    if alignment_file == None:
        print("WARNING: Did not find .ali file in directory: "+family_dir)        
    alignment = Alignment(alignment_file,tem_file)
    db=pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de",user="bioprakt13",password=os.environ["DB_PASSWORD"],database="bioprakt13")
    cursor= db.cursor()
    family_name_str = add_double_quote(alignment.family)
    family_class_str = add_double_quote(alignment.class_)   
    cursor.execute("INSERT INTO alignment(family_name,family_class) VALUES("+family_name_str+","+family_class_str+")")
    for row in cursor.fetchall():
        print(row)
    db.close()   
    for alignment_sequence in alignment.alignments:
        db=pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de",user="bioprakt13",password=os.environ["DB_PASSWORD"],database="bioprakt13")
        cursor= db.cursor()
        if alignment_sequence.start_chain and alignment_sequence.end_chain:
            values = add_double_quote(alignment_sequence.pdb_id)+","+add_double_quote(alignment_sequence.sequence)+","+alignment_sequence.r_factor+","+alignment_sequence.resolution+","+add_double_quote(alignment_sequence.ss_type)+","+add_double_quote(alignment_sequence.protein_source)+","+add_double_quote(alignment_sequence.protein_name)+","+add_double_quote(alignment.family)+","+add_double_quote(alignment_sequence.start_chain)+","+add_double_quote(alignment_sequence.end_chain)+","+alignment_sequence.start_index+","+alignment_sequence.end_index+","+add_double_quote(alignment_sequence.secondary_structure)+","+add_double_quote(alignment_sequence.protein_code)
            cursor.execute("INSERT INTO alignment_seq(pdb_code,alsequence,r_faktor,resolution,method,organism_name,protein_name,family_name,start_chain,end_chain,start,end,secondary_structure,protein_code) VALUES("+values+")")
    
        else:
            values = add_double_quote(alignment_sequence.pdb_id)+","+add_double_quote(alignment_sequence.sequence)+","+alignment_sequence.r_factor+","+alignment_sequence.resolution+","+add_double_quote(alignment_sequence.ss_type)+","+add_double_quote(alignment_sequence.protein_source)+","+add_double_quote(alignment_sequence.protein_name)+","+add_double_quote(alignment.family)+","+alignment_sequence.start_index+","+alignment_sequence.end_index+","+add_double_quote(alignment_sequence.secondary_structure)+","+add_double_quote(alignment_sequence.protein_code)
            cursor.execute("INSERT INTO alignment_seq(pdb_code,alsequence,r_faktor,resolution,method,organism_name,protein_name,family_name,start,end,secondary_structure,protein_code) VALUES("+values+")")
        for row in cursor.fetchall():
            print(row)    
        db.close()
      

