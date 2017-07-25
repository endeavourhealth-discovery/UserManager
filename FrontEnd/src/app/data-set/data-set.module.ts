import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataSetComponent } from './data-set/data-set.component';
import { DataSetEditorComponent } from './data-set-editor/data-set-editor.component';
import { DataSetPickerComponent } from './data-set-picker/data-set-picker.component';
import {DataSetService} from "./data-set.service";

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    DataSetComponent,
    DataSetEditorComponent,
    DataSetPickerComponent],
  providers: [DataSetService]
})
export class DataSetModule { }
