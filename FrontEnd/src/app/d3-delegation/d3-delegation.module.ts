import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ToastModule} from "ng2-toastr";
import {DialogsModule} from "eds-angular4";
import {ControlsModule} from "eds-angular4/dist/controls";
import {FormsModule} from "@angular/forms";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {D3TreeGraphModule} from "../d3-tree-graph/d3-tree-graph.module";
import {DelegationService} from "../delegation/delegation.service";
import {D3DelegationComponent} from "./d3-delegation/d3-delegation.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ToastModule.forRoot(),
    DialogsModule,
    ControlsModule,
    D3TreeGraphModule
  ],
  declarations: [D3DelegationComponent],
  providers: [
    DelegationService
  ]
})
export class D3DelegationModule { }
