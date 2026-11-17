import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlignmentPlotsComponent } from './alignment-plots.component';

describe('AlignmentPlotsComponent', () => {
  let component: AlignmentPlotsComponent;
  let fixture: ComponentFixture<AlignmentPlotsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AlignmentPlotsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlignmentPlotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
