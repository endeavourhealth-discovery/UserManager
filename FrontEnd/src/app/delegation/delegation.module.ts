import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DelegationComponent } from './delegation/delegation.component';
import {ToastModule} from "ng2-toastr";
import {FormsModule} from "@angular/forms";
import {DialogsModule} from "eds-angular4";
import {ControlsModule} from "eds-angular4/dist/controls";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DelegationService} from "./delegation.service";
import {TreeDiagram} from "angular2-tree-diagram";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ToastModule.forRoot(),
    DialogsModule,
    ControlsModule,
    TreeDiagram

  ],
  declarations: [
    DelegationComponent],
  providers: [
    DelegationService
  ]
})
export class DelegationModule { }
