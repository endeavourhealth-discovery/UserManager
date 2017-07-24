import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSharingSummaryEditorComponent } from './data-sharing-summary-editor.component';

describe('DataSharingSummaryEditorComponent', () => {
  let component: DataSharingSummaryEditorComponent;
  let fixture: ComponentFixture<DataSharingSummaryEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataSharingSummaryEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataSharingSummaryEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
