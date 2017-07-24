import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataFlowComponent } from './data-flow/data-flow.component';
import { LoggerService, EntityViewComponentsModule } from 'eds-angular4';
import {DataFlowService} from './data-flow.service';
import { DataFlowEditorComponent } from './data-flow-editor/data-flow-editor.component';
import { DataflowPickerComponent } from './dataflow-picker/dataflow-picker.component';

@NgModule({
  imports: [
    CommonModule,
    EntityViewComponentsModule
  ],
  declarations: [DataFlowComponent, DataFlowEditorComponent, DataflowPickerComponent],
  entryComponents : [
    DataflowPickerComponent
  ],
  providers: [
    DataFlowService,
    LoggerService]
})
export class DataFlowModule { }
