import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegionComponent } from './region/region.component';
import { LoggerService, EntityViewComponentsModule } from 'eds-angular4';
import {RegionService} from './region.service';
import { RegionEditorComponent } from './region-editor/region-editor.component';
import { RegionPickerComponent } from './region-picker/region-picker.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {AgmCoreModule, MapsAPILoader} from 'angular2-google-maps/core';
import {CustomLazyAPIKeyLoader} from './CustomLazyAPIKeyLoader';

@NgModule({
  imports: [
    AgmCoreModule.forRoot(),
    CommonModule,
    EntityViewComponentsModule,
    NgbModule
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
    LoggerService,
    {provide: MapsAPILoader, useClass: CustomLazyAPIKeyLoader }]
})
export class RegionModule { }
