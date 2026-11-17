import {Component} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {Options} from "../../../../../interfaces";
import {Subject} from "rxjs";
import {BackendService} from "../../../../services/backend.service";
import {DownloadService} from "../../../../services/download.service";


@Component({
  selector: 'app-create-model-file',
  templateUrl: './create-model-file.component.html',
  styleUrls: ['./create-model-file.component.scss']
})
export class CreateModelFileComponent {
  training_set: string = '';

  fileContents: string = '';

  version: string = '';
  $file: Subject<string> = new Subject<string>();
  form: FormGroup = new FormGroup({
    training_set: new FormControl(''),
    version: new FormControl('')
  })

  constructor(private backendService: BackendService, private downloadService: DownloadService) {
    this.$file.subscribe(data => this.training_set = data);
  }

  fileChange(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.fileContents = e.target.result;
    };
    reader.readAsText(file);
  }

  submit() {
    this.$file.next(this.fileContents);
    this.version = this.form.get('version')?.value;
    this.backendService.getModelFile(this.version, this.training_set).then((data) => this.downloadService.downloadFile(data, 'model_file.txt'));
  }

  models: Options[] = [
    {value: 'gor1', viewValue: 'GOR I'},
    {value: 'gor2', viewValue: 'GOR III'},
    {value: 'gor3', viewValue: 'GOR IV'},
  ]
}
