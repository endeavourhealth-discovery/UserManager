import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleTypeEditorComponent } from './role-type-editor.component';

describe('RoleTypeEditorComponent', () => {
  let component: RoleTypeEditorComponent;
  let fixture: ComponentFixture<RoleTypeEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RoleTypeEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RoleTypeEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
