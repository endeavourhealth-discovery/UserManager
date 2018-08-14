import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { ToastModule } from 'ng2-toastr/ng2-toastr';
import {DialogsModule, LoggerService} from 'eds-angular4';
import {ControlsModule} from "eds-angular4/dist/controls";
import {UserService} from "./user.service";
import { UserComponent } from './user/user.component';
import { UserEditorComponent } from './user-editor/user-editor.component';
import {ModuleStateService} from 'eds-angular4/dist/common';
import {PopoverModule} from "ngx-popover";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ToastModule.forRoot(),
    DialogsModule,
    ControlsModule,
    PopoverModule
  ],
  declarations: [
    UserComponent,
    UserEditorComponent
  ],
  entryComponents: [
    UserEditorComponent
  ],
  providers: [UserService, LoggerService, ModuleStateService]
})
export class UserModule { }
