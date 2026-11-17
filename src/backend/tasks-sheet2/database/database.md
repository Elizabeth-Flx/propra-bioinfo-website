## Database - Befehle
CREATE TABLE nukleotid_seq(
    nseq_key INT PRIMARY KEY                                "Corresponds to the key that stands before ">" in the fasta" 
    nsequence MEDIUMTEXT NOT NULL                           "Nucleotide sequence"
)

CREATE TABLE protein_seq(
    incr_key_protein    INT PRIMARY KEY AUTO_INCREMENT      "Increment key that is unique for each protein"
    organism_name       VARCHAR(200) NOT NULL               "Name of the organism the protein is from"
    length              INT NOT NULL                        "Length of the Protein-Sequence"
    protein_name        VARCHAR(200) NOT NULL               "Name of the given Protein e.g. Kinase"
    db_source           VARCHAR(20) NOT NULL                "The database the sequence was taken from e.g Swissprot"
    db_key              VARCHAR(30) NOT NULL                "The key used for the Protein in the database"
    psequence           MEDIUMTEXT NOT NULL                 "The amino-acid sequence of the protein"  
)

CREATE TABLE protein_kw(
    kw_id               INT PRIMARY KEY AUTO_INCREMENT       "Increment key that is unique for each keyword"
    kw                  VARCHAR(30) NOT NULL                 "The keyword itself"
    incr_key_protein    INT NOT NULL                         "Key of Protein that contains keyword"                        
)

CREATE TABLE secondary_structure(
    incr_key_ss         INT PRIMARY KEY AUTO_INCREMENT      "Increment key unique for secondary structure"                
    ss_type             VARCHAR(20) NOT NULL                "Type of secondary structure e.g beta sheet"
    incr_key_protein    INT NOT NULL                        "Key of Protein that ss belongs to"
    start               INT NOT NULL                        "start of ss in the protein"
    end                 INT NOT NULL                        "End of ss in the protein"
)

CREATE TABLE alignment(
    incr_key_align          INT PRIMARY KEY AUTO_INCREMENT      "Increments key that is unique for alignment"      
    family_name             VARCHAR(50) NOT NULL                "Name of Alignment-Family"
    family_class            VARCHAR(30) NOT NULL                "Class of Alignment-Family
)

CREATE TABLE alignment_seq(
    incr_key_align_seq      INT PRIMARY KEY AUTO_INCREMENT      "Auto-Increment-Key for each Sequence of the Alignment"
    pdb_code                VARCHAR(30)                         "PBD-Code of Protein"
    alsequence              MEDIUMTEXT NOT NULL                  "The sequence of the alignment with gaps"
    family_class            VARCHAR(30) NOT NULL                "Class of Alignment-Family
    pdb_code                VARCHAR(30) NOT NULL                "PBD-Code of Protein"
    protein_name            VARCHAR(50) NOT NULL                "Name of the protein that is aligned"
    organism_name           VARCHAR(60) NOT NULL                "Name of the Organism the aligned protein is from"
    r_faktor                DOUBLE(10,4)                        "R-Faktor"  
    resolution              DOUBLE(10,4)                        "Resolution"
    method                  VARCHAR(20)                         "Method"
)


# Archive of used Commands DB
INSERT INTO protein_seq(organism,length,pseq_name,source_db,db_key,psequence) VALUES("danielierus lochertococus",100000000,"protein 5000","ABC","ABC","MKA")
ALTER TABLE protein_seq RENAME COLUMN organism TO organism_name
ALTER TABLE protein_seq RENAME COLUMN pseq_name TO protein_name
ALTER TABLE protein_seq RENAME COLUMN pseq_key TO incr_key_protein
ALTER TABLE protein_seq RENAME COLUMN source_db TO db_source
ALTER TABLE protein_kw RENAME COLUMN pseq_key TO incr_key_protein
ALTER TABLE alignment RENAME COLUMN ali_key TO incr_key_align
ALTER TABLE alignment RENAME COLUMN organism TO organism_name
ALTER TABLE alignment_seq RENAME COLUMN al_seq_id TO incr_key_align_seq
ALTER TABLE alignment_seq RENAME COLUMN al_seq TO alsequence
ALTER TABLE secondary_structure RENAME COLUMN ss_key TO incr_key_ss;
ALTER TABLE secondary_structure RENAME COLUMN pseq_key TO incr_key_protein;
ALTER TABLE alignment DROP COLUMN domain;
ALTER TABLE alignment DROP COLUMN r_faktor;
ALTER TABLE alignment DROP COLUMN resolution;
ALTER TABLE alignment DROP COLUMN protein_name;
ALTER TABLE alignment_seq ADD r_faktor DOUBLE(10,4);
ALTER TABLE alignment DROP COLUMN organism_name;

