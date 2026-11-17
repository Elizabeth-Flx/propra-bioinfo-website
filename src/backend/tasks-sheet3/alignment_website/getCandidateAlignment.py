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

    if params is None:
        exit(0)

    with open("pairs.txt", 'w') as f:
        f.write("A B")

    with open("seqlib.txt", 'w') as f:
        f.write("A:" + params["seq1"] + '\n')
        f.write("B:" + params["seq2"])

    call = ['java', '-jar', './alignment.jar', '--pairs', 'pairs.txt', '--seqlib', 'seqlib.txt']

    call.append('-m')
    if (params['subMatrix'] == 'bc'):
        call.append('./matrix/BlakeCohenMatrix.mat')
    elif (params['subMatrix'] == 'blos'):
        call.append('./matrix/blosum62.mat')
    elif (params['subMatrix'] == 'dayh'):
        call.append('./matrix/dayhoff.mat')
    elif (params['subMatrix'] == 'pam'):
        call.append('./matrix/pam250.mat')

    call.append('--go')
    call.append(params['go'])

    call.append('--ge')
    call.append(params['ge'])

    call.append('--mode')
    call.append(params['alignMode'])

    if (params['alignAlgo'] != 'gotoh'):
        call.append('--nw')

    call.append('--format')
    call.append('html')

    a = json.loads(subprocess.check_output(call))
    print(json.dumps(a, indent=4))
