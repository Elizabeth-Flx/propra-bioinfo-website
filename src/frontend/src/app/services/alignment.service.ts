import { Injectable } from '@angular/core';
import { BackendService } from './backend.service';

@Injectable({
  providedIn: 'root'
})
export class AlignmentService {

  constructor(private backendService: BackendService) { }

  seq1: string = '';
  seq2: string = '';

  alignAlgo: string = '';
  alignType: string = '';

  subMatrix: string = '';
  go: number = 0;
  ge: number = 0;

  ref1: string = '';
  ref2: string = '';

  align1: string = '';
  align2: string = '';

  alignSplit1: string[] = []; 
  alignSplit2: string[] = []; 
  alignMatches: string[] = []; 

  score: number = 0;
  gapsOpenend1: any = 0;
  gapsOpenend2: any = 0;
  totalMatches: number = 0;
  correctMatches: number = 0;
  
  tracebackMatrix: number[][] = []

  insertionMatrixGotoh: number[][] = []
  deletionMatrixGotoh:  number[][] = []

  traceback: number[][] = [[0,0]]

  referenceSplit1: string[] = []; 
  referenceSplit2: string[] = [];
  referenceMatches: string[] = [];

  sensitivity: number = 0
  specificity: number = 0
  coverage: number = 0
  MSE: number = 0
  invMSE: number = 0


  showImage = false;


  public resetAlignmentVals() {
    this.align1 = ''
    this.align2 = ''
  
    this.alignSplit1 = []
    this.alignSplit2 = []
    this.alignMatches = []
  
    this.score = 0;
    this.gapsOpenend1 = 0;
    this.gapsOpenend2 = 0;
    this.totalMatches = 0;
    this.correctMatches = 0;
    
    this.tracebackMatrix = []
  
    this.insertionMatrixGotoh = []
    this.deletionMatrixGotoh = []
  
    this.traceback = [[0,0]]
  }
  public resetRefernceVals() {
    // this.ref1 = ""
    // this.ref2 = ""
    this.referenceSplit1 = []
    this.referenceSplit2 = []
    this.referenceMatches = []
    this.sensitivity = 0
    this.specificity = 0
    this.coverage = 0
    this.MSE = 0
    this.invMSE = 0
  }



  public runAlignment() {
    this.backendService.alignmentCalc(this.getParameters()).then((data) => {

      this.resetAlignmentVals()
      this.resetRefernceVals()
      console.log(data);

      this.align1 = data["align1"];
      this.align2 = data["align2"];
      this.score  = data["score"];
      
      if ("scoreMatrixNWSW" in data) while(data['scoreMatrixNWSW'].length) this.tracebackMatrix.push(data["scoreMatrixNWSW"].splice(0,this.seq2.length+1));

      if ("scoreMatrixGotoh"      in data) while(data['scoreMatrixGotoh']     .length) this.tracebackMatrix.      push(data["scoreMatrixGotoh"].splice(0,this.seq2.length+1));
      if ("insertionMatrixGotoh"  in data) while(data['insertionMatrixGotoh'] .length) this.insertionMatrixGotoh. push(data["insertionMatrixGotoh"].splice(0,this.seq2.length+1));
      if ("deletionMatrixGotoh"   in data) while(data['deletionMatrixGotoh']  .length) this.deletionMatrixGotoh.  push(data["deletionMatrixGotoh"].splice(0,this.seq2.length+1));

      console.log(this.tracebackMatrix)
      console.log(this.insertionMatrixGotoh)
      console.log(this.deletionMatrixGotoh)


      for (let i = 0; i < this.align1.length; i++) {

        if (this.align1[i] != '-' && this.align2[i] != '-') {
          this.traceback.push([this.traceback[this.traceback.length - 1][0] + 1, this.traceback[this.traceback.length - 1][1] + 1])
        } else if (this.align1[i] != '-') {
          this.traceback.push([this.traceback[this.traceback.length - 1][0] + 1, this.traceback[this.traceback.length - 1][1]])
        } else if (this.align2[i] != '-') {
          this.traceback.push([this.traceback[this.traceback.length - 1][0], this.traceback[this.traceback.length - 1][1] + 1])
        }

        if (this.align1[i] != '-' && this.align2[i] != '-') {
          this.totalMatches++;
          if (this.align1[i] == this.align2[i]) {
            this.correctMatches++;
          }
        }
      }


      this.splitCandidateAlignment()
      this.splitReferenceAlignment()
      
      this.gapsOpenend1 = this.align1.match(/^-|[^-]-/g)?.length || 0
      this.gapsOpenend2 = this.align2.match(/^-|[^-]-/g)?.length || 0

      this.generateFixpoint()

      if (this.ref1 != '') {
        this.backendService.referenceCalc(this.align1, this.align2, this.ref1, this.ref2).then((data) => {
          console.log(data)
          this.sensitivity = data["sensitivity"]
          this.specificity = data["specificity"]
          this.coverage = data["coverage"]
          this.MSE = data["MSE"]
          this.invMSE = data["invMSE"]
          
          console.log(this.sensitivity)
          console.log(this.specificity)
          console.log(this.coverage)
          console.log(this.MSE)
          console.log(this.invMSE)

        });
      }
    });
  }

  public splitCandidateAlignment() {
    this.alignSplit1 = []
    this.alignSplit2 = []
    for (let i = 0; i < this.align1.length; i += 80) {
      this.alignSplit1.push(this.align1.substring(i, i + 80))
      this.alignSplit2.push(this.align2.substring(i, i + 80))
      this.alignMatches.push(this.generateMatchString(this.align1.substring(i, i + 80), this.align2.substring(i, i + 80)))
    }
  }

  public splitReferenceAlignment() {
    this.referenceSplit1 = []
    this.referenceSplit2 = []
    for (let i = 0; i < this.ref1.length; i += 80) {
      this.referenceSplit1.push(this.ref1.substring(i, i + 80))
      this.referenceSplit2.push(this.ref2.substring(i, i + 80))
      this.referenceMatches.push(this.generateMatchString(this.ref1.substring(i, i + 80), this.ref2.substring(i, i + 80)))
    }
  }

  public setSequences(seq1: string, seq2: string) {
    this.ref1 = ""
    this.ref2 = ""
    if (seq1.includes('-') || seq2.includes('-')) {
      this.ref1 = seq1;
      this.ref2 = seq2;
    }

    this.seq1 = seq1.replace(/-/g, "");
    this.seq2 = seq2.replace(/-/g, "");

    console.log(this.seq1)
    console.log(this.seq2)

    console.log(this.ref1)
    console.log(this.ref2)
  }

  public setParameters(alignAlgo: string, alignType: string, subMatrix: string, go: number, ge: number) {
    this.alignAlgo = alignAlgo;
    this.alignType = alignType;
    this.subMatrix = subMatrix;
    this.go = go;
    this.ge = ge;
  }

  public getParameters(): any[] {
    return [
      this.seq1,
      this.seq2,
      this.subMatrix,
      this.go,
      this.ge,
      this.alignType,
      this.alignAlgo
    ];
  }

  public generateFixpoint() {
    this.backendService.createFixpoint(this.subMatrix, this.go.toString(), this.ge.toString(), this.alignType, "0.85", "gotoh")
    //this.showImage = true
    //window.open("http://bioclient1.bio.ifi.lmu.de/~trummer/propra/src/backend/tasks-sheet3/alignment_website/heatmap.png", '_blank')

  }

  // THIS IS TERRIBLE 
  positiveBC =     ["NR","DN","QA","QR","QN","QD","ER","EN","ED","EQ","GA","GN","GD",
                    "HR","HN","HD","HQ","LI","KR","KN","KQ","KE","KH","MI","ML","FI",
                    "FL","PD","PL","PK","SR","SN","SK","SP","TN","TQ","TE","TK","TS",
                    "WL","WF","YN","YH","YF","YT","VI","VL","VM","VF"];
  positiveBLOS   = ["DN","QR","ED","EQ","HN","HQ","LI","KR","KQ","KE","MI","ML","FL",
                    "SA","SN","TS","WF","YH","YF","YW","VI","VL","VM"];
  positiveDAYH =   ["NA","DA","DN","QR","QN","QD","EA","EN","ED","EQ","GA","GN","GD",
                    "GE","HR","HN","HD","HQ","HE","LI","KR","KN","KD","KQ","MI","ML",
                    "MK","FI","FL","FM","PA","PQ","SA","SN","SD","SG","SP","TA","TN",
                    "TI","TP","TS","WR","WF","YC","YF","VA","VI","VL","VM","VT"];
  positivePAM =    ["DN","QR","QN","QD","EN","ED","EQ","GA","GD","HR","HN","HD","HQ",
                    "HE","LI","KR","KN","KQ","MI","ML","FI","FL","PA","SA","SN","SG",
                    "SP","TA","TS","WR","YF","VI","VL","VM"];

  public generateMatchString(align1: string, align2: string) {
    let match = ``
    let curMatrix: string[] = []
    if      (this.subMatrix == "bc")   curMatrix = this.positiveBC
    else if (this.subMatrix == "blos") curMatrix = this.positiveBLOS
    else if (this.subMatrix == "dayh") curMatrix = this.positiveDAYH
    else if (this.subMatrix == "pam")  curMatrix = this.positivePAM


    for (let i = 0; i < align1.length; i++) {

      if (align1[i] == '-' || align2[i] == '-') {
        match += " "
      } else if (align1[i] == align2[i]) {
        match += "|"
      } else if (curMatrix.includes(align1[i] + align2[i]) || curMatrix.includes(align2[i] + align1[i])) {
        match += ":"
      } else {
        match += "."
      }
    }
    return match
  }


}
