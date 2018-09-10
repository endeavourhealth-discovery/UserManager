import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {User} from "./models/User";
import {Observable} from "rxjs/Observable";
import {UserRole} from "./models/UserRole";
import {UserAccessProfile} from "./models/UserAccessProfile";

@Injectable()
export class UserService {

  constructor(private http: Http) { }

  getUsers(organisationId: string, searchData: string = null): Observable<User[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('organisationId', organisationId);
    params.set('searchData', searchData);
    return vm.http.get('api/user/users', {search: params})
      .map((response) => response.json());
  }

  saveUser(editedUser: User, editMode: Boolean, userRoleId: string): Observable<User> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('editMode', editMode == true ? "1":"0");
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/user/users/save', editedUser, {search: params})
      .map((response) => response.json());
  }

  saveUserRoles(userRoles: UserRole[], userRoleId: string): Observable<string> {
    const vm = this;
    let params = new URLSearchParams();
    console.log(userRoles);
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/user/users/saveRoles', userRoles, {search: params})
      .map((response) => response.text());
  }

  deleteUser(userId: string, userRoleId: string) {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userId', userId);
    params.set('userRoleId', userRoleId);
    return vm.http.delete('api/user/users/delete', {search: params})
      .map((response) => response.text());;
  }

  getUserRoles(userId: string): Observable<UserRole[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userId', userId);
    return vm.http.get('api/userManager/getRoles', {search: params})
      .map((response) => response.json());
  }

  getUser(userId: string): Observable<User> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userId', userId);
    return vm.http.get('api/user/users/user', {search: params})
      .map((response) => response.json());
  }

  getRoleAccessProfile(roleTypeId: string, organisationId: string): Observable<UserAccessProfile[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('applicationPolicyId', roleTypeId);
    params.set('organisationId', organisationId);
    return vm.http.get('api/userBio/getAccessProfile', {search: params})
      .map((response) => response.json());
  }

}
