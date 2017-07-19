import {Injectable} from "@angular/core";
import {MenuService} from "eds-angular4";
import {MenuOption} from "eds-angular4/dist/layout/models/MenuOption";

@Injectable()
export class AppMenuService implements  MenuService {
  getApplicationTitle(): string {
    return 'Information Modeller';
  }
  getMenuOptions():MenuOption[] {
    return [
      {caption: 'Concept Modeller', state: 'conceptModeller', icon: 'fa fa-sitemap'},
      {caption: 'IM Settings', state: 'settings', icon: 'fa fa-cogs'}
    ];
  }
}
