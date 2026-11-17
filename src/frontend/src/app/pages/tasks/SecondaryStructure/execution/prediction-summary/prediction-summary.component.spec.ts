import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PredictionSummaryComponent } from './prediction-summary.component';

describe('PredictionSummaryComponent', () => {
  let component: PredictionSummaryComponent;
  let fixture: ComponentFixture<PredictionSummaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PredictionSummaryComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PredictionSummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
