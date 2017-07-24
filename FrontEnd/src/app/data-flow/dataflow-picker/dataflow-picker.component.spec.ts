import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataflowPickerComponent } from './dataflow-picker.component';

describe('DataflowPickerComponent', () => {
  let component: DataflowPickerComponent;
  let fixture: ComponentFixture<DataflowPickerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataflowPickerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataflowPickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
