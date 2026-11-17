import {Component} from '@angular/core';
import {AlignmentService} from 'src/app/services/alignment.service';
import { BackendService } from 'src/app/services/backend.service';
import { DownloadService } from 'src/app/services/download.service';

@Component({
  selector: 'app-alignment-results',
  templateUrl: './alignment-results.component.html',
  styleUrls: ['./alignment-results.component.scss']
})
export class AlignmentResultsComponent {

  constructor(public alignmentService: AlignmentService, private downloadService: DownloadService, private backendService: BackendService) {
  }

  highlightCandidate = false;
  highlightReference = false;

  showScore = false;
  showDeletion = false;
  showInsertion = false;

  baseImageUrl = "http://bioclient1.bio.ifi.lmu.de/~trummer/propra/src/backend/tasks-sheet3/alignment_website/heatmap.png"
  imageUrl = this.baseImageUrl

  counter = 1

  generateFixpoint() {
    this.alignmentService.generateFixpoint()
    //this.imageUrl = this.baseImageUrl + "?version=2"
  }
  showFixpoint() {
    window.open("http://bioclient1.bio.ifi.lmu.de/~trummer/propra/src/backend/tasks-sheet3/alignment_website/heatmap.png", '_blank')
  }

  toggleScore() {
    this.showScore = !this.showScore
  }
  toggleDeletion() {
    this.showDeletion = !this.showDeletion
  }
  toggleInsertion() {
    this.showInsertion = !this.showInsertion
  }

  toggleCandidate() {
    this.highlightCandidate = !this.highlightCandidate;
  }
  toggleReference() {
    this.highlightReference = !this.highlightReference;
  }

  getAlgo(): string {
    if (this.alignmentService.alignAlgo == "gotoh") {
      return "Gotoh"
    } else if (this.alignmentService.alignType == "local") {
      return "Smith–Waterman"
    } else if (this.alignmentService.alignAlgo == "nwsw") {
      return "Needleman-Wunsch"
    } else {
      return "None Selected"
    }
  }

  getType(): string {
    if (this.alignmentService.alignType == "global") {
      return "Global"
    } else if (this.alignmentService.alignType == "local") {
      return "Local"
    } else if (this.alignmentService.alignType == "freeshift") {
      return "Freeshift"
    } else {
      return "None Selected"
    }
  }

  getSubMatrix(): string {
    if (this.alignmentService.subMatrix == "bc") {
      return "Blake Cohen"
    } else if (this.alignmentService.subMatrix == "blos") {
      return "BLOSUM62"
    } else if (this.alignmentService.subMatrix == "dayh") {
      return "Dayhoff"
    } else if (this.alignmentService.subMatrix == "pam"){
      return "PAM250"
    } else {
      return "None Selected"
    }
  }

  public isTraceback(i: number, j: number): boolean {
    return this.alignmentService.traceback.some(index => index[0] === i && index[1] === j);
  }

  downloadScore() {
    let matrix: string = ""
    for (let i = 0; i < this.alignmentService.tracebackMatrix.length; i++) {
      for (let j = 0; j < this.alignmentService.tracebackMatrix[i].length; j++) {
        matrix += (this.alignmentService.tracebackMatrix[i][j] / 100.0).toString() + " "
      }
      matrix += "\n" 
    }
    this.downloadService.downloadFile(matrix, "scoreMatrix.txt")
  }
  downloadInsertion() {
    let matrix: string = ""
    for (let i = 0; i < this.alignmentService.insertionMatrixGotoh.length; i++) {
      for (let j = 0; j < this.alignmentService.insertionMatrixGotoh[i].length; j++) {
        matrix += (this.alignmentService.insertionMatrixGotoh[i][j] / 100.0).toString() + " "
      }
      matrix += "\n" 
    }
    this.downloadService.downloadFile(matrix, "insertionMatrix.txt")
  }
  downloadDeletion() {
    let matrix: string = ""
    for (let i = 0; i < this.alignmentService.deletionMatrixGotoh.length; i++) {
      for (let j = 0; j < this.alignmentService.deletionMatrixGotoh[i].length; j++) {
        matrix += (this.alignmentService.deletionMatrixGotoh[i][j] / 100.0).toString() + " "
      }
      matrix += "\n" 
    }
    this.downloadService.downloadFile(matrix, "deletionMatrix.txt")
  }

  downloadCandidate() {
    let align = ""
    align += this.alignmentService.align1 + "\n"
    align += this.alignmentService.alignMatches.join('') + "\n"
    align += this.alignmentService.align2 + "\n"
    this.downloadService.downloadFile(align, "candidateAlignment.txt")
  }

  downloadReference() {
    let align = ""
    align += this.alignmentService.ref1 + "\n"
    align += this.alignmentService.referenceMatches.join('') + "\n"
    align += this.alignmentService.ref2 + "\n"
    this.downloadService.downloadFile(align, "referenceAlignment.txt")
  }
}