import { Component } from '@angular/core';
import {BackendService} from "../../../../services/backend.service";
import {DownloadService} from "../../../../services/download.service";
import {Subject} from "rxjs";
import {FormControl, FormGroup} from "@angular/forms";
import {MatChipInputEvent} from "@angular/material/chips";

@Component({
  selector: 'app-dssp',
  templateUrl: './dssp.component.html',
  styleUrls: ['./dssp.component.scss']
})
export class DsspComponent {
  form: FormGroup = new FormGroup({
    ids: new FormControl([]),
    input_file: new FormControl('')
  })
  ids: string[] = [];
  input_file: string = '';
  $ids: Subject<string[]> = new Subject<string[]>();
  $input_file: Subject<string> = new Subject<string>();
  constructor(private backendService: BackendService, private downloadService: DownloadService) {
    this.$ids.subscribe(data => this.ids = data);
    this.$input_file.subscribe(data => this.input_file = data);
  }

  submit() {
    this.$ids.next(this.getids());
    console.log(this.form.get('input_file')?.value);
    this.$input_file.next(this.form.get('input_file')?.value);
    this.backendService.getSecLib(this.ids,this.input_file).then((data) => this.downloadService.downloadFile(data, 'output.seclib'));
  }


  remove(id: string) {
    let ids = this.getids();
    let index = ids.indexOf(id);
    if (index >= 0) {
      ids.splice(index, 1);
    }
  }

    getids() {
    return this.form.get('ids')?.value;
  }

  add(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value && this.getids().indexOf(value) < 0) {
      this.getids().push(value);
    }

    event.chipInput!.clear();
  }
}
