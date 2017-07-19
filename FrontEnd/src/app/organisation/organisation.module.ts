import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrganisationComponent } from './organisation/organisation.component';
import { OrganisationOverviewComponent } from './organisation-overview/organisation-overview.component';
import { OrganisationService } from './organisation.service';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    OrganisationComponent,
    OrganisationOverviewComponent
  ],
  providers: [OrganisationService]
})
export class OrganisationModule { }
