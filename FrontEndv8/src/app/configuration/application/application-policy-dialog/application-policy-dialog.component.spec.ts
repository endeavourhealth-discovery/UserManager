import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationPolicyDialogComponent } from './application-policy-dialog.component';

describe('ApplicationPolicyDialogComponent', () => {
  let component: ApplicationPolicyDialogComponent;
  let fixture: ComponentFixture<ApplicationPolicyDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplicationPolicyDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationPolicyDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
