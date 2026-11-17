import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {MatTabChangeEvent} from "@angular/material/tabs";
import {Subject} from "rxjs";
import {BackendService} from "../../../../../services/backend.service";

@Component({
  selector: 'app-select-reference',
  templateUrl: './select-reference.component.html',
  styleUrls: ['./select-reference.component.scss']
})
export class SelectReferenceComponent {
  form: FormGroup = new FormGroup({
    pasted: new FormControl(''),
    fasta: new FormControl(''),
    seqfromid: new FormControl('')
  })
  fileContents: string = '';
  @Input() $fasta!: Subject<string>;
  private currentTab = 'Get from our Database';

  constructor(private backendService: BackendService) {
  }

  submit() {
    if (this.currentTab == 'Get from our Database') {
      this.backendService.getReferenceStructureFromDB(this.form.get('seqfromid')?.value).then(data => {
        this.$fasta.next(data);
      });
    } else if (this.currentTab == 'Upload sequences as fasta') {
      this.$fasta.next(this.fileContents);
    } else if (this.currentTab == 'Paste sequences as text') {
      this.$fasta.next(this.form.get('pasted')?.value);
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
