import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Summary} from "../../../../../../interfaces";

@Component({
  selector: 'app-prediction-summary',
  templateUrl: './prediction-summary.component.html',
  styleUrls: ['./prediction-summary.component.scss']
})
export class PredictionSummaryComponent {
  constructor(
    public dialogRef: MatDialogRef<PredictionSummaryComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Summary,
  ) {
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
