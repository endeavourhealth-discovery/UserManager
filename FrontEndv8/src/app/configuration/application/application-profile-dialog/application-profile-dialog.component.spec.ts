import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationProfileDialogComponent } from './application-profile-dialog.component';

describe('ApplicationProfileDialogComponent', () => {
  let component: ApplicationProfileDialogComponent;
  let fixture: ComponentFixture<ApplicationProfileDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplicationProfileDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationProfileDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
