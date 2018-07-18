import {Injectable} from '@angular/core';
import {AbstractMenuProvider } from 'eds-angular4';
import {MenuOption} from 'eds-angular4/dist/layout/models/MenuOption';
import {Routes} from '@angular/router';
import {UserComponent} from "./user/user/user.component";
import {DelegationComponent} from "./delegation/delegation/delegation.component";

@Injectable()
export class AppMenuService implements  AbstractMenuProvider  {
  static getRoutes(): Routes {
    return [
      { path: '', redirectTo : 'user', pathMatch: 'full' }, // Default route
      { path: 'user', component: UserComponent},
      { path: 'delegation', component: DelegationComponent}
    ];
  }

  getClientId(): string {
    return 'eds-dsa-manager';
  }
  getApplicationTitle(): string {
    return 'User Manager';
  }
  getMenuOptions(): MenuOption[] {
    return [
      {caption: 'Users', state: 'user', icon: 'fa fa-user', role: 'eds-dsa-manager:viewer'},
      {caption: 'DelegationData', state: 'delegation', icon: 'fa fa-cogs', role: 'eds-dsa-manager:viewer'}
    ];
  }
}
