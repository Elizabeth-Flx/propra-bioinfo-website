#!/usr/bin/env python3

import os
import cgi
from acsearch import acsearch_to_fasta

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

if __name__ == "__main__":
    params = __get_params__()

    if params is None:
        exit(0)

    print(acsearch_to_fasta(params["ac"]))