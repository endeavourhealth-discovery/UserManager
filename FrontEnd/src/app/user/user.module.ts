import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule} from '@angular/forms';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { ToastModule } from 'ng2-toastr/ng2-toastr';
import {DialogsModule, LoggerService} from 'eds-angular4';
import {ControlsModule} from "eds-angular4/dist/controls";
import {UserService} from "./user.service";
import { UserComponent } from './user/user.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    ToastModule.forRoot(),
    DialogsModule,
    ControlsModule
  ],
  declarations: [
    UserComponent
  ],
  entryComponents: [

  ],
  providers: [UserService, LoggerService]
})
export class UserModule { }
