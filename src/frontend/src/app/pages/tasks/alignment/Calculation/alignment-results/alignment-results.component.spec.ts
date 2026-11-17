import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlignmentResultsComponent } from './alignment-results.component';

describe('AlignmentResultsComponent', () => {
  let component: AlignmentResultsComponent;
  let fixture: ComponentFixture<AlignmentResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AlignmentResultsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlignmentResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
