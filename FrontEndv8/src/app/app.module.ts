import {NgModule, DoBootstrap, ApplicationRef} from '@angular/core';
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular';
import {AppMenuService} from './app-menu.service';
import {RouterModule} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';
import {
  AbstractMenuProvider,
  DialogsModule,
  LayoutComponent,
  LayoutModule,
  LoggerModule,
  SecurityModule,
  UserManagerModule
} from 'dds-angular8';
import {UserModule} from "./user/user.module";
import {GenericTableModule} from "./generic-table/generic-table.module";
import {ConfigurationModule} from "./configuration/configuration.module";
import {OrganisationModule} from "./organisation/organisation.module";
import {AuditModule} from "./audit/audit.module";
import {DelegationModule} from "./delegation/delegation.module";
//import {OrganisationModule} from "./organisation/organisation.module";
//import {D3DelegationModule} from "./d3-delegation/d3-delegation.module";
//import {ConfigurationModule} from "./configuration/configuration.module";

const keycloakService = new KeycloakService();

@NgModule({
  imports: [
    KeycloakAngularModule,
    HttpClientModule,

    LayoutModule,
    SecurityModule,
    LoggerModule,
    UserManagerModule,
    UserModule,
    GenericTableModule,
    OrganisationModule,
    //D3DelegationModule,
    ConfigurationModule,
    AuditModule,
    DelegationModule,

    DialogsModule,

    RouterModule.forRoot(AppMenuService.getRoutes(), {useHash: true}),
  ],
  providers: [
    { provide: AbstractMenuProvider, useClass : AppMenuService },
    { provide: KeycloakService, useValue: keycloakService }
  ]
})
export class AppModule implements DoBootstrap {
  ngDoBootstrap(appRef: ApplicationRef) {
    keycloakService
      .init({config: 'public/wellknown/authconfigraw', initOptions: {onLoad: 'login-required'}})
      .then((authenticated) => {
        if (authenticated)
          appRef.bootstrap(LayoutComponent);
      })
      .catch(error => console.error('[ngDoBootstrap] init Keycloak failed', error));
  }
}
