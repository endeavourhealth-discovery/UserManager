import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
//import {D3TreeGraphComponent} from "./d3-tree-graph/d3-tree-graph.component";
//import {NvD3Module} from 'ng2-nvd3';
//import 'd3';
//import 'nvd3';

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
    //NvD3Module
  ],
  declarations: [
    //D3TreeGraphComponent,
  ],
  exports: [
    //D3TreeGraphComponent
  ],
  entryComponents: [
    //D3TreeGraphComponent
  ],
  providers: [
    DatePipe
  ]
})
export class D3TreeGraphModule { }
