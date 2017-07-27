import { Injectable } from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {DataSharingSummary} from './models/DataSharingSummary';
import {OrganisationManagerStatistics} from '../organisation/models/OrganisationManagerStatistics';

@Injectable()
export class DataSharingSummaryService {

  constructor(private http: Http) { }

  getAllDataSharingSummaries(): Observable<DataSharingSummary[]> {
    const vm = this;
    return vm.http.get('api/dataSharingSummary')
      .map((response) => response.json());
  }

  getDataSharingSummary(uuid: string): Observable<DataSharingSummary> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/dataSharingSummary', { search : params })
      .map((response) => response.json());
  }

  saveDataSharingSummary(cohort: DataSharingSummary): Observable<any> {
    const vm = this;
    return vm.http.post('api/dataSharingSummary', cohort)
      .map((response) => response.json());
  }

  deleteDataSharingSummary(uuid: string): Observable<any> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.delete('api/dataSharingSummary', { search : params })
      .map((response) => response.json());
  }

  search(searchData: string): Observable<DataSharingSummary[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('searchData', searchData);
    return vm.http.get('api/dataSharingSummary', { search : params })
      .map((response) => response.json());
  }

}
