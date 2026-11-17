import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SmallToolsComponent} from './small-tools.component';

describe('TasksComponent', () => {
  let component: SmallToolsComponent;
  let fixture: ComponentFixture<SmallToolsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmallToolsComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(SmallToolsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
