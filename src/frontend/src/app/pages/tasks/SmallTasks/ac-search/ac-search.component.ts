import {Component} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {BackendService} from "../../../../services/backend.service";
import {Subject} from "rxjs";
import {DownloadService} from "../../../../services/download.service";

@Component({
  selector: 'app-ac-search',
  templateUrl: './ac-search.component.html',
  styleUrls: ['./ac-search.component.scss']
})
export class AcSearchComponent {
  form: FormGroup = new FormGroup({
    accession: new FormControl('')
  })
  $fasta: Subject<string> = new Subject<string>();

  constructor(private backendService: BackendService, private downloadService: DownloadService) {

  }

  submit() {
    let accession = this.form.get('accession')?.value;
    this.backendService.ac_search(accession).then((data) => {
      this.$fasta.next(data);
    });
  }

  download(value: string, accession: string) {
    this.downloadService.downloadFile(value, accession + '.fasta');
  }
}
