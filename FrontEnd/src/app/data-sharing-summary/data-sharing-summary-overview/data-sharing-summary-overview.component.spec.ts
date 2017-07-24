import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSharingSummaryOverviewComponent } from './data-sharing-summary-overview.component';

describe('DataSharingSummaryOverviewComponent', () => {
  let component: DataSharingSummaryOverviewComponent;
  let fixture: ComponentFixture<DataSharingSummaryOverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DataSharingSummaryOverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DataSharingSummaryOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
