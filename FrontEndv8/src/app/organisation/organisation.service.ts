import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs/Observable";
import {Organisation} from "./models/Organisation";
import {Project} from "../configuration/models/Project";

@Injectable()
export class OrganisationService {

  constructor(private http: HttpClient) { }

  search(searchData : string): Observable<Organisation[]> {
    const url = 'api/organisation/search';
    let params = new HttpParams();
    if (searchData) params.append('searchData', searchData);
    return this.http.get<Organisation[]>(url,{params});
  }

  getProjectsForOrganisation(organisationId : string): Observable<Project[]> {
    const url = 'api/organisation/organisationProjects';
    let params = new HttpParams();
    if (organisationId) params.append('organisationId', organisationId);
    return this.http.get<Project[]>(url, {params});
  }

}
