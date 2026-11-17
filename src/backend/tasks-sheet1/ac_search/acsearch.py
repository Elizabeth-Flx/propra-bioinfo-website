#!/usr/bin/env python3

import requests


def acsearch_to_fasta(ac: str):
    try:
        ac_seq = requests.get(
            'https://rest.uniprot.org/uniprotkb/%s.fasta' % (ac), timeout=5)
        if ac_seq.status_code == 200:
            ac_seq_out = ac_seq.text
            return ac_seq_out
        else:
            print('The SwissProt AC-number does not exist')

    except Exception:
        print('A timeout occurred, most likely the SwissProt AC-number does not exist')

    return None


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser()
    parser.add_argument('--ac')

    args = parser.parse_args()

    print(acsearch_to_fasta(args.ac))
