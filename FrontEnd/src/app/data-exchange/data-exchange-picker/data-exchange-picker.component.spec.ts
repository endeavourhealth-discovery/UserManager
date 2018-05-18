import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataExchangePickerComponent } from './data-exchange-picker.component';

describe('DataExchangePickerComponent', () => {
  let component: DataExchangePickerComponent;
  let fixture: ComponentFixture<DataExchangePickerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataExchangePickerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataExchangePickerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
