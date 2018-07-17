import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {Delegation} from "./models/Delegation";

@Injectable()
export class DelegationService {

  constructor(private http: Http) { }

  getDelegations(delegationId: string): Observable<Delegation> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('delegationId', delegationId);
    return vm.http.get('api/delegation/get', {search: params})
      .map((response) => response.json());
  }


}
