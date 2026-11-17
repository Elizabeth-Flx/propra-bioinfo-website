import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PredictionVisualComponent } from './prediction-visual.component';

describe('PredictionVisualComponent', () => {
  let component: PredictionVisualComponent;
  let fixture: ComponentFixture<PredictionVisualComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PredictionVisualComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PredictionVisualComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
