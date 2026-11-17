#!/usr/bin/env python3
import os
import _pyio as io
import pymysql

# for search in file
def keyword_search_to_ac(keyword_list, file_to_search):
    result = []
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
    all_acs.sort()
    for entry in all_acs:
        if len(entry) != 0:
            result.append(entry)
    return result

# for search in db
def keywords_db_extracting(keyword_list):
    result = set()
    data_base = pymysql.connect(
                        #host="mysql2-ext.bio.ifi.lmu.de",
                        host="localhost",
                        port=3307,
                        user='bioprakt13',
                        password=os.environ["DB_PASSWORD"],
                        database='bioprakt13')
    try:
        cursor = data_base.cursor()
        for keyword in keyword_list:
            sql = 'SELECT DISTINCT db_key FROM protein_kw WHERE kw = "%s"' % (keyword)
            cursor.execute(sql)
            result_for_one_kw = cursor.fetchall()
            for row in result_for_one_kw:
                result.add(row)
            
        data_base.commit()
    except:
        data_base.rollback()
    data_base.close()
    return result


if __name__ == '__main__':
    import argparse

    parser = argparse.ArgumentParser(
        description='finding keywords in file for acs')
    parser.add_argument('--keyword', nargs='+')
    parser.add_argument('--swissprot', type=argparse.FileType("r"), required=False)
    parser.add_argument('--db', action= 'store_true')

    args = parser.parse_args()

    if args.swissprot:
        ac_list = keyword_search_to_ac(args.keyword, args.swissprot)
        for ac in ac_list:
            print(ac)
    elif args.db:
        ac_list_db = keywords_db_extracting(args.keyword)
        for ac in ac_list_db:
            print(ac[0])

