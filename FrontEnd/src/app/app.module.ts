import { BrowserModule } from '@angular/platform-browser';
import { NgModule} from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {KeycloakService} from 'eds-angular4/dist/keycloak/keycloak.service';
import {keycloakHttpFactory} from 'eds-angular4/dist/keycloak/keycloak.http';
import {Http, HttpModule, RequestOptions, XHRBackend} from '@angular/http';
import {LayoutComponent} from 'eds-angular4/dist/layout/layout.component';
import {LayoutModule, MenuService } from 'eds-angular4';
import {AppMenuService} from './app-menu.service';
import {SettingsComponent} from './settings/settings/settings.component';
import {SettingsModule} from './settings/settings.module';
import {ConceptModellerComponent} from './concept-modeller/concept-modeller/concept-modeller.component';
import {ConceptModellerModule} from './concept-modeller/concept-modeller.module';
import {OrganisationModule} from './organisation/organisation.module';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { OrganisationComponent } from './organisation/organisation/organisation.component';
import { OrganisationOverviewComponent } from './organisation/organisation-overview/organisation-overview.component';
import {ToastModule} from 'ng2-toastr/ng2-toastr';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

export class DummyComponent {}

const appRoutes: Routes = [
  { path: 'conceptModeller', component: ConceptModellerComponent },
  { path: 'settings', component: SettingsComponent },
  { path: 'organisation', component: OrganisationOverviewComponent}
];

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpModule,
    LayoutModule,
    SettingsModule,
    ConceptModellerModule,
    OrganisationModule,
    RouterModule.forRoot(appRoutes),
    NgbModule.forRoot(),
    ToastModule.forRoot()
  ],
  providers: [
    KeycloakService,
    { provide: Http, useFactory: keycloakHttpFactory, deps: [XHRBackend, RequestOptions, KeycloakService] },
    { provide: MenuService, useClass : AppMenuService }
  ],
  bootstrap: [LayoutComponent]
})
export class AppModule { }
