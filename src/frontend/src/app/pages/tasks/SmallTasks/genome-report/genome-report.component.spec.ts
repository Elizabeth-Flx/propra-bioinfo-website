import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenomeReportComponent } from './genome-report.component';

describe('GenomeReportComponent', () => {
  let component: GenomeReportComponent;
  let fixture: ComponentFixture<GenomeReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GenomeReportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenomeReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
