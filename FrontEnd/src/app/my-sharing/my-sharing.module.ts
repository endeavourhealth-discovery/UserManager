import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import { LoggerService, DialogsModule } from 'eds-angular4';
import { ToastModule } from 'ng2-toastr/ng2-toastr';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {EntityViewComponentsModule} from "eds-angular4/dist/entityViewer";
import {OrganisationService} from "../organisation/organisation.service";
import {MySharingOverviewComponent} from "./my-sharing-overview/my-sharing-overview.component";
import {ControlsModule} from "eds-angular4/dist/controls";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    EntityViewComponentsModule,
    ToastModule.forRoot(),
    NgbModule,
    DialogsModule,
    ControlsModule
  ],
  declarations: [
    MySharingOverviewComponent
  ],
  entryComponents : [
  ],
  providers: [
    OrganisationService,
    LoggerService]
})
export class MySharingModule { }
