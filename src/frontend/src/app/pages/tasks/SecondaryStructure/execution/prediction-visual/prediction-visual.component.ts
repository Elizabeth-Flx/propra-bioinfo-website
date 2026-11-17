import {Component, Input} from '@angular/core';
import {Position, Prediction, Probability, Result} from "../../../../../../interfaces";
import {Subject} from "rxjs";

@Component({
  selector: 'app-prediction-visual',
  templateUrl: './prediction-visual.component.html',
  styleUrls: ['./prediction-visual.component.scss']
})
export class PredictionVisualComponent {
  @Input()
  $result!: Subject<Result | undefined>;
  resultWithColor!: Prediction[];
  aminoAcidStyleClass = new Map<string, string>([
    ['A', '#e96b62'],
    ['C', '#fc9c5c'],
    ['D', '#69B00B'],
    ['E', '#f8e184'],
    ['F', '#79c279'],
    ['G', '#1dbdf2'],
    ['H', '#8b8bdf'],
    ['I', '#a89bb6'],
    ['K', '#f8aff8'],
    ['L', '#d786ae'],
    ['M', '#ffdee4'],
    ['N', '#e4e4e4'],
    ['P', '#A9A9A9'],
    ['Q', '#F5DEB3'],
    ['R', '#b48f8f'],
    ['S', '#fff5c0'],
    ['T', '#9deaea'],
    ['V', '#a3c5c5'],
    ['W', '#d9d2e9'],
    ['Y', '#aa9393'],
    [' ', 'white'],
  ]);

  secondaryStyleClass = new Map<string, string>([
    ['H', 'secondary-helix'],
    ['E', 'secondary-sheet'],
    ['C', 'secondary-coil'],
    ['P', 'secondary-p'],
    [' ', 'secondary-none'],
  ]);

  ngAfterViewInit() {
    this.$result.subscribe((results) => {
      if (results === undefined) {
        this.resultWithColor = [];
        return;
      }

      this.resultWithColor = [];
      for (let result of results?.predictions) {
        let positions: Position[] = [];
        for (let i = 0; i < result.sequence.length; i++) {
          positions.push({
            aminoAcid: result.sequence.charAt(i),
            color: 'background-color:' + this.aminoAcidStyleClass.get(result.sequence.charAt(i))
          });
        }
        result.color = positions;
        this.resultWithColor.push(result);
      }
    });
  }

  public getProbabilitySequence(probabilities: Probability[]) {
    let probabilitySequenceH = '-'.repeat(8);
    let probabilitySequenceE = '-'.repeat(8);
    let probabilitySequenceC = '-'.repeat(8);
    for (let probability of probabilities) {
      probabilitySequenceC += Math.floor(probability.C * 10).toString();
      probabilitySequenceH += Math.floor(probability.H * 10).toString();
      probabilitySequenceE += Math.floor(probability.E * 10).toString();
    }
    probabilitySequenceC += '-'.repeat(8);
    probabilitySequenceH += '-'.repeat(8);
    probabilitySequenceE += '-'.repeat(8);

    return [probabilitySequenceH, probabilitySequenceE, probabilitySequenceC];
  }

  getLetters(secondary: string) {
    const letters: { sequence: string, styleClass: string }[] = [];

    for (let i = 0; i < secondary.length; i++) {
      letters.push({
        sequence: secondary[i],
        styleClass: (this.secondaryStyleClass.has(secondary[i]) ? this.secondaryStyleClass.get(secondary[i]) : this.secondaryStyleClass.get(' '))!
      });
    }
    return letters;

  }
}
