import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {ApplicationPolicy} from "./models/ApplicationPolicy";
import {Application} from "./models/Application";
import {ApplicationProfile} from "./models/ApplicationProfile";
import {ApplicationPolicyAttribute} from "./models/ApplicationPolicyAttribute";

@Injectable()
export class ConfigurationService {

  constructor(private http: Http) { }

  getApplicationPolicies(): Observable<ApplicationPolicy[]> {
    const vm = this;
    return vm.http.get('api/applicationPolicy/getApplicationPolicies')
      .map((response) => response.json());
  }

  deleteApplicationPolicy(applicationPolicyId: string, userRoleId: string): Observable<any> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('applicationPolicyId', applicationPolicyId);
    params.set('userRoleId', userRoleId);
    return vm.http.delete('api/applicationPolicy/deleteApplicationPolicy', {search: params})
      .map((response) => response.text());
  }

  saveApplicationPolicy(roleType : ApplicationPolicy, userRoleId: string): Observable<any> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/applicationPolicy/saveApplicationPolicy', roleType, {search: params})
      .map((response) => response.text());
  }

  getApplications(): Observable<Application[]> {
    const vm = this;
    return vm.http.get('api/application/getApplications')
      .map((response) => response.json());
  }

  saveApplication(application : Application, userRoleId: string): Observable<any> {
    const vm = this;
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

  getRoleTypeAccessProfiles(applicationPolicyId: string): Observable<ApplicationPolicyAttribute[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('applicationPolicyId', applicationPolicyId);
    return vm.http.get('api/roleTypeAccessProfile/getRoleTypeAccessProfiles', {search: params})
      .map((response) => response.json());
  }

  saveRoleTypeAccessProfiles(roleProfiles: ApplicationPolicyAttribute[], userRoleId: string): Observable<string> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/roleTypeAccessProfile/saveRoleTypeAccessProfiles', roleProfiles, {search: params})
      .map((response) => response.text());
  }

}
