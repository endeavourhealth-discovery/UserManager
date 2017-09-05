import {Injectable} from '@angular/core';
import {AbstractMenuProvider } from 'eds-angular4';
import {MenuOption} from 'eds-angular4/dist/layout/models/MenuOption';

@Injectable()
export class AppMenuService implements  AbstractMenuProvider  {
  getClientId(): string {
    return 'eds-dsa-manager';
  }
  getApplicationTitle(): string {
    return 'Data Sharing Manager';
  }
  getMenuOptions(): MenuOption[] {
    return [
      {caption: 'Organisation', state: 'organisationOverview', icon: 'fa fa-hospital-o', role: 'eds-dsa-manager:org-manager'},
      {caption: 'Sharing', state: 'sharingOverview', icon: 'fa fa-cogs', role: 'eds-dsa-manager:sharing-manager'}
    ];
  }
}
