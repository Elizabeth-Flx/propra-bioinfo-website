import {Component} from '@angular/core';
import {Subject} from "rxjs";
import {BackendService} from "../../../../services/backend.service";
import {Result, Summary} from "../../../../../interfaces";
import {MatDialog} from "@angular/material/dialog";
import {PredictionSummaryComponent} from "./prediction-summary/prediction-summary.component";

@Component({
  selector: 'app-execution',
  templateUrl: './execution.component.html',
  styleUrls: ['./execution.component.scss']
})
export class ExecutionComponent {
  $fasta: Subject<string> = new Subject<string>();
  $usePretrained: Subject<boolean> = new Subject<boolean>();
  $model: Subject<string> = new Subject<string>();
  $useGor5: Subject<boolean> = new Subject<boolean>();
  $reference: Subject<string> = new Subject<string>();
  fasta: string | undefined = undefined;
  usePretrained: boolean = true;
  model: string | undefined = undefined;
  reference: string | undefined = undefined;

  $result: Subject<Result | undefined> = new Subject<Result | undefined>();

  constructor(private backend: BackendService, public dialog: MatDialog) {
    this.$fasta.subscribe(data => this.fasta = data);
    this.$usePretrained.subscribe(data => this.usePretrained = data);
    this.$model.subscribe(data => this.model = data);
    this.$reference.subscribe(data => this.reference = data);
  }

  canCalculate(): boolean {
    return this.fasta !== undefined && this.model !== undefined;
  }

  calculate() {
    if (this.reference) {
      this.makePredictionWithReference(this.fasta!, this.model!, this.reference, this.usePretrained);
    } else {
      this.makePrediction(this.fasta!, this.model!, this.usePretrained);
    }
  }

  makePrediction(fasta: string, model: string, usePretrained: boolean = true) {
    if (usePretrained) {
      this.backend.predictFromPretrained(fasta, model).then(data => this.$result.next(data));
    } else {
      this.backend.predictFromModelfile(fasta, model).then(data => this.$result.next(data));
    }
  }

  makePredictionWithReference(fasta: string, model: string, reference: string, usePretrained: boolean = true) {
    if (usePretrained) {
      this.backend.predictFromPretrainedWithReference(fasta, model, reference).then(data => this.$result.next(data));
    } else {
      this.backend.predictFromModelfileWithReference(fasta, model, reference).then(data => this.$result.next(data));
    }
  }

  reset() {
    this.$result.next(undefined);
  }

  showSummary(summary: Summary) {
    this.dialog.open(PredictionSummaryComponent, {data: summary});
  }
}
