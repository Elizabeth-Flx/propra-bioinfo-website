import {Component} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {Subject} from "rxjs";
import {BackendService} from "../../../../services/backend.service";
import {DownloadService} from "../../../../services/download.service";

@Component({
  selector: 'app-dna2rna',
  templateUrl: './dna2rna.component.html',
  styleUrls: ['./dna2rna.component.scss']
})
export class Dna2rnaComponent {
  form: FormGroup = new FormGroup({
    fasta: new FormControl(''),
    features: new FormControl('')
  })

  fasta_contents: string = '';
  features_contents: string = '';

  $source: Subject<string> = new Subject<string>();
  $output: Subject<string> = new Subject<string>();

  constructor(private backendService: BackendService, private downloadService: DownloadService) {
  }

  submit() {
    this.backendService.dna2aa(this.fasta_contents, this.features_contents).then(
      (data: string) => {
        this.$output.next(data);
      });
  }

  download(value: string, accession: string) {
    this.downloadService.downloadFile(value, accession + '.fasta');
  }

  fastaChange(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.fasta_contents = e.target.result;
    };
    reader.readAsText(file);
  }

  featuresChange(event: any) {
    const file = event.target.files[0];
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.features_contents = e.target.result;
    };
    reader.readAsText(file);
  }


  ngAfterViewInit(): void {
    setTimeout(() => {
      this.$source.next('use-pattern');
    });
  }
}
