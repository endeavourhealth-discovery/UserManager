import {Injectable} from '@angular/core';
import {AbstractMenuProvider } from 'eds-angular4';
import {MenuOption} from 'eds-angular4/dist/layout/models/MenuOption';
import {Routes} from '@angular/router';
import {UserComponent} from "./user/user/user.component";
import {DelegationComponent} from "./delegation/delegation/delegation.component";
import {D3DelegationComponent} from "./d3-delegation/d3-delegation/d3-delegation.component";
import {ConfigurationComponent} from "./configuration/configuration/configuration.component";
import {AuditComponent} from "./audit/audit/audit.component";
import {UserEditorComponent} from './user/user-editor/user-editor.component';

@Injectable()
export class AppMenuService implements  AbstractMenuProvider  {
  static getRoutes(): Routes {
    return [
      { path: '', redirectTo : 'user', pathMatch: 'full' }, // Default route
      { path: 'user', component: UserComponent},
      { path: 'user/:organisationId', component: UserComponent},
      { path: 'userEdit', component: UserEditorComponent},
      { path: 'delegation', component: DelegationComponent},
      { path: 'd3delegation', component: D3DelegationComponent},
      { path: 'configuration', component: ConfigurationComponent},
      { path: 'audit', component: AuditComponent}
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
      {caption: 'Users', state: 'user', icon: 'fa fa-user', role: 'eds-user-manager:user-manager'},
      {caption: 'Configuration', state: 'configuration', icon: 'fa fa-cogs', role: 'eds-user-manager:user-manager'},
      {caption: 'Delegation', state: 'd3delegation', icon: 'fa fa-sitemap', role: 'eds-user-manager:user-manager'},
      {caption: 'Audit', state: 'audit', icon: 'fa fa-list-ul', role: 'eds-user-manager:user-manager'}
      /*{caption: 'Delegation Data', state: 'delegation', icon: 'fa fa-sitemap', role: 'eds-dsa-manager:viewer'},*/
    ];
  }
}
