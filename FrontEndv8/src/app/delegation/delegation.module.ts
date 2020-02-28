import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ChecklistDatabase, DelegationComponent} from './delegation/delegation.component';
import {
  MatButtonModule, MatCardModule,
  MatCheckboxModule, MatDialogModule, MatFormFieldModule, MatIconModule,
  MatInputModule, MatMenuModule,
  MatPaginatorModule, MatProgressBarModule, MatProgressSpinnerModule, MatSelectModule,
  MatSnackBarModule, MatSortModule,
  MatTableModule, MatTreeModule
} from "@angular/material";
import {CoreModule, MessageBoxDialogComponent} from "dds-angular8";
import {RouterModule} from "@angular/router";
import {FormsModule} from "@angular/forms";
import {FlexModule} from "@angular/flex-layout";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {DelegationService} from "./delegation.service";



@NgModule({
  declarations: [DelegationComponent],
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
  entryComponents: [
    MessageBoxDialogComponent
  ],
  providers: [
    DelegationService,
    ChecklistDatabase
  ]

})
export class DelegationModule { }
