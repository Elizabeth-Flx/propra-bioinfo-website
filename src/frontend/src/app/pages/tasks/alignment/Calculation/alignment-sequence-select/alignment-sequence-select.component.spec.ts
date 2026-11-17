import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AlignmentSequenceSelectComponent} from './alignment-sequence-select.component';

describe('AlignmentComponent', () => {
  let component: AlignmentSequenceSelectComponent;
  let fixture: ComponentFixture<AlignmentSequenceSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AlignmentSequenceSelectComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AlignmentSequenceSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
