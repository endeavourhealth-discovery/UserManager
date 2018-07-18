import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {DelegationData} from "./models/DelegationData";
import {Delegation} from "./models/Delegation";

@Injectable()
export class DelegationService {

  constructor(private http: Http) { }

  getDelegationRelationships(delegationId: string): Observable<DelegationData> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('delegationId', delegationId);
    return vm.http.get('api/delegationRelationship/get', {search: params})
      .map((response) => response.json());
  }

  getDelegations(): Observable<Delegation[]> {
    const vm = this;
    return vm.http.get('api/delegation/get')
      .map((response) => response.json());
  }

}
