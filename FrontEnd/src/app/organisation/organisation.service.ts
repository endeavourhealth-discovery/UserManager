import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {Organisation} from "./models/Organisation";

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

}
