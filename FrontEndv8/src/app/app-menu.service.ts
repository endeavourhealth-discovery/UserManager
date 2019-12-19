import {Injectable} from '@angular/core';
import {Routes} from '@angular/router';
import {AbstractMenuProvider, MenuOption} from 'dds-angular8';
import {UserComponent} from './user/user/user.component';

@Injectable()
export class AppMenuService implements  AbstractMenuProvider {
  static getRoutes(): Routes {
    return [
      { path: '', redirectTo : 'user', pathMatch: 'full' }, // Default route
      { path: 'user', component: UserComponent, data: {role: 'User'}},
      { path: 'user/:organisationId', component: UserComponent, data: {role: 'User'}}
    ];
  }

  useUserManagerForRoles(): boolean {
    return true;
  }

  getClientId(): string {
    return 'eds-user-manager';
  }

  getApplicationTitle(): string {
    return 'User Manager';
  }

  getMenuOptions(): MenuOption[] {
    return [
      {caption: 'Users', state: 'user', icon: 'account_box'},
    ];
  }
}
