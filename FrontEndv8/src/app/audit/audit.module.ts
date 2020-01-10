import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuditComponent } from './audit/audit.component';
import {
  MatButtonModule, MatCardModule, MatCheckboxModule, MatDatepickerModule, MatDialogModule,
  MatFormFieldModule,
  MatIconModule, MatInputModule, MatMenuModule, MatNativeDateModule, MatPaginatorModule, MatProgressBarModule,
  MatProgressSpinnerModule, MatSelectModule, MatSnackBarModule, MatSortModule, MatTableModule,
  MatTreeModule
} from "@angular/material";
import {RouterModule} from "@angular/router";
import {BrowserModule} from "@angular/platform-browser";
import {FlexModule} from "@angular/flex-layout";
import {FormsModule} from "@angular/forms";
import {CoreModule} from "dds-angular8";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AuditCommonModule} from "dds-auditTrail";



@NgModule({
  declarations: [AuditComponent],
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
    MatProgressBarModule,
    AuditCommonModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  providers: [
    MatDatepickerModule
  ]
})
export class AuditModule { }
