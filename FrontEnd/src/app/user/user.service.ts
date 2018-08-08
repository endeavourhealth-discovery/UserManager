import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {User} from "./models/User";
import {Observable} from "rxjs/Observable";
import {UserRole} from "./models/UserRole";

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

  saveUser(editedUser: User, editMode: Boolean): Observable<User> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('editMode', editMode == true ? "1":"0");
    return vm.http.post('api/user/users/save', editedUser, {search: params})
      .map((response) => response.json());
  }

  deleteUser(userId: string) {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userId', userId);
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

}
