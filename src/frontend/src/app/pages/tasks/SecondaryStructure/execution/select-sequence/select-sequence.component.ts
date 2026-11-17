import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {Subject} from "rxjs";
import {BackendService} from "../../../../../services/backend.service";
import {MatTabChangeEvent} from "@angular/material/tabs";

@Component({
  selector: 'app-select-sequence',
  templateUrl: './select-sequence.component.html',
  styleUrls: ['./select-sequence.component.scss']
})
export class SelectSequenceComponent {
  form: FormGroup = new FormGroup({
    accession: new FormControl(''),
    pasted: new FormControl(''),
    fasta: new FormControl(''),
    seqfromid: new FormControl(''),
    seqfromuniprot: new FormControl('')
  })
  fileContents: string = '';
  @Input()
  $fasta!: Subject<string>;
  private currentTab = 'Get from Uniprot';

  constructor(private backendService: BackendService) {
  }

  submit() {
    if (this.currentTab == 'Get from our Database') {
      this.backendService.getSequenceFromDB(this.form.get('seqfromid')?.value).then((data) => this.$fasta.next(data));
    } else if (this.currentTab == 'Get from Uniprot') {
      let accession = this.form.get('seqfromuniprot')?.value;
      this.backendService.ac_search(accession)?.then((data) => {
        this.$fasta.next(data);
      });
    } else if (this.currentTab == 'Paste sequences as text') {
      this.$fasta.next(this.form.get('pasted')?.value)
    } else if (this.currentTab == 'Upload sequences as fasta') {
      this.$fasta.next(this.fileContents)
    }
  }

  fileChange(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.fileContents = e.target.result;
    };
    reader.readAsText(file);
  }

  public onSelectedTabChange(matTabChangeEvent: MatTabChangeEvent) {
    this.currentTab = matTabChangeEvent.tab.textLabel;
  }
}
