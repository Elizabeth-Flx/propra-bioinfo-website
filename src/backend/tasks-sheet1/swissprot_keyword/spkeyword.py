#!/usr/bin/env python3
import _pyio as io


def keyword_search_to_ac(keyword_list, file_to_search):
    if isinstance(file_to_search, str):
        file_to_search = io.StringIO(file_to_search)

    ac_and_kw = {}
    all_acs = []
    data = file_to_search.read()

    for block in data.split('//\n'):
        lines = block.split('\n')
        # Check if block contains keywords
        ac_keys = ''.join([line[5:]
                          for line in lines if line.startswith('AC')])
        ac_keys_list = ac_keys.split(';')
        ac_keys_list = [i.strip() for i in ac_keys_list]
        kw_value = ''.join([line[5:]
                           for line in lines if line.startswith('KW')])[:-1]
        kw_value = kw_value.split(';')
        kw_value = [i.strip() for i in kw_value]
        for ac_key in ac_keys_list:
            if ac_key in ac_and_kw:
                ac_and_kw[ac_key].update(kw_value)
            else:
                ac_and_kw[ac_key] = set(kw_value)
    for key, values in ac_and_kw.items():
        for same_key in keyword_list:
            if same_key in values:
                all_acs.append(key)
    result = []
    all_acs.sort()
    for entry in all_acs:
        if len(entry) != 0:
            result.append(entry)
    return result


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(
        description='finding keywords in file for acs')
    parser.add_argument('--keyword', nargs='+')
    parser.add_argument(
        '--swissprot', type=argparse.FileType("r"), help='Data file')

    args = parser.parse_args()

    ac_list = keyword_search_to_ac(args.keyword, args.swissprot)
    for ac in ac_list:
        print(ac)
