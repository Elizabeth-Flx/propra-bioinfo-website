import {Component} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {Subject} from "rxjs";
import {BackendService} from "../../../../services/backend.service";
import {DownloadService} from "../../../../services/download.service";

@Component({
  selector: 'app-prosite',
  templateUrl: './prosite.component.html',
  styleUrls: ['./prosite.component.scss']
})
export class PrositeComponent {
  form: FormGroup = new FormGroup({
    fasta: new FormControl(''),
    pattern: new FormControl(''),
    webId: new FormControl(''),
    extern: new FormControl(false)
  })

  fileContents: string = '';

  $source: Subject<string> = new Subject<string>();
  $output: Subject<string> = new Subject<string>();

  constructor(private backendService: BackendService, private downloadService: DownloadService) {
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
    this.backendService.prosite_pattern(this.fileContents, this.form.get('pattern')?.value, this.form.get('webId')?.value, this.form.get('extern')?.value).then((data: string) => {
      this.$output.next(data);
    });
  }

  download(content: string) {
    this.downloadService.downloadFile(content, 'prosite.txt');
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.$source.next('use-pattern');
    });
  }
}
