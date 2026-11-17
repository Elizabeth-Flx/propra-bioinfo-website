import {Component} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {MatTabChangeEvent} from "@angular/material/tabs";
import {AlignmentService} from 'src/app/services/alignment.service';
import {BackendService} from 'src/app/services/backend.service';


@Component({
  selector: 'app-alignment-reference-select',
  templateUrl: './alignment-reference-select.component.html',
  styleUrls: ['./alignment-reference-select.component.scss']
})
export class AlignmentReferenceSelectComponent {

  fileContents: string = "";
  form: FormGroup = new FormGroup({
    dbID1: new FormControl(''),
    dbID2: new FormControl(''),
    plain1: new FormControl(''),
    plain2: new FormControl(''),
    fasta: new FormControl(''),
    overwrite: new FormControl(false),
  })
  private currTab = 'Get from our Database';

  constructor(private alignmentService: AlignmentService, private backendService: BackendService) {
  }

  public onSelectedTabChange(matTabChangeEvent: MatTabChangeEvent) {
    this.currTab = matTabChangeEvent.tab.textLabel;
    console.log(this.currTab)
  }

  public submit() {
    console.log(this.form.get('dbID1')?.value)
    console.log(this.form.get('dbID2')?.value)
    console.log(this.form.get('plain1')?.value)
    console.log(this.form.get('plain2')?.value)
    console.log(this.form.get('fasta'))
    console.log(this.fileContents)
    console.log(this.form.get('overwrite')?.value)
    console.log(window.innerWidth)
    console.log(document.getElementById('test')?.clientWidth)

    this.alignmentService.runAlignment()
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


}
