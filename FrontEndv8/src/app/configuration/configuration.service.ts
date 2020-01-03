import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {ApplicationPolicy} from "../models/ApplicationPolicy";
import {Application} from "../models/Application";
import {ApplicationProfile} from "../models/ApplicationProfile";
import {ApplicationPolicyAttribute} from "../models/ApplicationPolicyAttribute";

@Injectable()
export class ConfigurationService {

  constructor(private http: HttpClient) { }

  getApplicationPolicies(): Observable<ApplicationPolicy[]> {
    const url = 'api/applicationPolicy/getApplicationPolicies';
    return this.http.get<ApplicationPolicy[]>(url);
  }

  getNonSuperUserApplicationPolicies(): Observable<ApplicationPolicy[]> {
    const url = 'api/applicationPolicy/getNonSuperUserApplicationPolicies';
    return this.http.get<ApplicationPolicy[]>(url);
  }

  deleteApplicationPolicy(applicationPolicyId: string, userRoleId: string): Observable<any> {
    const url = 'api/applicationPolicy/deleteApplicationPolicy';
    let params = new HttpParams();
    if (applicationPolicyId) params = params.append('applicationPolicyId', applicationPolicyId);
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.get<any>(url,{params});
  }

  saveApplicationPolicy(roleType : ApplicationPolicy, userRoleId: string): Observable<any> {
    const url = 'api/applicationPolicy/saveApplicationPolicy';
    let params = new HttpParams();
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.post<any>(url, roleType,{params});
  }

  getApplications(): Observable<Application[]> {
    const url = 'api/application/getApplications';
    return this.http.get<Application[]>(url);
  }

  saveApplication(application : Application, userRoleId: string): Observable<any> {
    const url = 'api/application/saveApplication';
    let params = new HttpParams();
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.post<any>(url, application, {params});
  }

  deleteApplication(applicationId: string, userRoleId: string): Observable<any> {
    const url = 'api/application/deleteApplication';
    let params = new HttpParams();
    if (applicationId) params = params.append('applicationId', applicationId);
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.delete<any>(url, {params});
  }

  getApplicationProfiles(applicationId: string): Observable<ApplicationProfile[]> {
    const url = 'api/applicationProfile/getApplicationProfiles';
    let params = new HttpParams();
    if (applicationId) params = params.append('applicationId', applicationId);
    return this.http.get<ApplicationProfile[]>(url, {params});
  }

  saveApplicationProfiles(profiles: ApplicationProfile[], userRoleId: string): Observable<string> {
    const url = 'api/applicationProfile/saveApplicationProfiles';
    let params = new HttpParams();
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.post<string>(url, profiles, {params});
  }

  getRoleTypeAccessProfiles(applicationPolicyId: string): Observable<ApplicationPolicyAttribute[]> {
    const url = 'api/roleTypeAccessProfile/getRoleTypeAccessProfiles';
    let params = new HttpParams();
    if (applicationPolicyId) params = params.append('applicationPolicyId', applicationPolicyId);
    return this.http.get<ApplicationPolicyAttribute[]>(url,{params});
  }

  saveRoleTypeAccessProfiles(roleProfiles: ApplicationPolicyAttribute[], userRoleId: string): Observable<string> {
    const url = 'api/roleTypeAccessProfile/saveRoleTypeAccessProfiles';
    let params = new HttpParams();
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    //console.log('hete');
    return this.http.post<string>(url, roleProfiles,{params});
  }

  flushCache(): Observable<any> {
    const urlOne = 'api/application/flushCache';
    this.http.get(urlOne);
    const urlTwo = 'api/userManager/flushCache';
    return this.http.get<any>(urlTwo);
  }

}
