import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import { OrganisationComponent } from './organisation/organisation.component';
import { OrganisationOverviewComponent } from './organisation-overview/organisation-overview.component';
import { OrganisationService } from './organisation.service';
import { LoggerService, EntityViewComponentsModule } from 'eds-angular4';
import { ToastModule } from 'ng2-toastr/ng2-toastr';
import { OrganisationEditorComponent } from './organisation-editor/organisation-editor.component';
import { OrganisationPickerComponent } from './organisation-picker/organisation-picker.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {RegionPickerComponent} from '../region/region-picker/region-picker.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    EntityViewComponentsModule,
    ToastModule.forRoot(),
    NgbModule
  ],
  declarations: [
    OrganisationComponent,
    OrganisationOverviewComponent,
    OrganisationEditorComponent,
    OrganisationPickerComponent,
    RegionPickerComponent
  ],
  entryComponents : [
    OrganisationPickerComponent
  ],
  providers: [
    OrganisationService,
    LoggerService]
})
export class OrganisationModule { }
