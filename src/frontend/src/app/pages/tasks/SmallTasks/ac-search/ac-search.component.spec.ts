import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AcSearchComponent} from './ac-search.component';

describe('AcToFastaComponent', () => {
  let component: AcSearchComponent;
  let fixture: ComponentFixture<AcSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AcSearchComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AcSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
