import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataProcessingAgreementComponent } from './data-processing-agreement/data-processing-agreement.component';
import { DataProcessingAgreementEditorComponent } from './data-processing-agreement-editor/data-processing-agreement-editor.component';
import { DataProcessingAgreementPickerComponent } from './data-processing-agreement-picker/data-processing-agreement-picker.component';
import {DataProcessingAgreementService} from "./data-processing-agreement.service";

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    DataProcessingAgreementComponent,
    DataProcessingAgreementEditorComponent,
    DataProcessingAgreementPickerComponent],
  providers: [DataProcessingAgreementService]
})
export class DataProcessingAgreementModule { }
