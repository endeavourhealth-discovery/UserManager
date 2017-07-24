import { Injectable } from '@angular/core';
import {URLSearchParams, Http} from '@angular/http';
import {Observable} from 'rxjs';
import {DataFlow} from './models/DataFlow';
import {Dpa} from '../dpa/models/Dpa';
import {Dsa} from '../dsa/models/Dsa';

@Injectable()
export class DataFlowService {

  constructor(private http: Http) { }

  getAllDataFlows(): Observable<DataFlow[]> {
    const vm = this;
    return vm.http.get('api/dataFlow')
      .map((response) => response.json());
  }

  getDataFlow(uuid: string): Observable<DataFlow> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dataFlow', { search : params })
      .map((response) => response.json());
  }

  saveDataFlow(cohort: DataFlow): Observable<any> {
    const vm = this;
    return vm.http.post('api/dataFlow', cohort)
      .map((response) => response.json());
  }

  deleteDataFlow(uuid: string): Observable<any> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.delete('api/dataFlow', { search : params })
      .map((response) => response.json());
  }

  search(searchData: string): Observable<DataFlow[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('searchData', searchData);
    return vm.http.get('api/dataFlow', { search : params })
      .map((response) => response.json());
  }

  getLinkedDpas(uuid: string):  Observable<Dpa[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dataFlow/dpas', { search : params })
      .map((response) => response.json());
  }

  getLinkedDsas(uuid: string):  Observable<Dsa[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dataFlow/dsas', { search : params })
      .map((response) => response.json());
  }

}
