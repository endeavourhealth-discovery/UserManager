import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ConfigurationComponent} from './configuration/configuration.component';
import {ConfigurationService} from "./configuration.service";
import {
  MatButtonModule,
  MatCardModule, MatCheckboxModule,
  MatDialogModule,
  MatFormFieldModule,
  MatIconModule, MatInputModule,
  MatMenuModule,
  MatPaginatorModule, MatProgressBarModule, MatProgressSpinnerModule,
  MatSelectModule, MatSnackBarModule, MatSortModule,
  MatTableModule, MatTabsModule, MatTooltipModule, MatTreeModule
} from '@angular/material';
import {FlexModule} from "@angular/flex-layout";
import {RouterModule} from "@angular/router";
import {CoreModule} from "dds-angular8";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {BrowserModule} from "@angular/platform-browser";
import {GenericTableModule} from "../generic-table/generic-table.module";
import {ApplicationEditorComponent} from "./application/application-editor/application-editor.component";
import {ApplicationPolicyEditorComponent} from "./application-policy-editor/application-policy-editor.component";
import {ApplicationProfileDialogComponent} from "./application/application-profile-dialog/application-profile-dialog.component";
import { ApplicationProfilePickerComponent } from './application/application-profile-picker/application-profile-picker.component';
import { ApplicationPolicyDialogComponent } from './application/application-policy-dialog/application-policy-dialog.component';
import { ApplicationDialogComponent } from './application/application-dialog/application-dialog.component';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    CommonModule,
    FormsModule,
    MatCardModule,
    MatTableModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    FormsModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    RouterModule,
    FlexModule,
    MatSelectModule,
    MatSnackBarModule,
    MatCheckboxModule,
    MatMenuModule,
    MatDialogModule,
    CoreModule,
    MatButtonModule,
    MatTreeModule,
    MatTooltipModule,
    MatTabsModule,
    MatCheckboxModule,
    MatProgressBarModule,
    GenericTableModule
  ],
  declarations: [
    ConfigurationComponent,
    ApplicationEditorComponent,
    ApplicationPolicyEditorComponent,
    ApplicationProfileDialogComponent,
    ApplicationProfilePickerComponent,
    ApplicationPolicyDialogComponent,
    ApplicationDialogComponent
  ],
  entryComponents: [
    ApplicationProfileDialogComponent,
    ApplicationProfilePickerComponent,
    ApplicationPolicyDialogComponent,
    ApplicationDialogComponent
  ],
  providers: [
    ConfigurationService,
    DatePipe
  ]
})
export class ConfigurationModule { }
