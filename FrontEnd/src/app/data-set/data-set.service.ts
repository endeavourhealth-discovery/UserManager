import { Injectable } from '@angular/core';
import {URLSearchParams, Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {DataSet} from './models/Dataset';
import {Dpa} from '../data-processing-agreement/models/Dpa';

@Injectable()
export class DataSetService {

  constructor(private http: Http) { }

  getAllDataSets(): Observable<DataSet[]> {
    const vm = this;
    return vm.http.get('api/dataSet')
      .map((response) => response.json());
  }

  getDataSet(uuid: string): Observable<DataSet> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dataSet', { search : params })
      .map((response) => response.json());
  }

  saveDataSet(dataset: DataSet): Observable<any> {
    const vm = this;
    return vm.http.post('api/dataSet', dataset)
      .map((response) => response.text());
  }

  deleteDataSet(uuid: string): Observable<any> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.delete('api/dataSet', { search : params })
      .map((response) => response.text());
  }

  search(searchData: string): Observable<DataSet[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('searchData', searchData);
    return vm.http.get('api/dataSet', { search : params })
      .map((response) => response.json());
  }

  getLinkedDpas(uuid: string):  Observable<Dpa[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dataSet/dpas', { search : params })
      .map((response) => response.json());
  }

}
