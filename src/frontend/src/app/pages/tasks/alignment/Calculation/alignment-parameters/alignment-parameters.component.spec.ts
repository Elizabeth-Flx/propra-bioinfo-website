import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlignmentParametersComponent } from './alignment-parameters.component';

describe('AlignmentParametersComponent', () => {
  let component: AlignmentParametersComponent;
  let fixture: ComponentFixture<AlignmentParametersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AlignmentParametersComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlignmentParametersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
