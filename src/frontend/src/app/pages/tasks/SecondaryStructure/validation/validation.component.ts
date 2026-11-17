import {Component} from '@angular/core';
import {BackendService} from "../../../../services/backend.service";

@Component({
  selector: 'app-validation',
  templateUrl: './validation.component.html',
  styleUrls: ['./validation.component.scss']
})
export class ValidationComponent {
  API_URL: string;

  constructor(backendService: BackendService) {
    this.API_URL = backendService.API_URL;
  }
}
