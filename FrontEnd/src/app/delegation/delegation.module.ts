import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DelegationComponent } from './delegation/delegation.component';
import {ToastModule} from "ng2-toastr";
import {FormsModule} from "@angular/forms";
import {DialogsModule} from "eds-angular4";
import {ControlsModule} from "eds-angular4/dist/controls";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {DelegationService} from "./delegation.service";
import {TreeGraphModule} from "../tree-graph/tree-graph.module";
import {D3TreeGraphModule} from "../d3-tree-graph/d3-tree-graph.module";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ToastModule.forRoot(),
    DialogsModule,
    ControlsModule,
    TreeGraphModule,
    D3TreeGraphModule
  ],
  declarations: [
    DelegationComponent,],
  providers: [
    DelegationService
  ]
})
export class DelegationModule { }
