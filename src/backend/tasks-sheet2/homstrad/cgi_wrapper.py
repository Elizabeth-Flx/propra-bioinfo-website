#!/usr/bin/env python3

import os
import cgi
import pymysql
import json

def __convert_to_dict__(form: cgi.FieldStorage):
    return {key: form.getvalue(key) for key in form.keys()}

def __get_params__():
    print("Access-Control-Allow-Origin: *")
    print("Access-Control-Allow-Methods: POST, GET, OPTIONS")

    if os.environ.get("REQUEST_METHOD") == "OPTIONS":
        # This section handles CORS preflight requests
        print("Access-Control-Allow-Headers: Content-Type")
        print()
        return None
    else:
        print("Content-Type: text/plain")
        print()
        return __convert_to_dict__(cgi.FieldStorage())

def get_families_for(id_: str):
    cursor.execute(f"select distinct family_name from alignment_seq where protein_code = \"{id_}\" or pdb_code = \"{id_}\"")
    families = set([entry[0] for entry in cursor.fetchall()])

    return families

def get_alignment_for(family: str):
    cursor.execute(f"select alsequence, secondary_structure, protein_code from alignment_seq WHERE family_name = \"{family}\"")
    return [{"alignment": alignment, "secondary": structure, "code": code} for alignment, structure, code in cursor.fetchall()]


if __name__ == "__main__":
    params = __get_params__()

    if params is None:
        exit(0)

    if not "pdb_id" in params:
        print("Status: 400 Bad Request")
        exit(0)

    first_id = params["pdb_id"]
    second_id = params.get("pdb_id2", None)

    db=pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de",user="bioprakt13",password=os.environ["DB_PASSWORD"],database="bioprakt13")
    cursor= db.cursor()

    first_families = get_families_for(first_id)


    if not second_id:
        result = [{"name": family, "alignment": get_alignment_for(family)} for family in get_families_for(first_id)]
        print(json.dumps(result))
    else:
        second_families = get_families_for(second_id)
        common_families = first_families & second_families

        if common_families:
            results = [{"name": family, "alignment": get_alignment_for(family)} for family in common_families]
            print(json.dumps(results))
        else:
            results = [{"name": family + f" ({first_id})", "alignment": get_alignment_for(family)} for family in first_families]
            results.extend([{"name": family + f" ({second_id})", "alignment": get_alignment_for(family)} for family in second_families])

            print(json.dumps(results))