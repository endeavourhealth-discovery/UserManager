import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationProfilePickerComponent } from './application-profile-picker.component';

describe('ApplicationProfilePickerComponent', () => {
  let component: ApplicationProfilePickerComponent;
  let fixture: ComponentFixture<ApplicationProfilePickerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplicationProfilePickerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationProfilePickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
