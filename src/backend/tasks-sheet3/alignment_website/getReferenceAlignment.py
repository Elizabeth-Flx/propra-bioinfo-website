#!/usr/bin/env python3

import os
import cgi
import sys
import json
import subprocess

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
    elif os.environ.get("REQUEST_METHOD") == "GET":
        print("Content-Type: application/json")
        print()
        return __convert_to_dict__(cgi.FieldStorage())
    elif os.environ.get("REQUEST_METHOD") == "POST":
        print("Content-Type: application/json")
        print()
        request_body = sys.stdin.read()
        return json.loads(request_body)

if __name__ == "__main__":
    params = __get_params__()

    #params = {
    #    "alignCandidate1": "GQETSYIEDNSNQNGAISLIFSLKEEVGALAKVLRLFEENDINLTHIESRPSRLNKDEYEFFTYLDKRTKPVLGSIIKSLRNDIGATVHELSRDKEK----------",
    #    "alignCandidate2": "-----FPEVSLPLHGGRRLMHIHENRPGVLTALNKIFAEQGVNIAA----------------QYLQ--TSAQMGYVVIDIEADEDVAEKALQAMKAIPGTIRARLLY",
    #    "alignReference1": "GQETSYIEDNSNQNGAISLIFSLKEEVGALAKVLRLFEENDINLTHIESRPSRLNKDEYEFFTYLDKRTKPVLGSIIKSLRNDIGA-TVHELSRDKEK",
    #    "alignReference2": "-----FPEVSLPLHGGRRLMHIHENRPGVLTALNKIFAEQGVNIAAQYLQTS---AQMGYVVIDIEA-DEDVAEKALQAMKAIPGTIRARLLY-----"
    #}

    if params is None:
        exit(0)

    with open("reference.txt", 'w') as f:
        f.write("Header\n")
        f.write("A: " + params["alignCandidate1"] + "\n")
        f.write("B: " + params["alignCandidate2"] + "\n")
        f.write("A: " + params["alignReference1"] + "\n")
        f.write("B: " + params["alignReference2"])

    call = ['java', '-jar', './validation_json.jar', '-f', 'reference.txt']

    a = json.loads(subprocess.check_output(call))
    print(json.dumps(a, indent=4))
