import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConfigurationComponent } from './configuration/configuration.component';
import {DialogsModule, LoggerService} from 'eds-angular4';
import {FormsModule} from "@angular/forms";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {ToastModule} from "ng2-toastr";
import {ControlsModule} from "eds-angular4/dist/controls";
import {ConfigurationService} from "./configuration.service";
import {Ng4JsonEditorModule} from "angular4-jsoneditor";
import { ApplicationEditorComponent } from './application-editor/application-editor.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ToastModule.forRoot(),
    DialogsModule,
    ControlsModule,
    Ng4JsonEditorModule
  ],
  declarations: [ConfigurationComponent, ApplicationEditorComponent],
  providers: [
    ConfigurationService
  ]
})
export class ConfigurationModule { }
