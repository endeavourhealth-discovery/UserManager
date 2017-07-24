import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataProcessingAgreementComponent } from './data-processing-agreement/data-processing-agreement.component';
import { DataProcessingAgreementEditorComponent } from './data-processing-agreement-editor/data-processing-agreement-editor.component';
import { DataProcessingAgreementPickerComponent } from './data-processing-agreement-picker/data-processing-agreement-picker.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [DataProcessingAgreementComponent, DataProcessingAgreementEditorComponent, DataProcessingAgreementPickerComponent]
})
export class DataProcessingAgreementModule { }
