import {Injectable} from '@angular/core';
import {MenuService} from 'eds-angular4';
import {MenuOption} from 'eds-angular4/dist/layout/models/MenuOption';

@Injectable()
export class AppMenuService implements  MenuService {
  getApplicationTitle(): string {
    return 'Data Sharing Manager';
  }
  getMenuOptions(): MenuOption[] {
    return [
      {caption: 'Organisation', state: 'organisationOverview', icon: 'fa fa-hospital-o'},
      {caption: 'Sharing', state: 'sharingOverview', icon: 'fa fa-cogs'}
    ];
  }
}
