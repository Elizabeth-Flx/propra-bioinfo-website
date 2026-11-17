import {Component} from '@angular/core';
import {FormControl, FormGroup} from "@angular/forms";
import {BackendService} from "../../../../services/backend.service";
import {DownloadService} from "../../../../services/download.service";
import {Subject} from "rxjs";

export interface AlignmentSequence {
  alignment: string;
  secondary: string;
  code: string;
}

export interface Family {
  name: string;
  alignment: AlignmentSequence[];
}

@Component({
  selector: 'app-homstrad',
  templateUrl: './homstrad.component.html',
  styleUrls: ['./homstrad.component.scss']
})

export class HomstradComponent {
  form: FormGroup = new FormGroup({
    pdb_id: new FormControl(''),
    pdb_id2: new FormControl('')
  })

  secondaryStyleClass = new Map<string, string>([
    ['H', 'secondary-helix'],
    ['E', 'secondary-sheet'],
    ['C', 'secondary-coil'],
    ['P', 'secondary-p'],
    [' ', 'secondary-none'],
  ]);

  $result: Subject<Family[]> = new Subject<Family[]>();

  constructor(private backendService: BackendService, private downloadService: DownloadService) {
  }

  has_pdb_id() {
    return this.form.get('pdb_id')?.value.length > 0;
  }

  submit() {
    let pdb_id = this.form.get('pdb_id')?.value;
    let pdb_id2 = this.form.get('pdb_id2')?.value;
    this.backendService.homstrad(pdb_id, pdb_id2).then((data) => {
      this.$result.next(data);
      console.log(data);
    });
  }

  getLetters(alignmentSeq: AlignmentSequence) {
    const letters: { sequence: string, styleClass: string }[] = [];
    const alignment = alignmentSeq.alignment;
    const secondary = alignmentSeq.secondary;

    for (let i = 0; i < alignment.length; i++) {
      letters.push({
        sequence: alignment[i],
        styleClass: (this.secondaryStyleClass.has(secondary[i]) ? this.secondaryStyleClass.get(secondary[i]) : this.secondaryStyleClass.get(' '))!
      });
    }
    return letters;
  }

  download(families: Family[]) {
    let content = '';
    for (let family of families) {
      content += `> ${family.name}\n`;
      for (let alignmentSeq of family.alignment) {
        content += `${alignmentSeq.code}\t${alignmentSeq.alignment}\t${alignmentSeq.secondary}\n`;
      }
    }
    this.downloadService.downloadFile(content, 'homstrad.txt');
  }
}

