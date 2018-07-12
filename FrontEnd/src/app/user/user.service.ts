import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {User} from "./models/User";
import {Observable} from "rxjs/Observable";

@Injectable()
export class UserService {

  constructor(private http: Http) { }

  getUsers(): Observable<User[]> {
    const vm = this;
    return vm.http.get('api/user/users')
      .map((response) => response.json());
  }

}
