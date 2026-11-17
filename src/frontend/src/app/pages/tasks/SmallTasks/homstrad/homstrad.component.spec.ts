import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomstradComponent } from './homstrad.component';

describe('HomstradComponent', () => {
  let component: HomstradComponent;
  let fixture: ComponentFixture<HomstradComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HomstradComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomstradComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
