import { BrowserModule } from '@angular/platform-browser';
import { NgModule} from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {KeycloakService} from "eds-angular4/dist/keycloak/keycloak.service";
import {keycloakHttpFactory} from "eds-angular4/dist/keycloak/keycloak.http";
import {Http, HttpModule, RequestOptions, XHRBackend} from "@angular/http";
import {LayoutComponent} from "eds-angular4/dist/layout/layout.component";
import {LayoutModule, MenuService} from "eds-angular4";
import {AppMenuService} from "./app-menu.service";
import {SettingsComponent} from "./settings/settings/settings.component";
import {SettingsModule} from "./settings/settings.module";
import {ConceptModellerComponent} from "./concept-modeller/concept-modeller/concept-modeller.component";
import {ConceptModellerModule} from "./concept-modeller/concept-modeller.module";

export class DummyComponent {}

const appRoutes: Routes = [
  { path: 'conceptModeller', component: ConceptModellerComponent },
  { path: 'settings', component: SettingsComponent },
  { path: 'eds-user-manager', component: DummyComponent }
];

@NgModule({
  declarations: [
  ],
  imports: [
    BrowserModule,
    HttpModule,
    LayoutModule,
    SettingsModule,
    ConceptModellerModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    KeycloakService,
    { provide: Http, useFactory: keycloakHttpFactory, deps: [XHRBackend, RequestOptions, KeycloakService] },
    { provide: MenuService, useClass : AppMenuService }
  ],
  bootstrap: [LayoutComponent]
})
export class AppModule { }
