import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegionComponent } from './region/region.component';
import { LoggerService, DialogsModule } from 'eds-angular4';
import {RegionService} from './region.service';
import { RegionEditorComponent } from './region-editor/region-editor.component';
import { RegionPickerComponent } from './region-picker/region-picker.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {FormsModule} from '@angular/forms';
import { ToastModule } from 'ng2-toastr/ng2-toastr';
import {EntityViewComponentsModule} from "eds-angular4/dist/entityViewer";
import {GoogleMapsModule} from "eds-angular4/dist/googleMaps";

@NgModule({
  imports: [
    // AgmCoreModule.forRoot(),
    CommonModule,
    FormsModule,
    EntityViewComponentsModule,
    NgbModule,
    ToastModule.forRoot(),
    GoogleMapsModule,
    DialogsModule
  ],
  declarations: [
    RegionComponent,
    RegionEditorComponent,
    RegionPickerComponent
  ],
  entryComponents : [
    RegionPickerComponent
  ],
  providers: [
    RegionService,
    LoggerService]
  // {provide: MapsAPILoader, useClass: CustomLazyAPIKeyLoader }]
})
export class RegionModule { }
