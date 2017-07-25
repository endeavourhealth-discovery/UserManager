import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataSharingAgreementComponent } from './data-sharing-agreement/data-sharing-agreement.component';
import { DataSharingAgreementEditorComponent } from './data-sharing-agreement-editor/data-sharing-agreement-editor.component';
import { DataSharingAgreementPickerComponent } from './data-sharing-agreement-picker/data-sharing-agreement-picker.component';
import { PurposeAddComponent } from './purpose-add/purpose-add.component';
import {DataSharingAgreementService} from "./data-sharing-agreement.service";

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    DataSharingAgreementComponent,
    DataSharingAgreementEditorComponent,
    DataSharingAgreementPickerComponent,
    PurposeAddComponent],
  providers: [DataSharingAgreementService]
})
export class DataSharingAgreementModule { }