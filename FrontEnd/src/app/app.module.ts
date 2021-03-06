import { BrowserModule } from '@angular/platform-browser';
import { NgModule} from '@angular/core';
import { RouterModule } from '@angular/router';

import {KeycloakService} from 'eds-angular4/dist/keycloak/keycloak.service';
import {keycloakHttpFactory} from 'eds-angular4/dist/keycloak/keycloak.http';
import {Http, HttpModule, RequestOptions, XHRBackend} from '@angular/http';
import {LayoutComponent} from 'eds-angular4/dist/layout/layout.component';
import {LayoutModule, AbstractMenuProvider, UserManagerNotificationService} from 'eds-angular4';
import {AppMenuService} from './app-menu.service';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ToastModule} from "ng2-toastr";
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {UserModule} from "./user/user.module";
import {EntityViewComponentsModule} from "eds-angular4/dist/entityViewer";
import {OrganisationModule} from "./organisation/organisation.module";
import {D3DelegationModule} from "./d3-delegation/d3-delegation.module";
import {ConfigurationModule} from "./configuration/configuration.module";
import {AuditModule} from "./audit/audit.module";
import {Ng4JsonEditorModule} from "angular4-jsoneditor";

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpModule,
    LayoutModule,
    RouterModule.forRoot(AppMenuService.getRoutes(), {useHash: true}),
    NgbModule.forRoot(),
    ToastModule.forRoot(),
    UserModule,
    OrganisationModule,
    EntityViewComponentsModule,
    D3DelegationModule,
    ConfigurationModule,
    AuditModule,
    Ng4JsonEditorModule
  ],
  providers: [
    KeycloakService,
    { provide: Http, useFactory: keycloakHttpFactory, deps: [XHRBackend, RequestOptions, KeycloakService, AbstractMenuProvider, UserManagerNotificationService] },
    { provide: AbstractMenuProvider, useClass : AppMenuService }
  ],
  bootstrap: [LayoutComponent]
})
export class AppModule { }
