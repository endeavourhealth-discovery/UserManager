import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {DelegationData} from "./models/DelegationData";
import {Delegation} from "./models/Delegation";
import {DelegationRelationship} from "./models/DelegationRelationship";
import {DelegatedOrganisation} from "./models/DelegatedOrganisation";

@Injectable()
export class DelegationService {

  private selectedOrganisation: string;

  constructor(private http: HttpClient) { }

  updateSelectedOrganisation(organisationId: string) {
    this.selectedOrganisation = organisationId;
  }

  getSelectedOrganisation() {
    return this.selectedOrganisation;
  }

  getDelegationRelationships(delegationId: string): Observable<DelegationRelationship[]> {
    const url = 'api/delegationRelationship/get';
    let params = new HttpParams();
    params.set('delegationId', delegationId);
    return this.http.get<DelegationRelationship[]>(url,{params});
  }

  getTreeData(delegationId: string): Observable<DelegationData> {
    const url = 'api/delegationRelationship/getd3';
    let params = new HttpParams();
    if (delegationId) params = params.append('delegationId', delegationId);
    return this.http.get<DelegationData>(url,{params});
  }

 getDelegations(organisationId: string = null): Observable<Delegation[]> {
    const url = 'api/delegation/get';
    let params = new HttpParams();
    if (organisationId != null) {
      params = params.append('organisationId', organisationId);
    }
    return this.http.get<Delegation[]>(url,{params});
  }

  getDelegatedOrganisations(organisationId: string): Observable<DelegatedOrganisation[]> {
    const url = 'api/delegation/getDelegatedOrganisations';
    let params = new HttpParams();
    if (organisationId) params = params.append('organisationId', organisationId);
    return this.http.get<DelegatedOrganisation[]>(url,{params});
  }

  getGodModeOrganisations(): Observable<DelegatedOrganisation[]> {
    const url = 'api/delegationRelationship/getGodModeOrganisations'
    return this.http.get<DelegatedOrganisation[]>(url);
  }

  saveRelationship(relationship: DelegationRelationship, userRoleId: string): Observable<any> {
    const url = 'api/delegationRelationship/saveRelationship';
    let params = new HttpParams();
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.post<any>(url, relationship,{params});
  }

  saveDelegation(delegation: Delegation, userRoleId: string): Observable<any> {
    const url = 'api/delegation/saveDelegation';
    let params = new HttpParams();
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.post<any>(url, delegation,{params});
  }

  deleteDelegation(delegation: string, userRoleId: string): Observable<any> {
    const url = 'api/delegation/deleteDelegation';
    let params = new HttpParams();
    if (delegation) params = params.append('delegationId', delegation);
    if (userRoleId) params = params.append('userRoleId', userRoleId);
    return this.http.delete<any>(url,{params});
  }

}
