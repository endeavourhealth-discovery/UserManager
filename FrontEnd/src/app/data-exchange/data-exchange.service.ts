import { Injectable } from '@angular/core';
import {URLSearchParams, Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {DataExchange} from './models/DataExchange';
import {Dpa} from '../data-processing-agreement/models/Dpa';
import {Dsa} from '../data-sharing-agreement/models/Dsa';
import {DataFlow} from "../data-flow/models/DataFlow";

@Injectable()
export class DataExchangeService {

  constructor(private http: Http) { }

  getAllDataExchanges(): Observable<DataExchange[]> {
    const vm = this;
    return vm.http.get('api/dataExchange')
      .map((response) => response.json());
  }

  getDataExchange(uuid: string): Observable<DataExchange> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dataExchange', { search : params })
      .map((response) => response.json());
  }

  saveDataExchange(cohort: DataExchange): Observable<any> {
    const vm = this;
    return vm.http.post('api/dataExchange', cohort)
      .map((response) => response.text());
  }

  deleteDataExchange(uuid: string): Observable<any> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.delete('api/dataExchange', { search : params })
      .map((response) => response.text());
  }

  search(searchData: string): Observable<DataExchange[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('searchData', searchData);
    return vm.http.get('api/dataExchange', { search : params })
      .map((response) => response.json());
  }

  getLinkedDataFlows(uuid: string):  Observable<DataFlow[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dataExchange/dataFlows', { search : params })
      .map((response) => response.json());
  }

}
