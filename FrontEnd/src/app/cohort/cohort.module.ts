import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CohortComponent } from './cohort/cohort.component';
import { CohortEditorComponent } from './cohort-editor/cohort-editor.component';
import { CohortPickerComponent } from './cohort-picker/cohort-picker.component';
import {CohortService} from "./cohort.service";

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [CohortComponent, CohortEditorComponent, CohortPickerComponent],
  providers: [CohortService]
})
export class CohortModule { }
