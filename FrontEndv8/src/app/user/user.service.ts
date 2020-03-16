import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {User} from "../models/User";
import {UserAccessProfile} from "../models/UserAccessProfile";
import {UserRegion} from "../models/UserRegion";
import {Region} from "../models/Region";
import {UserProfile} from "../models/UserProfile";
import {UserApplicationPolicy} from "../models/UserApplicationPolicy";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";

@Injectable()
export class UserService {

  constructor(private http: HttpClient) { }

  getUsers(organisationId: string, searchData: string = null, machineUsers : boolean = false): Observable<User[]> {
    let url = 'api/user/users';
    if (machineUsers) {
      url = 'api/user/machineUsers'
    }
    let params = new HttpParams();
    if (organisationId) params = params.append('organisationId', organisationId);
    if (searchData) params = params.append('searchData', searchData);
    return this.http.get<User[]>(url,{params});
  }

  saveUser(editedUser: User, editMode: boolean, userRoleId: string): Observable<User> {
    const url = 'api/user/users/save';
    let params = new HttpParams();
    params = params.append('editMode', editMode == true ? "1":"0");
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.post<User>(url, editedUser,{params});
  }

  saveUserProjects(userRoles: UserProject[], userRoleId: string): Observable<string> {
    const url = 'api/user/users/saveProjects';
    let params = new HttpParams();
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.post<string>(url, userRoles, {params});
  }

  deleteUser(userId: string, userRoleId: string) {
    const url = 'api/user/users/delete';
    let params = new HttpParams();
    if (userId) params = params.append('userId', userId);
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.delete(url, {params});
  }

  getUserRoles(userId: string): Observable<UserProject[]> {
    const url = 'api/userManager/getRoles';
    let params = new HttpParams();
    if (userId) params = params.append('userId', userId);
    return this.http.get<UserProject[]>(url, {params});
  }

  getUser(userId: string): Observable<User> {
    const url = 'api/user/users/user';
    let params = new HttpParams();
    if (userId) params = params.append('userId', userId);
    return this.http.get<User>(url, {params});
  }

  getRoleAccessProfile(roleTypeId: string, organisationId: string): Observable<UserAccessProfile[]> {
    const url = 'api/userBio/getAccessProfile';
    let params = new HttpParams();
    if (roleTypeId) params = params.append('applicationPolicyId', roleTypeId);
    if (organisationId) params = params.append('organisationId', organisationId);
    return this.http.get<UserAccessProfile[]>(url, {params});
  }

  getUserRegion(userId: string): Observable<UserRegion> {
    const url = 'api/user/userRegion';
    let params = new HttpParams();
    if (userId) params = params.append('userId', userId);
    return this.http.get<UserRegion>(url, {params});
  }

  saveUserRegion(userRegion: UserRegion, userProjectId: string): Observable<string> {
    const url = 'api/user/setUserRegion';
    let params = new HttpParams();
    if (userProjectId) params = params.append('userProjectId', userProjectId);
    return this.http.post<string>(url, userRegion, {params});
  }

  getAvailableRegions(): Observable<Region[]> {
    const url = 'api/user/availableRegions';
    return this.http.get<Region[]>(url);
  }

  getUserApplicationPolicy(userId: string): Observable<UserApplicationPolicy> {
    const url = 'api/user/userApplicationPolicy';
    let params = new HttpParams();
    if (userId) params = params.append('userId', userId);
    return this.http.get<UserApplicationPolicy>(url, {params});
  }

  saveUserApplicationPolicy(userApplicationPolicy: UserApplicationPolicy, userProjectId: string): Observable<string> {
    const url = 'api/user/setUserApplicationPolicy';
    let params = new HttpParams();
    if (userProjectId) params = params.append('userProjectId', userProjectId);
    return this.http.post<string>(url, userApplicationPolicy, {params});
  }

  getUserProfile(userId: string): Observable<UserProfile> {
    const url = 'api/userManager/getUserProfile';
    let params = new HttpParams();
    if (userId) params = params.append('userId', userId);
    return this.http.get<UserProfile>(url, {params});
  }

  sendUserPasswordEmail(userId: string, userProjectId: string): Observable<string> {
    const url = 'api/user/sendUpdatePasswordEmail';
    let params = new HttpParams();
    if (userId) params = params.append('userId', userId);
    if (userProjectId) params = params.append('userProjectId', userProjectId);
    return this.http.get<string>(url, {params});
  }

  loadUserProjects(userId: string): Observable<UserProject[]> {
    const vm = this;
    let params = new HttpParams();
    params = params.append('userId', userId);
    return vm.http.get<UserProject[]>('api/userManager/getProjects', {params: params});
  }

}
