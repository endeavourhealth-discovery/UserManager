import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataSharingSummaryComponent } from './data-sharing-summary/data-sharing-summary.component';
import { DataSharingSummaryEditorComponent } from './data-sharing-summary-editor/data-sharing-summary-editor.component';
import { DataSharingSummaryOverviewComponent } from './data-sharing-summary-overview/data-sharing-summary-overview.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [DataSharingSummaryComponent, DataSharingSummaryEditorComponent, DataSharingSummaryOverviewComponent]
})
export class DataSharingSummaryModule { }
