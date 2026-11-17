import {Component} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {MatTabChangeEvent} from '@angular/material/tabs';
import {AlignmentService} from 'src/app/services/alignment.service';
import { BackendService } from 'src/app/services/backend.service';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'app-alignment-sequence-select',
  templateUrl: './alignment-sequence-select.component.html',
  styleUrls: ['./alignment-sequence-select.component.scss']
})
export class AlignmentSequenceSelectComponent {

  tmpVal = 'hello'
  fileContents: string = ''
  form: FormGroup = new FormGroup({
    homstradFamily: new FormControl(''),
    homstradID1: new FormControl(''),
    homstradID2: new FormControl(''),
    swissID1: new FormControl(''),
    swissID2: new FormControl(''),
    plain1: new FormControl(''),
    plain2: new FormControl('')
  })
  //private currTab = 'Get from Homstrad';
  private currTab = 'Get from Swissprot';

  constructor(private alignmentService: AlignmentService, private backendService: BackendService, private http: HttpClient) {
  }

  public onSelectedTabChange(matTabChangeEvent: MatTabChangeEvent) {
    this.currTab = matTabChangeEvent.tab.textLabel;
    console.log(this.currTab)
  }

  fileChange(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.fileContents = e.target.result;
    };
    reader.readAsText(file);
    console.log(this.fileContents)
  }

  getFirstTwoSequences(fastaContent: string): string[] {
    const sequences: string[] = [];
    let currentSequence = '';

    // Split the fasta content into an array of lines
    const lines = fastaContent.split('\n');

    // Loop through each line
    for (const line of lines) {
      // Check if the line is a sequence header
      if (line.startsWith('>')) {
        // If we already have two sequences, break out of the loop
        if (sequences.length === 2) {
          break;
        }

        // If we have a current sequence, add it to the sequences array
        if (currentSequence !== '') {
          sequences.push(currentSequence);
          currentSequence = '';
        }
      } else {
        // If the line is not a header, add it to the current sequence
        currentSequence += line.trim();
      }
    }

    // Add the last sequence to the sequences array
    if (currentSequence !== '') {
      sequences.push(currentSequence);
    }

    // Return the first two sequences
    return sequences.slice(0, 2);
  }


  submit() {
    if (this.currTab == "Get from Homstrad") {

      let family = this.form.get('homstradFamily')?.value
      let url = 'https://files.rcsb.org/download/2YGU.pdb'
      
      this.http.get(url, { responseType: 'text' }).subscribe((response: string) => {
        console.log(response);  
        let records = response.split('\n').filter(line => line.startsWith('ATOM'));
        console.log(records)


      });

    } else if (this.currTab == "Get from Swissprot") {
    let id = this.form.get('swissID1')?.value;
    this.backendService.ac_search(id).then((data1) => {

      let id = this.form.get('swissID2')?.value;
      this.backendService.ac_search(id).then((data2) => {

        let sequences: string[] = this.getFirstTwoSequences(data1 + "\n" + data2)
        this.alignmentService.setSequences(sequences[0], sequences[1]);
        this.alignmentService.runAlignment()

      });
    });

    } else if (this.currTab == "Paste sequences as text") {
      this.alignmentService.setSequences(this.form.get('plain1')?.value, this.form.get('plain2')?.value)
      this.alignmentService.runAlignment()
    } else if (this.currTab == "Upload sequences as fasta") {
      let sequences: string[] = this.getFirstTwoSequences(this.fileContents);
      this.alignmentService.setSequences(sequences[0], sequences[1]);
      this.alignmentService.runAlignment()
    }
  }


}




