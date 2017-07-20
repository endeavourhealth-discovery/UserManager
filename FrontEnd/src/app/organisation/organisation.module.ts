import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import { OrganisationComponent } from './organisation/organisation.component';
import { OrganisationOverviewComponent } from './organisation-overview/organisation-overview.component';
import { OrganisationService } from './organisation.service';
import { LoggerService } from 'eds-angular4';
import { ToastModule } from 'ng2-toastr/ng2-toastr';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ToastModule.forRoot()
  ],
  declarations: [
    OrganisationComponent,
    OrganisationOverviewComponent
  ],
  providers: [
    OrganisationService,
    LoggerService]
})
export class OrganisationModule { }
