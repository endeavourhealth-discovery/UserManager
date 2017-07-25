import { Injectable } from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {DataFlow} from '../data-flow/models/DataFlow';
import {Dpa} from './models/Dpa';
import {DataSet} from '../data-set/models/Dataset';
import {Cohort} from "../cohort/models/Cohort";

@Injectable()
export class DataProcessingAgreementService {

  constructor(private http: Http) { }

  getAllDpas(): Observable<Dpa[]> {
    const vm = this;
    return vm.http.get('api/dpa')
      .map((response) => response.json());
  }

  getDpa(uuid: string): Observable<Dpa> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dpa', { search : params })
      .map((response) => response.json());
  }

  saveDpa(dpa: Dpa): Observable<any> {
    const vm = this;
    return vm.http.post('api/dpa', dpa);
  }

  deleteDpa(uuid: string): Observable<any> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.delete('api/dpa', { search : params })
      .map((response) => response.json());
  }

  search(searchData: string): Observable<Dpa[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('searchData', searchData);
    return vm.http.get('api/dpa', { search : params })
      .map((response) => response.json());
  }

  getLinkedDataFlows(uuid: string):  Observable<DataFlow[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dpa/dataflows', { search : params })
      .map((response) => response.json());
  }

  getLinkedCohorts(uuid: string):  Observable<Cohort[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dpa/cohorts', { search : params })
      .map((response) => response.json());
  }

  getLinkedDataSets(uuid: string):  Observable<DataSet[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dpa/datasets', { search : params })
      .map((response) => response.json());
  }

}