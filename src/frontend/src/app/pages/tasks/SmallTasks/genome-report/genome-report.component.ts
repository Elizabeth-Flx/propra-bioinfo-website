import {Component} from '@angular/core';
import {Subject} from "rxjs";
import {BackendService} from "../../../../services/backend.service";
import {DownloadService} from "../../../../services/download.service";
import {FormControl, FormGroup} from "@angular/forms";
import {MatChipInputEvent} from "@angular/material/chips";

@Component({
  selector: 'app-genome-report',
  templateUrl: './genome-report.component.html',
  styleUrls: ['./genome-report.component.scss']
})
export class GenomeReportComponent {
  form: FormGroup = new FormGroup({
    keywords: new FormControl([])
  })
  $output: Subject<string> = new Subject<string>();

  constructor(private backendService: BackendService, private downloadService: DownloadService) {
  }

  getKeywords() {
    return this.form.get('keywords')?.value;
  }

  remove(keyword: string) {
    let keywords = this.getKeywords();
    let index = keywords.indexOf(keyword);
    if (index >= 0) {
      keywords.splice(index, 1);
    }
  }

  submit() {
    this.backendService.genomeReport(this.getKeywords()).then((data: string) => {
      this.$output.next(data);
    });
  }

  download(data: string, filename: string) {
    this.downloadService.downloadFile(data, filename);
  }

  add(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value && this.getKeywords().indexOf(value) < 0) {
      this.getKeywords().push(value);
    }

    event.chipInput!.clear();
  }
}
