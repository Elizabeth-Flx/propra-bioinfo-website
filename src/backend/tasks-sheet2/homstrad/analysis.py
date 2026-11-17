#!/usr/bin/env python3

import os
import pymysql
from collections import Counter

db=pymysql.connect(host="mysql2-ext.bio.ifi.lmu.de",user="bioprakt13",password=os.environ["DB_PASSWORD"],database="bioprakt13")
cursor= db.cursor()
cursor.execute("select count(*) from alignment")
ans1=f"Answer 1:\t{cursor.fetchall()[0][0]}"

cursor.execute("select count(*) from alignment_seq")
count_all_sequences = cursor.fetchall()[0][0]

cursor.execute("select count(DISTINCT protein_code) from alignment_seq")
count_distinct = cursor.fetchall()[0][0]

ans2=f"Answer 2:\t{count_all_sequences}\t{count_distinct}"

cursor.execute("select protein_code FROM alignment_seq")
protein_codes = [row[0] for row in cursor.fetchall()]

counter = Counter(protein_codes)
frequencies_count = Counter(counter.values())
distinct_frequencies = list(frequencies_count.keys())
distinct_frequencies.sort()

frequencies_count_str = "\t".join([f"( {frequency}: {frequencies_count[frequency]} )" for frequency in distinct_frequencies])
ans3 = f"Answer 3:\t{frequencies_count_str}"

max_frequency = max(distinct_frequencies)

sorted_keys = list([protein_code for protein_code in counter.keys() if counter[protein_code] == max_frequency])
sorted_keys.sort()
filtered_frequency_str = "\t".join([f"({protein_code},{counter[protein_code]})" for protein_code in sorted_keys])
ans4 = f"Answer 4:\t{filtered_frequency_str}"

db.close()

with open("Homstrad-results.txt", "w+") as f:
    f.write(ans1 + "\n")
    f.write(ans2 + "\n")
    f.write(ans3 + "\n")
    f.write(ans4 + "\n")

