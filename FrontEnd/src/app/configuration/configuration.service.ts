import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {RoleType} from "./models/RoleType";
import {Application} from "./models/Application";
import {ApplicationProfile} from "./models/ApplicationProfile";
import {UserRole} from "../user/models/UserRole";

@Injectable()
export class ConfigurationService {

  constructor(private http: Http) { }

  getRoleTypes(): Observable<RoleType[]> {
    const vm = this;
    return vm.http.get('api/roleType/getRoles')
      .map((response) => response.json());
  }

  saveRoleType(roleType : RoleType): Observable<any> {
    const vm = this;
    return vm.http.post('api/roleType/saveRoleType', roleType)
      .map((response) => response.json());
  }

  getApplications(): Observable<Application[]> {
    const vm = this;
    return vm.http.get('api/application/getApplications')
      .map((response) => response.json());
  }

  saveApplication(application : Application, userRoleId: string): Observable<any> {
    const vm = this;
    console.log(application);
    let params = new URLSearchParams();
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/application/saveApplication', application, {search: params})
      .map((response) => response.text());
  }

  deleteApplication(applicationId: string, userRoleId: string): Observable<any> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('applicationId', applicationId);
    params.set('userRoleId', userRoleId);
    return vm.http.delete('api/application/deleteApplication', {search: params})
      .map((response) => response.text());
  }

  getApplicationProfiles(applicationId: string): Observable<ApplicationProfile[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('applicationId', applicationId);
    return vm.http.get('api/applicationProfile/getApplicationProfiles', {search: params})
      .map((response) => response.json());
  }

  saveApplicationProfiles(profiles: ApplicationProfile[], userRoleId: string): Observable<string> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/applicationProfile/saveApplicationProfiles', profiles, {search: params})
      .map((response) => response.text());
  }

}
