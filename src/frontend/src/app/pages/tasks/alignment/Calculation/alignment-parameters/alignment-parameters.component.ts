import {Component} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {AlignmentService} from 'src/app/services/alignment.service';

@Component({
  selector: 'app-alignment-parameters',
  templateUrl: './alignment-parameters.component.html',
  styleUrls: ['./alignment-parameters.component.scss']
})
export class AlignmentParametersComponent {

  form: FormGroup = new FormGroup({
    alignAlgo: new FormControl("nwsw"),
    alignMode: new FormControl("global"),
    subMatrix: new FormControl("bc"),
    go: new FormControl('-12'),
    ge: new FormControl('-1'),
  })

  constructor(private alignmentService: AlignmentService) {
  }

  public submit() {
    this.alignmentService.setParameters(
      this.form.get('alignAlgo')?.value,
      this.form.get('alignMode')?.value,
      this.form.get('subMatrix')?.value,
      this.form.get('go')?.value,
      this.form.get('ge')?.value
    )
    this.alignmentService.runAlignment()
  }


}
