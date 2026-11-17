import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlignmentReferenceSelectComponent } from './alignment-reference-select.component';

describe('AlignmentReferenceSelectComponent', () => {
  let component: AlignmentReferenceSelectComponent;
  let fixture: ComponentFixture<AlignmentReferenceSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AlignmentReferenceSelectComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlignmentReferenceSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
