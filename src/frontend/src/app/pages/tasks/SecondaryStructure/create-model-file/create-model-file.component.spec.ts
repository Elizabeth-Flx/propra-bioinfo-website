import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateModelFileComponent } from './create-model-file.component';

describe('CreateModelFileComponent', () => {
  let component: CreateModelFileComponent;
  let fixture: ComponentFixture<CreateModelFileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateModelFileComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateModelFileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
