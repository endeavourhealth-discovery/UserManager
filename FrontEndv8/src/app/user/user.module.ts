import {NgModule} from '@angular/core';
import {CommonModule, DatePipe} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {UserComponent} from './user/user.component';
import {UserService} from "./user.service";
import {DelegationService} from "../d3-delegation/delegation.service";
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
import { UserEditorComponent } from './user-editor/user-editor.component';
import {UserProfileComponent} from "./user-profile/user-profile.component";
import {LinkifyPipe} from "./user-profile/linkify.pipe";
import { UserDialogComponent } from './user-dialog/user-dialog.component';
import { UserPickerComponent } from './user-picker/user-picker.component';

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
    MatProgressBarModule,
    GenericTableModule
  ],
  declarations: [
    UserComponent,
    UserEditorComponent,
    UserProfileComponent,
    LinkifyPipe,
    UserDialogComponent,
    UserPickerComponent
    // UserEditorComponent,
  ],
  entryComponents: [
    // UserEditorComponent
    UserDialogComponent,
    UserPickerComponent
  ],
  providers: [
    UserService,
    DelegationService,
    DatePipe
  ]
})
export class UserModule { }
