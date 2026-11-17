#!/usr/bin/env python3

from Validation.wrapper import train
import os
import cgi
import sys
import json
import string
import random


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


def get_random_string():
    result_str = ''.join(random.choice(string.ascii_lowercase)
                         for _ in range(10))
    return result_str


if __name__ == "__main__":
    params = __get_params__()

    if params is None:
        exit(0)

    db = params["db"]
    version = params["version"]

    id_ = get_random_string()

    temp_path = os.path.join(os.path.dirname(
        os.path.realpath(__file__)), "temp")
    current_path = os.path.join(temp_path, id_)
    os.makedirs(current_path, exist_ok=True)

    db_path = os.path.join(current_path, "database.db")
    model_path = os.path.join(current_path, "model.mdl")

    with open(db_path, "w+") as f:
        f.write(db)

    train(db_path, model_path, version)

    with open(model_path, "r") as f:
        model = f.read()
        print(model)
