import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationPolicyEditorComponent } from './application-policy-editor.component';

describe('ApplicationPolicyEditorComponent', () => {
  let component: ApplicationPolicyEditorComponent;
  let fixture: ComponentFixture<ApplicationPolicyEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplicationPolicyEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationPolicyEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
