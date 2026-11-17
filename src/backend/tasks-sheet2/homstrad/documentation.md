# Filling the database
## Investigating the files to add
- Files can be found in /mnt/biocluster/praktikum/bioprakt-ws22/Data/HOMSTRAD
- Each subfolder contains two files: 
    - An .ali file containing the alignment
        - This file appears to contain important metadata as well as the alignment between the two peptides
    - A .tem file containing the template
        - This file does not contain much metadata, but it contains further information about both peptide sequences from the .ali file.
        - Contained information is: 
            - Aligned sequences
            - Secondary structure
            - Some more information, which appears to be irrelevant for our purposes