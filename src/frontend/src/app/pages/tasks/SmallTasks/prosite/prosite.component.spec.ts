import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrositeComponent } from './prosite.component';

describe('PrositeComponent', () => {
  let component: PrositeComponent;
  let fixture: ComponentFixture<PrositeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrositeComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrositeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
