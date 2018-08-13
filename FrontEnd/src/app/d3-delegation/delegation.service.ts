import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {DelegationData} from "./models/DelegationData";
import {Delegation} from "./models/Delegation";
import {Organisation} from "../organisation/models/Organisation";
import {DelegationRelationship} from "./models/DelegationRelationship";
import {DelegatedOrganisation} from "./models/DelegatedOrganisation";

@Injectable()
export class DelegationService {

  private selectedOrganisation: string;

  constructor(private http: Http) { }

  updateSelectedOrganisation(organisationId: string) {
    this.selectedOrganisation = organisationId;
  }

  getSelectedOrganisation() {
    return this.selectedOrganisation;
  }

  getDelegationRelationships(delegationId: string): Observable<DelegationRelationship[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('delegationId', delegationId);
    return vm.http.get('api/delegationRelationship/get', {search: params})
      .map((response) => response.json());
  }

  getTreeData(delegationId: string): Observable<DelegationData> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('delegationId', delegationId);
    return vm.http.get('api/delegationRelationship/getd3', {search: params})
      .map((response) => response.json());
  }

  getDelegations(organisationId: string = null): Observable<Delegation[]> {
    const vm = this;
    let params = new URLSearchParams();
    if (organisationId != null) {
      params.set('organisationId', organisationId);
    }
    return vm.http.get('api/delegation/get', {search: params})
      .map((response) => response.json());
  }

  getDelegatedOrganisations(organisationId: string): Observable<DelegatedOrganisation[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('organisationId', organisationId);
    return vm.http.get('api/delegation/getDelegatedOrganisations', {search: params})
      .map((response) => response.json());
  }

  getGodModeOrganisations(): Observable<DelegatedOrganisation[]> {
    const vm = this;
    return vm.http.get('api/delegationRelationship/getGodModeOrganisations')
      .map((response) => response.json());
  }

  saveRelationship(relationship: DelegationRelationship, userRoleId: string): Observable<any> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/delegationRelationship/saveRelationship', relationship, {search: params})
      .map((response) => response.text());
  }

  saveDelegation(delegation: Delegation, userRoleId: string): Observable<any> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('userRoleId', userRoleId);
    return vm.http.post('api/delegation/saveDelegation', delegation, {search: params})
      .map((response) => response.text());
  }

  deleteDelegation(delegation: string, userRoleId: string): Observable<any> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('delegationId', delegation);
    params.set('userRoleId', userRoleId);
    return vm.http.delete('api/delegation/deleteDelegation', {search: params})
      .map((response) => response.text());
  }

}
