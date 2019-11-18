import { NgModule } from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule} from "@angular/forms";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {ControlsModule} from "eds-angular4/dist/controls";
import {DialogsModule} from "eds-angular4";
import {NgxPaginationModule} from "ngx-pagination";
import {AngularDateTimePickerModule} from "angular2-datetimepicker";
import {AuditComponent} from "./audit/audit.component";
import {AuditCommonModule} from "eds-audittrail/dist/auditTrail";


@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ControlsModule,
    DialogsModule,
    NgxPaginationModule,
    AngularDateTimePickerModule,
    AuditCommonModule
  ],
  declarations: [AuditComponent]

})
export class AuditModule { }
