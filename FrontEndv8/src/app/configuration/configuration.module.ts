import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
//import {ApplicationEditorComponent} from './application-editor/application-editor.component';
//import {ApplicationPolicyEditorComponent} from './application-policy-editor/application-policy-editor.component';
//import {ConfigurationComponent} from './configuration/configuration.component';
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
  MatTableModule, MatTreeModule
} from '@angular/material';
import {FlexModule} from "@angular/flex-layout";
import {RouterModule} from "@angular/router";
import {CoreModule} from "dds-angular8";
import {GenericTableComponent} from "../generic-table/generic-table/generic-table.component";
import {GenericTableSspComponent} from "../generic-table/generic-table-ssp/generic-table-ssp.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {BrowserModule} from "@angular/platform-browser";

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
    MatProgressBarModule
  ],
  declarations: [
    //ConfigurationComponent,
    //ApplicationEditorComponent,
    //ApplicationPolicyEditorComponent,
    GenericTableComponent,
    GenericTableSspComponent
  ],
  entryComponents: [
  ],
  providers: [
    ConfigurationService,
    DatePipe
  ]
})
export class ConfigurationModule { }
