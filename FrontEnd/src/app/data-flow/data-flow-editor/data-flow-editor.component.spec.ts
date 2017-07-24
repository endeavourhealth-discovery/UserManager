import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataFlowEditorComponent } from './data-flow-editor.component';

describe('DataFlowEditorComponent', () => {
  let component: DataFlowEditorComponent;
  let fixture: ComponentFixture<DataFlowEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataFlowEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataFlowEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
