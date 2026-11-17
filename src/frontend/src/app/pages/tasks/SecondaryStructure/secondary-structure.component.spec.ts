import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SecondaryStructureComponent } from './secondary-structure.component';

describe('SecondaryStructureComponent', () => {
  let component: SecondaryStructureComponent;
  let fixture: ComponentFixture<SecondaryStructureComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SecondaryStructureComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SecondaryStructureComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
