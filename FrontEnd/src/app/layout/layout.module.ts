import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FormsModule} from "@angular/forms";
import {SecurityModule} from "../security/security.module";
import {SidebarComponent} from "./sidebar/sidebar.component";
import {TopnavComponent} from "./topnav/topnav.component";
import {LayoutComponent} from "./layout.component";
import {RouterModule} from "@angular/router";

@NgModule({
  imports : [
    BrowserModule,
    FormsModule,
    SecurityModule,
    RouterModule
  ],
  declarations : [
    LayoutComponent,
    SidebarComponent,
    TopnavComponent,
  ]
})
export class LayoutModule {}