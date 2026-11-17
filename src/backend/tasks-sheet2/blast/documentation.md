## BLAST documentation 
# Step 1:Indexing the Mycoplasma Genitalium Proteins
./makeblastdb is the command to create a Blast Database.
The parameter -in specifies the input (fasta-file) for indexing. With the parameter db-tpy you can specify the type of the DB you want to create (e.g for protein sequences you would write "prot"). By using "-title" you can set the title of your DB. "-out" is the output-path for the db-files
So the command to index our Mycoplasma Genitalium Proteome would be the following:
makeblastdb -in /mnt/biosoft/praktikum_data/Genomes/Mycoplasma_genitalum_protein.faa -dbtype prot -title mycoplasma_genitalium_proteins -out /mnt/biocluster/praktikum/bioprakt/progprakt-13/database/src/backend/tasks-sheet2/blast/blast
# Step 2: Finding a human gene sequence in the Proteome
For this search we are using a nucleotide query and searching a protein database. For this specific case you use the "blastx" command.
So the following command is used for generating the desired output in xml-format:
./blastx -db /mnt/biocluster/praktikum/bioprakt/progprakt-13/database/src/backend/tasks-sheet2/blast/blast -query /mnt/biosoft/praktikum_data/Genomes/hs_chr08.fna -out /mnt/biocluster/praktikum/bioprakt/progprakt-13/database/src/backend/tasks-sheet2/blast/output_xml.xml -outfmt 5
And for the table format:
./blastx -db /mnt/biocluster/praktikum/bioprakt/progprakt-13/database/src/backend/tasks-sheet2/blast/blast -query /mnt/biosoft/praktikum_data/Genomes/hs_chr08.fna -out /mnt/biocluster/praktikum/bioprakt/progprakt-13/database/src/backend/tasks-sheet2/blast/output_table.txt -outfmt 6
# Step 3: Interpreting one output gene
One of the hits in the output-file is Probable ATP-dependent 6-phosphofructokinase. This protein is essential for glycolosis which is a universal process that takes place in many organisms in exactly the same steps and with the same enzymes to convert glucose into pyruvate which can then be used in other metabolic processes. Probable ATP-dependent 6-phosphofructokinase is the enzyme that catalyzes the commited step. So it is quite logical the protein appears in both species.
UniProt-Entry(https://www.uniprot.org/uniprotkb/P47457/entry)

