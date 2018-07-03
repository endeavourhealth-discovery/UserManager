import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataSetComponent } from './data-set/data-set.component';
import { DataSetEditorComponent } from './data-set-editor/data-set-editor.component';
import { DataSetPickerComponent } from './data-set-picker/data-set-picker.component';
import {DataSetService} from './data-set.service';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DialogsModule} from 'eds-angular4';
import {FormsModule} from '@angular/forms';
import { ToastModule } from 'ng2-toastr/ng2-toastr';
import {EntityViewComponentsModule} from "eds-angular4/dist/entityViewer";
import {ControlsModule} from "eds-angular4/dist/controls";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    EntityViewComponentsModule,
    ToastModule.forRoot(),
    DialogsModule,
    ControlsModule
  ],
  declarations: [
    DataSetComponent,
    DataSetEditorComponent,
    DataSetPickerComponent],
  entryComponents: [
    DataSetPickerComponent
  ],
  providers: [DataSetService]
})
export class DataSetModule { }
