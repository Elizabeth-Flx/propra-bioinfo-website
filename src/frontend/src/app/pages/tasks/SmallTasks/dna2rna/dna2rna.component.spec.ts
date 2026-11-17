import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Dna2rnaComponent } from './dna2rna.component';

describe('Dna2rnaComponent', () => {
  let component: Dna2rnaComponent;
  let fixture: ComponentFixture<Dna2rnaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ Dna2rnaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Dna2rnaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
