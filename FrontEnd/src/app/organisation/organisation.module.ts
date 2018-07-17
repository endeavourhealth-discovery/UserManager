import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {LoggerService} from "eds-angular4";
import {OrganisationService} from "./organisation.service";
import { OrganisationPickerComponent } from './organisation-picker/organisation-picker.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [OrganisationPickerComponent],
  providers: [OrganisationService, LoggerService]
})
export class OrganisationModule { }
