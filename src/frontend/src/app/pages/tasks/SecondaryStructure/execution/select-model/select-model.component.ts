import {Component, Input} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {Options} from "../../../../../../interfaces";
import {Subject} from "rxjs";

@Component({
  selector: 'app-select-model',
  templateUrl: './select-model.component.html',
  styleUrls: ['./select-model.component.scss']
})
export class SelectModelComponent {
  form: FormGroup = new FormGroup({
    model_file: new FormControl(''),
    name: new FormControl('')
  })
  models: Options[] = [
    {value: 'cb513-1', viewValue: 'CB513 GOR I'},
    {value: 'cb513-3', viewValue: 'CB513 GOR III'},
    {value: 'cb513-4', viewValue: 'CB513 GOR IV'},
  ]
  @Input() $model!: Subject<string>;
  @Input() $usePretrained!: Subject<boolean>;

  fileChange(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.$usePretrained.next(false);
      this.$model.next(e.target.result);
    };
    reader.readAsText(file);
  }

  modelChange(value: string) {
    this.$usePretrained.next(true);
    this.$model.next(value);
  }
}
