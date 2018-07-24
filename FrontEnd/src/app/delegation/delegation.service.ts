import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {DelegationData} from "./models/DelegationData";
import {Delegation} from "./models/Delegation";
import {Organisation} from "../organisation/models/Organisation";

@Injectable()
export class DelegationService {

  private selectedOrganisation: string = '995fbac1-3d99-3318-a65b-2581f3301d0e';
  private selectedDelegation: string = '416fae5a-88e1-11e8-91d9-80fa5b320513';

  constructor(private http: Http) { }

  updateSelectedDelegation(delegationId: string) {
    this.selectedDelegation = delegationId;
  }

  updateSelectedOrganisation(organisationId: string) {
    this.selectedOrganisation = organisationId;
  }

  getSelectedOrganisation() {
    return this.selectedOrganisation;
  }

  getSelectedDelegation() {
    return this.selectedDelegation;
  }

  getDelegationRelationships(delegationId: string): Observable<DelegationData> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('delegationId', delegationId);
    return vm.http.get('api/delegationRelationship/get', {search: params})
      .map((response) => response.json());
  }

  getDelegationRelationshipsD3(delegationId: string): Observable<DelegationData> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('delegationId', delegationId);
    return vm.http.get('api/delegationRelationship/getd3', {search: params})
      .map((response) => response.json());
  }

  getDelegations(): Observable<Delegation[]> {
    const vm = this;
    return vm.http.get('api/delegation/get')
      .map((response) => response.json());
  }

  getDelegatedOrganisations(organisationId: string, delegationId: string): Observable<Organisation[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userId', 'tete');
    params.set('delegationId', delegationId);
    params.set('organisationId', organisationId);
    return vm.http.get('api/delegation/getDelegatedOrganisations', {search: params})
      .map((response) => response.json());
  }

}
