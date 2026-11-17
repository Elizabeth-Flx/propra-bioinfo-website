import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectSequenceComponent } from './select-sequence.component';

describe('SelectSequenceComponent', () => {
  let component: SelectSequenceComponent;
  let fixture: ComponentFixture<SelectSequenceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelectSequenceComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectSequenceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
