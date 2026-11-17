#!/usr/bin/env python3

from Validation.wrapper import predict, validate
import os
import cgi
import sys
import json
import string
import random

script_dir = os.path.dirname(os.path.realpath(__file__))
pretrained_dir = os.path.join(script_dir, "Pretrained-Models")

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

    fasta = params["fasta"]
    model = params["model"]

    id_ = get_random_string()

    temp_path = os.path.join(script_dir, "temp")

    current_path = os.path.join(temp_path, id_)
    os.makedirs(current_path, exist_ok=True)

    fasta_path = os.path.join(current_path, "fasta.fasta")
    model_path = os.path.join(pretrained_dir, model + ".mdl")
    output_path = os.path.join(current_path, "output.txt")

    with open(fasta_path, "w+") as f:
        f.write(fasta)

    predict(model_path, fasta_path, output_path, probabilities=True, html=True)

    with open(output_path, "r") as f:
        predictions = json.load(f)

    response = {
        "predictions": predictions
    }

    if "reference" in params:
        summary_file = os.path.join(current_path, "summary.json")
        details_file = os.path.join(current_path, "details.json")
        reference_file = os.path.join(current_path, "reference.txt")

        with open(reference_file, "w+") as f:
            f.write(params["reference"])

        validate(output_path, reference_file, summary_file, details_file, html=True)

        with open(details_file, "r") as f:
            details = json.load(f)

        with open(summary_file, "r") as f:
            summary = json.load(f)
            response["summary"] = summary

        for prediction in predictions:
            for detail in details:
                if prediction["name"] == detail["name"]:
                    prediction["q3"] = detail["q3"]
                    prediction["sov"] = detail["sov"]
                    prediction["reference"] = detail["reference"]
                    for structure in ["H", "E", "C"]:
                        if structure in detail["qPerSS"]:
                            prediction["q" +
                                    structure] = detail["qPerSS"][structure]
                        if structure in detail["sovPerSS"]:
                            prediction["sov" +
                                    structure] = detail["sovPerSS"][structure]
                    break

        print(json.dumps(response, indent=4))
    else:
        print(json.dumps(response, indent=4))

