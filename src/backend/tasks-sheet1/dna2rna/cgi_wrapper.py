#!/usr/bin/env python3

import os
import cgi
import sys
import json
from dna2aa import express_gene

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
        print("Content-Type: text/plain")
        print()
        return __convert_to_dict__(cgi.FieldStorage())
    elif os.environ.get("REQUEST_METHOD") == "POST":
        print("Content-Type: text/plain")
        print()
        request_body = sys.stdin.read()
        return json.loads(request_body)

if __name__ == "__main__":
    params = __get_params__()

    if params is None:
        exit(0)

    features = params["features"]
    fasta = params["fasta"]

    print(express_gene(fasta, features))
