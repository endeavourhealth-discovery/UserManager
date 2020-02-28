import {Injectable} from '@angular/core';
import {Routes} from '@angular/router';
import {AbstractMenuProvider, MenuOption} from 'dds-angular8';
import {UserComponent} from './user/user/user.component';
import {ConfigurationComponent} from "./configuration/configuration/configuration.component";
import {UserEditorComponent} from "./user/user-editor/user-editor.component";
import {ApplicationEditorComponent} from "./configuration/application-editor/application-editor.component";
import {ApplicationPolicyEditorComponent} from "./configuration/application-policy-editor/application-policy-editor.component";
import {UserProfileComponent} from "./user/user-profile/user-profile.component";
import {AuditComponent} from "./audit/audit/audit.component";
import {DelegationComponent} from "./delegation/delegation/delegation.component";

@Injectable()
export class AppMenuService implements  AbstractMenuProvider {
  static getRoutes(): Routes {
    return [
      { path: '', redirectTo : 'user', pathMatch: 'full' }, // Default route
      { path: 'user', component: UserComponent, data: {role: 'User'}},
      { path: 'user/:organisationId', component: UserComponent, data: {role: 'User'}},
      { path: 'userEdit', component: UserEditorComponent, data: {role: 'User'}},
      { path: 'configuration', component: ConfigurationComponent, data: {role: 'User'}},
      { path: 'appEdit', component: ApplicationEditorComponent, data: {role: 'Admin'}},
      { path: 'appPolicyEdit', component: ApplicationPolicyEditorComponent, data: {role: 'Admin'}},
      { path: 'userProfile', component: UserProfileComponent, data: {role: 'User'}},
      { path: 'audit', component: AuditComponent, data: {role: 'User'}},
      { path: 'delegation', component: DelegationComponent, data: {role: 'Admin'}},
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
      {caption: 'Configuration', state: 'configuration', icon: 'account_box'},
      {caption: 'Delegation', state: 'delegation', icon: 'account_box'},
      {caption: 'Audit', state: 'audit', icon: 'account_box'}
    ];
  }
}
