import {Injectable} from '@angular/core';
import {URLSearchParams, Http} from '@angular/http';
import {Observable} from 'rxjs';
import {Organisation} from './models/Organisation';
import {Region} from '../region/models/Region';
import {Address} from './models/Address';
import {Marker} from '../region/models/Marker';
import {OrganisationManagerStatistics} from './models/OrganisationManagerStatistics';
import {FileUpload} from './models/FileUpload';

@Injectable()
export class OrganisationService  {

  constructor(private http: Http) {  }

  getOrganisation(uuid: string): Observable<Organisation> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/organisationManager', { search : params })
      .map((response) => response.json());
  }

  getOrganisationRegions(uuid: string):  Observable<Region[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/organisationManager/regions', { search : params })
      .map((response) => response.json());
  }

  getChildOrganisations(uuid: string):  Observable<Organisation[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/organisationManager/childOrganisations', { search : params })
      .map((response) => response.json());
  }

  getParentOrganisations(uuid: string, isService: number):  Observable<Organisation[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    params.set('isService', isService.toString());
    return vm.http.get('api/organisationManager/parentOrganisations', { search : params })
      .map((response) => response.json());
  }

  getServices(uuid: string):  Observable<Organisation[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/organisationManager/services', { search : params })
      .map((response) => response.json());
  }

  getOrganisationAddresses(uuid: string):  Observable<Address[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/organisationManager/addresses', { search : params })
      .map((response) => response.json());
  }

  saveOrganisation(organisation: Organisation): Observable<any> {
    const vm = this;
    return vm.http.post('api/organisationManager', organisation)
      .map((response) => response.json());
  }

  deleteOrganisation(uuid: string): Observable<any> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.delete('api/organisationManager', { search : params })
      .map((response) => response.json());
  }

  search(searchData: string, searchType: string,
         pageNumber: number = 1, pageSize: number = 20,
         orderColumn: string = 'name', descending: boolean = false ): Observable<Organisation[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('searchData', searchData);
    params.set('searchType', searchType);
    params.set('pageNumber', pageNumber.toString());
    params.set('pageSize', pageSize.toString());
    params.set('orderColumn', orderColumn);
    params.set('descending', descending.toString());
    return vm.http.get('api/organisationManager', { search : params })
      .map((response) => response.json());
  }

  getOrganisationMarkers(uuid: string): Observable<Marker[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/organisationManager/markers', { search : params })
      .map((response) => response.json());
  }

  getUpdatedBulkOrganisations(): Observable<any> {
    const vm = this;
    return vm.http.get('api/organisationManager/editedBulks')
      .map((response) => response.json());
  }

  getConflictedOrganisations(): Observable<any> {
    const vm = this;
    return vm.http.get('api/organisationManager/conflicts')
      .map((response) => response.json());
  }

  deleteBulks(): Observable<any> {
    const vm = this;
    return vm.http.delete('api/organisationManager/deleteBulks')
      .map((response) => response.json());
  }

  startUpload(): Observable<any> {
    const vm = this;
    return vm.http.get('api/organisationManager/startUpload', {withCredentials : true})
      .map((response) => response.text());
  }

  endUpload(): Observable<any> {
    const vm = this;
    return vm.http.get('api/organisationManager/endUpload')
      .map((response) => response.text());
  }

  uploadCsv(fileToUpload: FileUpload): Observable<any> {
    const vm = this;
    return vm.http.post('api/organisationManager/upload', fileToUpload);
  }

  getStatistics(type: string): Observable<OrganisationManagerStatistics[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('type', type);
    return vm.http.get('api/organisationManager/statistics', { search : params })
      .map((response) => response.json());
  }

  getTotalCount(expression: string, searchType: string): Observable<number> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('expression', expression);
    params.set('searchType', searchType);
    return vm.http.get('api/organisationManager/searchCount', { search : params })
      .map((response) => response.json());
  }

}
