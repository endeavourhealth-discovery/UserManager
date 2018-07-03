import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CohortComponent } from './cohort/cohort.component';
import { CohortEditorComponent } from './cohort-editor/cohort-editor.component';
import { CohortPickerComponent } from './cohort-picker/cohort-picker.component';
import {CohortService} from './cohort.service';
import {DialogsModule} from 'eds-angular4';
import {FormsModule} from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { ToastModule } from 'ng2-toastr/ng2-toastr';
import {EntityViewComponentsModule} from "eds-angular4/dist/entityViewer";
import {ControlsModule} from "eds-angular4/dist/controls";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    EntityViewComponentsModule,
    NgbModule,
    ToastModule.forRoot(),
    DialogsModule,
    ControlsModule
  ],
  declarations: [
    CohortComponent,
    CohortEditorComponent,
    CohortPickerComponent],
  entryComponents: [
    CohortPickerComponent
  ],
  providers: [CohortService]
})
export class CohortModule { }
