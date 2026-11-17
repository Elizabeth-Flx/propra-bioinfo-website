import os
import json
import random
from collections import defaultdict

script_path = os.path.dirname(os.path.realpath(__file__))
artifacts_path = script_path

for _ in range(4):
    artifacts_path = os.path.dirname(artifacts_path)

artifacts_path = os.path.join(artifacts_path, "bin")


class Sequence:
    def __init__(self, block: str = None, id: str = None, sequence: str = None):
        if block is not None:
            lines = block.splitlines()
            self.id = lines[0].strip()
            self.sequence = "".join(lines[1:]).replace(" ", "").replace(
                "\t", "").replace("\r", "").replace("\n", "")
        elif id is not None and sequence is not None:
            self.id = id
            self.sequence = sequence
        else:
            raise Exception("Invalid arguments")

    def __str__(self):
        return f">{self.id}\n{self.sequence}"


class SequenceSecondary(Sequence):
    def __init__(self, block: str):
        lines = block.splitlines()
        self.id = lines[0].strip()
        self.sequence = lines[1][2:].strip()
        self.secondary = lines[2][2:].strip()

    def __str__(self):
        return f">{self.id}\nAS {self.sequence}\nSS {self.secondary}"


def train(db: str, model: str, version: str):
    jar = os.path.join(artifacts_path, "train.jar")
    os.system(f"java -jar {jar} --db {db} --model {model} --method {version}")


def predict(model: str, sequences: str, outfile: str, probabilities: bool = False, html: bool = False):
    jar = os.path.join(artifacts_path, "predict.jar")
    os.system(
        f"java -jar {jar} --model {model} --seq {sequences} {'--probabilities' if probabilities else ''} --format {'html' if html else 'txt'} > {outfile}")


def validate(predictions: str, seclib: str, summaryFile: str, detailedFile: str, html: bool = False):
    jar = os.path.join(artifacts_path, "validateGor.jar")
    os.system(
        f"java -jar {jar} -p {predictions} -r {seclib} -s {summaryFile} -d {detailedFile} -f {'html' if html else 'txt'}")


def evaluate_lists(train_data: list, test_data: list, validate_data: list, directory: str, version: str):
    os.makedirs(directory, exist_ok=True)

    f_train = os.path.join(directory, "train.txt")
    f_test = os.path.join(directory, "test.txt")
    f_validate = os.path.join(directory, "validate.txt")

    with open(f_train, "w") as f:
        f.write("\n\n".join([str(x) for x in train_data]))

    with open(f_test, "w") as f:
        f.write("\n\n".join([str(x) for x in test_data]))

    with open(f_validate, "w") as f:
        f.write("\n\n".join([str(x) for x in validate_data]))

    return evaluate(f_train, f_test, f_validate, version, directory)


def evaluate(database_file: str, sequence_file: str, validation_file: str, version: str, directory: str):
    os.makedirs(directory, exist_ok=True)
    f_model = os.path.join(directory, "model.mod")
    f_predictions = os.path.join(directory, "predictions.prd")
    f_summary = os.path.join(directory, "summary.txt")
    f_detailed = os.path.join(directory, "detailed.txt")

    train(database_file, f_model, version)
    predict(f_model, sequence_file, f_predictions)
    validate(f_predictions, validation_file, f_summary, f_detailed, html=True)

    with open(f_detailed, "r") as f:
        summaryJson = json.load(f)
    return summaryJson


def cross_validate(database_file: str, sequence_file: str, version: str, k: int = 5):
    sequence_secondaries = [SequenceSecondary(
        block) for block in open(database_file).read().split(">") if block]
    sequences = [Sequence(block)
                 for block in open(sequence_file).read().split(">") if block]

    pairs = []

    for sequence in sequences:
        for sequence_secondary in sequence_secondaries:
            if sequence.id == sequence_secondary.id:
                pairs.append((sequence, sequence_secondary))
                break

    random.shuffle(pairs)

    n = len(pairs)
    group_size = n // k

    results = []

    for i in range(k):
        test = pairs[i * group_size:(i + 1) * group_size]
        train = pairs[:i * group_size] + pairs[(i + 1) * group_size:]

        directory = f"fold{i}"

        train_data = [sequence_secondary for _, sequence_secondary in train]
        test_data = [sequence for sequence, _ in test]
        validate_data = [sequence_secondary for _, sequence_secondary in test]

        result = evaluate_lists(train_data, test_data,
                                validate_data, directory, version)
        results.append(result)

    return combine_dicts(results)


def combine_dicts(results: list):
    """
    Calculate the average value for each key, given a list of dictionaries.
    The original dictionary structure should be preserved.
    All the dictionaries have the same structure.
    """

    result = defaultdict(list)

    for fold_result in results:
        for result_entry in fold_result:
            if "q3" in result_entry:
                result["q3"].append(result_entry["q3"])

            if "sov" in result_entry:
                result["sov"].append(result_entry["sov"])

            for ss in ["H", "E", "C"]:
                if ss in result_entry["sovPerSS"]:
                    result[f"sov{ss}"].append(result_entry["sovPerSS"][ss])
                if ss in result_entry["qPerSS"]:
                    result[f"q{ss}"].append(result_entry["qPerSS"][ss])
    return result
