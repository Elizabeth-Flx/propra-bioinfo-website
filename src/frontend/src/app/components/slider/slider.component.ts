import {Component, Input} from '@angular/core';
import {Subject} from "rxjs";

@Component({
  selector: 'app-slider',
  templateUrl: './slider.component.html',
  styleUrls: ['./slider.component.scss']
})
export class SliderComponent {
  @Input() valueName?: string;
  @Input() $subject!: Subject<number>;
  @Input() min: number = 0;
  @Input() max: number = 100;
  value = 0;

  ngAfterViewInit() {
    this.$subject.subscribe((value) => {
      this.value = value;
    });
  }
}
