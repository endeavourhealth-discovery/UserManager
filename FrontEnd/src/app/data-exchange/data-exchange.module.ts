import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataExchangeComponent } from './data-exchange/data-exchange.component';
import { LoggerService, DialogsModule } from 'eds-angular4';
import {DataExchangeService} from './data-exchange.service';
import { DataExchangeEditorComponent } from './data-exchange-editor/data-exchange-editor.component';
import { DataExchangePickerComponent } from './data-exchange-picker/data-exchange-picker.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule} from '@angular/forms';
import {ToastModule} from 'ng2-toastr';
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
  declarations: [DataExchangeComponent, DataExchangeEditorComponent, DataExchangePickerComponent],
  entryComponents : [
    DataExchangePickerComponent
  ],
  providers: [
    DataExchangeService,
    LoggerService]
})
export class DataExchangeModule { }
