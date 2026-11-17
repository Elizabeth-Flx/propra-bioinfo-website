import {Component} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {MatChipInputEvent} from "@angular/material/chips";
import {BackendService} from "../../../../services/backend.service";
import {DownloadService} from "../../../../services/download.service";
import {Subject} from "rxjs";

@Component({
  selector: 'app-keyword-search',
  templateUrl: './keyword-search.component.html',
  styleUrls: ['./keyword-search.component.scss']
})
export class KeywordSearchComponent {
  form: FormGroup = new FormGroup({
    file: new FormControl(''),
    keywords: new FormControl([])
  })

  fileContents: string = '';

  $output: Subject<string[]> = new Subject<string[]>();

  constructor(private backendService: BackendService, private downloadService: DownloadService) {
  }

  getKeywords() {
    return this.form.get('keywords')?.value;
  }

  submit() {
    this.backendService.keyword_search(this.fileContents, this.getKeywords()).then((data: string[]) => {
      this.$output.next(data);
    });
  }

  fileChange(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.fileContents = e.target.result;
    };
    reader.readAsText(file);
  }

  remove(keyword: string) {
    let keywords = this.getKeywords();
    let index = keywords.indexOf(keyword);
    if (index >= 0) {
      keywords.splice(index, 1);
    }
  }

  download(data: string[], filename: string) {
    this.downloadService.downloadFile(data.join("\n"), filename);
  }

  add(event: MatChipInputEvent) {
    const value = (event.value || '').trim();

    if (value && this.getKeywords().indexOf(value) < 0) {
      this.getKeywords().push(value);
    }

    event.chipInput!.clear();
  }
}
