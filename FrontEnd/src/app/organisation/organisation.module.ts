import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {LoggerService} from "eds-angular4";
import {OrganisationService} from "./organisation.service";
import { OrganisationPickerComponent } from './organisation-picker/organisation-picker.component';
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    CommonModule,
    FormsModule
  ],
  declarations: [OrganisationPickerComponent],
  entryComponents: [
    OrganisationPickerComponent
  ],
  providers: [OrganisationService, LoggerService]
})
export class OrganisationModule { }
