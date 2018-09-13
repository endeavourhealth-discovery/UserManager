import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {Organisation} from "./models/Organisation";
import {Project} from "../configuration/models/Project";

@Injectable()
export class OrganisationService {

  constructor(private http: Http) { }

  search(searchData : string): Observable<Organisation[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('searchData', searchData);
    return vm.http.get('api/organisation/search', { search : params })
      .map((response) => response.json());
  }

  getProjectsForOrganisation(organisationId : string): Observable<Project[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('organisationId', organisationId);
    return vm.http.get('api/organisation/organisationProjects', { search : params })
      .map((response) => response.json());
  }

}
