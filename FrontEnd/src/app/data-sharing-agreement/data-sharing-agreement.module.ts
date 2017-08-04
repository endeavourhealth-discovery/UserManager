import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataSharingAgreementComponent } from './data-sharing-agreement/data-sharing-agreement.component';
import { DataSharingAgreementEditorComponent } from './data-sharing-agreement-editor/data-sharing-agreement-editor.component';
import { DataSharingAgreementPickerComponent } from './data-sharing-agreement-picker/data-sharing-agreement-picker.component';
import { PurposeAddComponent } from './purpose-add/purpose-add.component';
import {DataSharingAgreementService} from './data-sharing-agreement.service';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {EntityViewComponentsModule, GoogleMapsModule} from 'eds-angular4';
import {FormsModule} from '@angular/forms';
import { ToastModule } from 'ng2-toastr/ng2-toastr';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    EntityViewComponentsModule,
    ToastModule.forRoot(),
    GoogleMapsModule
  ],
  declarations: [
    DataSharingAgreementComponent,
    DataSharingAgreementEditorComponent,
    DataSharingAgreementPickerComponent,
    PurposeAddComponent],
  entryComponents : [
    PurposeAddComponent,
    DataSharingAgreementPickerComponent
  ],
  providers: [DataSharingAgreementService]
})
export class DataSharingAgreementModule { }
