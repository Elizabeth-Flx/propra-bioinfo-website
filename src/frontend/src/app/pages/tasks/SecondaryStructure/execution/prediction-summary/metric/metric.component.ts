import {Component, Input} from '@angular/core';
import {Metric} from 'src/interfaces';

@Component({
  selector: 'app-metric',
  templateUrl: './metric.component.html',
  styleUrls: ['./metric.component.scss']
})
export class MetricComponent {
  @Input() metric!: Metric;
}
