import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {User} from "./models/User";
import {Observable} from "rxjs/Observable";
import {UserProject} from "./models/UserProject";
import {UserAccessProfile} from "./models/UserAccessProfile";
import {UserRegion} from "./models/UserRegion";
import {Region} from "./models/Region";

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

  saveUserProjects(userRoles: UserProject[], userRoleId: string): Observable<string> {
    const vm = this;
    let params = new URLSearchParams();
    console.log(userRoles);
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/user/users/saveProjects', userRoles, {search: params})
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

  getUserRoles(userId: string): Observable<UserProject[]> {
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

  getUserRegion(userId: string): Observable<UserRegion> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userId', userId);
    return vm.http.get('api/user/userRegion', {search: params})
      .map((response) => response.json());
  }

  saveUserRegion(userRegion: UserRegion, userProjectId: string): Observable<string> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userProjectId', userProjectId);
    return vm.http.post('api/user/setUserRegion', userRegion, {search: params})
      .map((response) => response.text());
  }

  getAvailableRegions(): Observable<Region[]> {
    const vm = this;
    return vm.http.get('api/user/availableRegions')
      .map((response) => response.json());
  }

}
