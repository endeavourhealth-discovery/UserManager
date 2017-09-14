import { Injectable } from '@angular/core';
import {URLSearchParams, Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {Cohort} from './models/Cohort';
import {Dpa} from '../data-processing-agreement/models/Dpa';

@Injectable()
export class CohortService {

  constructor(private http: Http) { }

  getAllCohorts(): Observable<Cohort[]> {
    const vm = this;
    return vm.http.get('api/cohort')
      .map((response) => response.json());
  }

  getCohort(uuid: string): Observable<Cohort> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/cohort', { search : params })
      .map((response) => response.json());
  }

  saveCohort(cohort: Cohort): Observable<any> {
    const vm = this;
    return vm.http.post('api/cohort', cohort)
      .map((response) => response.text());
  }

  deleteCohort(uuid: string): Observable<any> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.delete('api/cohort', { search : params })
      .map((response) => response.text());
  }

  search(searchData: string): Observable<Cohort[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('searchData', searchData);
    return vm.http.get('api/cohort', { search : params })
      .map((response) => response.json());
  }

  getLinkedDpas(uuid: string):  Observable<Dpa[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/cohort/dpas', { search : params })
      .map((response) => response.json());
  }

}
