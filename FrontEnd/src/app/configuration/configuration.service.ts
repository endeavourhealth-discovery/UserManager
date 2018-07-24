import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {RoleType} from "./models/RoleType";

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

}
