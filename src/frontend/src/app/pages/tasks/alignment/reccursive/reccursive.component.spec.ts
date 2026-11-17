import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReccursiveComponent } from './reccursive.component';

describe('ReccursiveComponent', () => {
  let component: ReccursiveComponent;
  let fixture: ComponentFixture<ReccursiveComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReccursiveComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReccursiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
