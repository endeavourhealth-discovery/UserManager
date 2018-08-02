import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuditComponent } from './audit/audit.component';
import {FormsModule} from "@angular/forms";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {ControlsModule} from "eds-angular4/dist/controls";
import {DialogsModule} from "eds-angular4";
import {AuditService} from "./audit.service";
import { AuditDetailComponent } from './audit-detail/audit-detail.component';
import {NgxPaginationModule} from "ngx-pagination";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ControlsModule,
    DialogsModule,
    NgxPaginationModule
  ],
  declarations: [AuditComponent, AuditDetailComponent],
  providers: [
    AuditService
  ],
  entryComponents: [
    AuditDetailComponent
  ]
})
export class AuditModule { }
