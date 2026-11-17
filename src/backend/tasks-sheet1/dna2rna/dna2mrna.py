#!/usr/bin/env python3

# fasta-parser
def fasta_parser(fasta):
    header, record = "", []
    records = dict()
    for line in fasta:
        line = line.rstrip()
        if not line:
            continue
        if line.startswith(">"):
            if record:
                records[header] = "".join(record)
            headers = line.split(sep=" ")
            header = headers[0].lstrip(">")
            record = []
        else:
            record.append(line)
    records[header] = "".join(record)
    return records

def transcribed_orf(fasta_file_dict):
    trans_transcription = str.maketrans("ACGT","TGCA")
    result = ""
    for id, sequence in fasta_file_dict.items():
        sequence = sequence.replace("T","U")
        if id.startswith(">"):
            id_sequence = id+"\n"+sequence+"\n"
        else:    
            id_sequence = ">"+id+"\n"+sequence+"\n"
        result += id_sequence
    return result

if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(description='Add a fasta-file')
    parser.add_argument("--fasta", type=argparse.FileType('r'), required=True)
    args = parser.parse_args()
    print(transcribed_orf(fasta_parser(args.fasta)))