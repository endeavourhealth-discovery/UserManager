import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApplicationEditorComponent } from './application-editor.component';

describe('ApplicationEditorComponent', () => {
  let component: ApplicationEditorComponent;
  let fixture: ComponentFixture<ApplicationEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApplicationEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApplicationEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
