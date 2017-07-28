import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CohortComponent } from './cohort/cohort.component';
import { CohortEditorComponent } from './cohort-editor/cohort-editor.component';
import { CohortPickerComponent } from './cohort-picker/cohort-picker.component';
import {CohortService} from './cohort.service';
import {EntityViewComponentsModule} from 'eds-angular4';
import {FormsModule} from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    EntityViewComponentsModule,
    NgbModule
  ],
  declarations: [
    CohortComponent,
    CohortEditorComponent,
    CohortPickerComponent],
  providers: [CohortService]
})
export class CohortModule { }
