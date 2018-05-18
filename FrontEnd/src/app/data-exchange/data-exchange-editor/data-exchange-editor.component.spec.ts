import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataExchangeEditorComponent } from './data-exchange-editor.component';

describe('DataExchangeEditorComponent', () => {
  let component: DataExchangeEditorComponent;
  let fixture: ComponentFixture<DataExchangeEditorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataExchangeEditorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataExchangeEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
