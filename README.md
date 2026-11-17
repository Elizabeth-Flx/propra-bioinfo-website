# ProPra Bioinformatics Website

A group project from the bioinformatics programming practical (ProPra) at LMU Munich. Over three task sheets we built a set of bioinformatics tools and a web interface for them:

- an **Angular** frontend (`src/frontend`);
- a **Python CGI** backend (`src/backend`), which also calls **Java** programs for sequence alignment and secondary structure prediction.

By Annika Fomm, Elizabeth Elodie Lochert, Nico Trummer and Serafina Reif.

## Features

The website has three tool pages, which you reach from the home page.

### Small tools (`/smallTools`)
- **Accession search**: look up a protein by its UniProt accession.
- **SwissProt keyword search**: find entries by keyword.
- **PROSITE pattern search**: search sequences for PROSITE patterns.
- **DNA → mRNA → protein** translation.
- **Genome report**: genome length and related statistics.
- **HOMSTRAD**: compare two structures by PDB ID.

### Sequence alignment (`/alignment`)
- Needleman–Wunsch, Smith–Waterman and Gotoh alignment with selectable substitution matrices (Java, `tasks-sheet3/alignment`).
- Candidate alignments compared with reference alignments.
- Fixpoint alignment heatmaps.
- A recursive Needleman–Wunsch implementation for comparison.

### Secondary structure prediction (`/secondary`)
- **GOR I, III, IV and V** prediction from pretrained models or an uploaded model file (Java, `tasks-sheet3/SecondaryStructurePrediction`).
- Training new models.
- Validation against DSSP assignments from the CB513 dataset.

The backend also includes standalone scripts from task sheets 1 and 2 that the website doesn't use, such as ORF finding, BLAST and Bowtie analyses, CRISPR-Cas, ENCODE and the SwissProt database import. Most of their folders contain a `.md` file documenting the work.

## Repository structure

```
src/
├── frontend/                        Angular 15 app (Angular Material)
│   └── src/app/
│       ├── pages/home/
│       ├── pages/tasks/SmallTasks/
│       ├── pages/tasks/alignment/
│       ├── pages/tasks/SecondaryStructure/
│       └── services/backend.service.ts   all backend requests
└── backend/
    ├── local_cgi_server.py          runs the CGI scripts locally
    ├── cgi.py                       replacement for the stdlib cgi module (removed in Python 3.13)
    ├── requirements.txt
    ├── tasks-sheet1/                small tools and exercises
    ├── tasks-sheet2/                databases, HOMSTRAD, ORFs, BLAST, Bowtie, ...
    └── tasks-sheet3/
        ├── alignment/               Java alignment source (Maven)
        ├── alignment_website/       CGI scripts + prebuilt alignment jars
        └── SecondaryStructurePrediction/   GOR source (Maven), prebuilt jars in bin/, CGI scripts in src/main/python/
presentation_data/gor/               CB513 data and GOR models used for the presentation
```

## Running locally

### Requirements
- Python 3.11 or newer (the backend dev container uses 3.11, and `cgi.py` covers Python 3.13+)
- Node.js and npm, for Angular 15
- Java, for the alignment and GOR jars
- Maven, only if you want to rebuild the Java code

### Backend

```bash
cd src/backend
pip install -r requirements.txt
python local_cgi_server.py            # serves http://127.0.0.1:8000/
```

`local_cgi_server.py` runs any `.py` file under `src/backend` as a CGI script and adds CORS headers, so the frontend can call the same URLs it used on the course server, for example `/tasks-sheet1/ac_search/cgi_wrapper.py`. Use `--host` and `--port` to change where it listens.

### Frontend

```bash
cd src/frontend
npm install
npm start                             # ng serve → http://localhost:4200/
```

The frontend sends its requests to `apiUrl` in `src/frontend/src/environments/environment.ts`, which is `http://localhost:8000/` by default.

### Dev containers

Both parts of the project have their own VS Code dev container:

- `src/backend/.devcontainer`: Python 3.11. It runs `pip3 install -r requirements.txt` when the container is created.
- `src/frontend/.devcontainer`: Node.js with the Angular CLI.

To use one, open `src/backend` or `src/frontend` (not the repository root) in VS Code and choose **Reopen in Container**. The backend container doesn't include Java, so you need to add it to run the alignment and GOR tools.

### Known limitations

- Some features depend on services from the original course, which aren't available outside LMU:
  - The database lookups (`tasks-sheet2/database`, `tasks-sheet2/homstrad`) connect to the course MySQL server `mysql2-ext.bio.ifi.lmu.de`. The password is read from the `DB_PASSWORD` environment variable (`export DB_PASSWORD=...` before starting the backend).
  - The alignment results page loads its heatmap from `bioclient1.bio.ifi.lmu.de`.
- The accession search, PROSITE search and genome report fetch data from external web services, so they need internet access.

## Rebuilding the Java tools

The website uses the prebuilt jars in `tasks-sheet3/alignment_website/` and `tasks-sheet3/SecondaryStructurePrediction/bin/`. To rebuild them:

```bash
cd src/backend/tasks-sheet3/alignment && mvn package
cd src/backend/tasks-sheet3/SecondaryStructurePrediction && mvn package
```

Then copy the jars from `target/` to the locations above.

## Development conventions

These are the conventions the team followed during the course:

- **Branches** are created from `main` and named `<name>/<short-description>`, for example `serafina/add-new-feature`. Delete a branch once it has been merged.
- **Changes reach `main` through merge or pull requests** reviewed by another team member. Nobody pushes directly to `main`.
- **Commit messages** are short, imperative and descriptive, for example `Add PROSITE search page` or `Fix [problem description] bug`.
- **Each commit contains one logical change.** Keep a new feature and an unrelated bug fix in separate commits. Change documentation in the same commit as the code it describes.
