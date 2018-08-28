import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {RoleType} from "./models/RoleType";
import {Application} from "./models/Application";

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
    let params = new URLSearchParams();
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/application/saveApplication', application)
      .map((response) => response.json());
  }

}
