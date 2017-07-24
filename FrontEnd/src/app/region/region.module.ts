import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegionComponent } from './region/region.component';
import { LoggerService, EntityViewComponentsModule } from 'eds-angular4';
import {RegionService} from './region.service';
import { RegionEditorComponent } from './region-editor/region-editor.component';
import { RegionPickerComponent } from './region-picker/region-picker.component';

@NgModule({
  imports: [
    CommonModule,
    EntityViewComponentsModule,
  ],
  declarations: [RegionComponent, RegionEditorComponent, RegionPickerComponent],
  entryComponents : [
    RegionPickerComponent
  ],
  providers: [
    RegionService,
    LoggerService]
})
export class RegionModule { }
