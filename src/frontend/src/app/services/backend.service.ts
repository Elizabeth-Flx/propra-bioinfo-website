import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';
import {Result} from "../../interfaces";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class BackendService {
  public API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {
  }

  public ac_search(accession: string): Promise<any> {
    const url = this.API_URL + 'tasks-sheet1/ac_search/cgi_wrapper.py?ac=' + accession;

    return this.getTextRequest(url);
  }

  public keyword_search(file: string, keywords: string[]): Promise<string[]> {
    const url = this.API_URL + 'tasks-sheet1/swissprot_keyword/cgi_wrapper.py';

    return this.postRequest(url, {content: file, keywords: keywords.join(',')});
  }

  public predictFromPretrained(fasta: string, model: string): Promise<Result> {
    const url = this.API_URL + 'tasks-sheet3/SecondaryStructurePrediction/src/main/python/cgi_predict_pretrained.py';

    const payload = {fasta: fasta, model: model};

    return this.postRequest(url, payload);
  }

  public predictFromModelfile(fasta: string, model: string): Promise<Result> {
    const url = this.API_URL + 'tasks-sheet3/SecondaryStructurePrediction/src/main/python/cgi_predict_modelfile.py';

    const payload = {fasta: fasta, model: model};

    return this.postRequest(url, payload);
  }

  public predictFromPretrainedWithReference(fasta: string, model: string, reference: string): Promise<Result> {
    const url = this.API_URL + 'tasks-sheet3/SecondaryStructurePrediction/src/main/python/cgi_predict_pretrained.py';

    const payload = {fasta: fasta, model: model, reference: reference};

    return this.postRequest(url, payload);
  }

  public predictFromModelfileWithReference(fasta: string, model: string, reference: string): Promise<Result> {
    const url = this.API_URL + 'tasks-sheet3/SecondaryStructurePrediction/src/main/python/cgi_predict_modelfile.py';

    const payload = {fasta: fasta, model: model, reference: reference};

    return this.postRequest(url, payload);
  }

  public prosite_pattern(file: string, pattern: string, webId: string, external: boolean) {
    const url = this.API_URL + 'tasks-sheet1/prosite_pattern/cgi_wrapper.py';

    const payload = {fasta: file, pattern: pattern, webId: webId, external: external};

    return this.postTextRequest(url, payload);
  }

  public dna2aa(fasta: string, features: string) {
    const url = this.API_URL + 'tasks-sheet1/dna2rna/cgi_wrapper.py';

    const payload = {fasta: fasta, features: features};

    return this.postTextRequest(url, payload);
  }

  public genomeReport(keywords: string[]) {
    const url = this.API_URL + 'tasks-sheet1/genome_report/cgi_wrapper.py';

    return this.postTextRequest(url, {keywords: keywords.join(',')});
  }

  public homstrad(pdb_id: string, pdb_id2: string) {
    const url = this.API_URL + 'tasks-sheet2/homstrad/cgi_wrapper.py?pdb_id=' + pdb_id + '&pdb_id2=' + pdb_id2;

    return this.getRequest(url);
  }

  public alignmentCalc(parameters: string[]): Promise<any> {
    let url = this.API_URL + 'tasks-sheet3/alignment_website/getCandidateAlignment.py';
    const payload = {
      "seq1": parameters[0],
      "seq2": parameters[1],
      "subMatrix": parameters[2],
      "go": parameters[3],
      "ge": parameters[4],
      "alignMode": parameters[5],
      "alignAlgo": parameters[6],
    };
    console.log(url)
    return this.postRequest(url, payload)
  }

  public referenceCalc(candidate1: string, candidate2: string, reference1: string, reference2: string): Promise<any> {
    let url = this.API_URL + 'tasks-sheet3/alignment_website/getReferenceAlignment.py';
    const payload = {
      "alignCandidate1": candidate1,
      "alignCandidate2": candidate2,
      "alignReference1": reference1,
      "alignReference2": reference2
    };
    return this.postRequest(url, payload)
  }


  public createFixpoint(subMatrix: string, go: string, ge: string, alignMode: string, midpoint: string, alignAlgo: string) {
    let url = this.API_URL + 'tasks-sheet3/alignment_website/generateFixpoint.py';
    const payload = {
      'subMatrix': subMatrix,
      'go': go,
      'ge': ge,
      'alignMode': alignMode,
      'midpoint': midpoint,
      'alignAlgo': alignAlgo,
    };
    this.postRequest(url, payload)
  }

  public getSequenceFromDB(id: string): Promise<any> {
    const url = this.API_URL + 'tasks-sheet2/database/cgi_wrapper.py?id=' + id + "&source=protein_seq";
    return this.getRequest(url)
  }

  public getReferenceStructureFromDB(id: string): Promise<any> {
    const url = this.API_URL + 'tasks-sheet2/database/cgi_wrapper.py?id=' + id + "&source=protein_seq&attr=db_key,psequence,secondary_structure";
    return this.getRequest(url)
  }

  public runGOR(fasta: string, model: string) {

  }

  public getSecLib(ids: string[], input_file:string): Promise<any> {
    const payload = {
      "ids": ids,
      "input_path": input_file
    }
    return this.postTextRequest(this.API_URL + 'tasks-sheet3/SecondaryStructurePrediction/src/main/python/cgi_dssp.py', payload)
  }

  public getModelFile(version: string, file: string): Promise<any> {
    const url = this.API_URL + 'tasks-sheet3/SecondaryStructurePrediction/src/main/python/cgi_train.py';
    const payload = {
      "db": file,
      "version": version
    }
    return this.postTextRequest(url, payload);
  }

  private async getRequest(request: string): Promise<any> {
    try {
      return lastValueFrom(this.http.get<any>(request))
    } catch (error) {
      console.log(error);
      return
    }
  }

  private async getTextRequest(request: string): Promise<string | undefined> {
    try {
      return lastValueFrom(this.http.get(request, {responseType: 'text'}))
    } catch (error) {
      console.log(error);
      return
    }
  }

  private postRequest(request: string, payload: any): any {
    try {
      return lastValueFrom(this.http.post<any>(request, payload))
    } catch (error) {
      console.log(error);
      return
    }
  }

  private postTextRequest(request: string, payload: any): any {
    try {
      return lastValueFrom(this.http.post(request, payload, {responseType: 'text'}))
    } catch (error) {
      return
    }
  }
}
