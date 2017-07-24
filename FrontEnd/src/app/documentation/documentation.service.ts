import { Injectable } from '@angular/core';
import {Http} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {Documentation} from './models/Documentation';

@Injectable()
export class DocumentationService {

  constructor(private http: Http) { }


  getDocument(uuid: string): Observable<Documentation> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.get('api/documentation', { search : params })
      .map((response) => response.json());
  }

  getAllAssociatedDocuments(parentUuid: string, parentType: string ): Observable<Documentation[]> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('parentUuid', parentUuid);
    params.set('parentType', parentType);
    return vm.http.get('api/documentation/associated', { search : params })
      .map((response) => response.json());
  }

  deleteDocument(uuid: string): Observable<any> {
    const vm = this;
    const params = new URLSearchParams();
    params.set('uuid', uuid);
    return vm.http.delete('api/documentation', { search : params })
      .map((response) => response.json());
  }

}
