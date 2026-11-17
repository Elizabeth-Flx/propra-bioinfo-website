#!/usr/bin/env python3

import os
import re
import requests
import argparse
import requests
import _pyio as _io
import pymysql

def read_fasta_file(fasta_file):
    sequences = {}
    f = fasta_file.read()
    current_seq_name = ''
    current_seq = ''
    for line in f.split('\n'):
        if line.startswith('>'):
            # Start of a new sequence
            if current_seq_name:
                # Store the previous sequence in the dictionary
                sequences[current_seq_name] = current_seq
            # Reset the current sequence variables
            current_seq_name = line[1:].strip()
            current_seq = ''
        else:
            # Add the current line to the current sequence
            current_seq += line.strip()
    # Store the last sequence in the dictionary
    sequences[current_seq_name] = current_seq
    return sequences


def get_database_sequences(regex):
    data_base = pymysql.connect(
                        #host="mysql2-ext.bio.ifi.lmu.de",
                        host="localhost",
                        port=3307,
                        user='bioprakt13',
                        password=os.environ["DB_PASSWORD"],
                        database='bioprakt13')
    sequences = {}
    try:
        cursor = data_base.cursor()
        sql = 'SELECT db_key, psequence FROM protein_seq WHERE psequence REGEXP "%s"' % (regex) 
        cursor.execute(sql)
        results = cursor.fetchall()
        for row in results:
            sequences[row[0]] = row[1]
    except:
        data_base.rollback()
    data_base.close()

    return sequences
    


def extract_web_ids():
    # Send a GET request to the Prosite database website
    response = requests.get('https://ftp.expasy.org/databases/prosite/prosite.dat')

    # Check if the request was successful
    if response.status_code == 200:

        web_ids = {}

        # Generate dict with prosite patterns from web
        for block in response.text.split(r'//'):
            lines = block.split('\n')
            # Check if block contains pattern
            if (lines[1].endswith('PATTERN.')):
                prosite_pattern = ''.join([line[5:] for line in lines if line.startswith('PA')])[:-1]
                web_ids[lines[2][5:-1]] = prosite_pattern
        return web_ids
    else:
        # Raise an exception if the request failed
        raise Exception('Failed to extract Prosite text.')

def prosite_to_regex(prosite_pattern):

    # Edge case
    if prosite_pattern.endswith('>]'):
        option_1 = prosite_to_regex(prosite_pattern.replace('>',''))
        option_2 = prosite_to_regex(prosite_pattern[:prosite_pattern.rfind('-')] + ">")
        return option_1 + '|' + option_2 

    else:
        # Reformat non accepted AA's
        regex_pattern = prosite_pattern.replace('{', '[^').replace('}', ']')
        # Reformat repetition
        regex_pattern = regex_pattern.replace('x','.').replace('(','{').replace(')','}')
        # Remove -
        regex_pattern = regex_pattern.replace('-','')
        # N terminus or C Terminus restrictions
        regex_pattern = regex_pattern.replace('<', '^').replace('>', '$')

        return "(?=(%s))" % (regex_pattern)


# Output from local calculation
def find_matches(fastas, prosite_pattern):
    str_output = ''
    for seq_id, seq in fastas.items():
        result = find_single_fasta(prosite_to_regex(prosite_pattern), seq, seq_id)
        if len(result) != 0:
            str_output += result
    return str_output[:-1]

def find_single_fasta(regex, sequence, sequence_id):
    r = re.compile(regex)
    result = ''
    for match in r.finditer(sequence):
        result += "%s\t%s\t%s" % (sequence_id, str(match.start() + 1), match.group(1)) + '\n'
    return result

# Output from Prosite webseite 
def web_prosite_request(raw_fasta, prosite_pattern):
    url = "https://prosite.expasy.org/cgi-bin/prosite/PSScan.cgi"
    r = requests.post(url, data = {
        'meta': 'opt3',
        'meta3_protein': 'opt1',
        'seq': raw_fasta,
        'sig': prosite_pattern,
        'output': 'plain',
        'submit': 'START THE SCAN'
    })
    raw = r.text
    str_results = ''

    # Extract results from HTML
    raw = raw[raw.find('\n>')-1:raw.find('</pre>')]
    raw = raw.split('\n\n')[1:-1]

    for result in raw:
        lines = result.split('\n      ')
        sequence_id = lines[0][:lines[0].find(' ')].replace('\n', '')
        for l in lines[1:]:
            str_results += "%s\t%s\t%s\n" % (sequence_id[1:], l[:l.find(' ')], l[l.rfind(' ')+1:].upper())
    return str_results[:-1]



# Required for CGI interface
def compute(fasta_file: "_io.TextIOWrapper | str", database_seqs: bool, prosite_pattern: str, web_id: str, do_external: bool):
    if isinstance(fasta_file, str):
        fasta_file = _io.StringIO(fasta_file)

    #try:
    if do_external:
        raw_fasta = ""

        if prosite_pattern is None:
            web_ids = extract_web_ids()
            prosite_pattern = web_ids[web_id]

        if database_seqs:
            fasta_dict = get_database_sequences(prosite_to_regex(prosite_pattern))
            for key, value in fasta_dict.items():
                raw_fasta += '>' + key + '\n'
                raw_fasta += value + '\n'
        else:
            raw_fasta = fasta_file.read()

        return web_prosite_request(raw_fasta, prosite_pattern)

    else:
        fasta_dict = {}

        if prosite_pattern is None:
            web_ids = extract_web_ids()
            prosite_pattern = web_ids[web_id]

        if database_seqs:
            fasta_dict = get_database_sequences(prosite_to_regex(prosite_pattern))
            output = ""
            for key, value in fasta_dict.items():
                output += key + '\n'
            return output
        else:
            fasta_dict = read_fasta_file(fasta_file)

        return find_matches(fasta_dict, prosite_pattern)
    #except:
    #    return None
    
if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--fasta", type=argparse.FileType('r'))
    parser.add_argument("--db", action="store_true")
    parser.add_argument("--pattern", type=str)
    parser.add_argument("--web")
    parser.add_argument("--extern", action="store_true")

    args = vars(parser.parse_args())

    result = compute(args['fasta'], args['db'], args['pattern'], args['web'], args['extern'])

    if result != None:
        print(result)

