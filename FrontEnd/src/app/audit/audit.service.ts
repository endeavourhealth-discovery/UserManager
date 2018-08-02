import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {AuditSummary} from "./models/AuditSummary";

@Injectable()
export class AuditService {

  constructor(private http: Http) { }

  getAuditSummary(pageNumber: number, pageSize: number,
                  organisationId: string = null, userId: string = null): Observable<AuditSummary[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('pageNumber', pageNumber.toString());
    params.set('pageSize', pageSize.toString());
    if (organisationId != null) {
      params.set('organisationId', organisationId);
    }
    if (userId != null) {
      params.set('userId', userId);
    }

    return vm.http.get('api/audit/getAudit', {search: params})
      .map((response) => response.json());
  }

  getAuditCount(organisationId: string = null, userId: string = null): Observable<number> {
    const vm = this;
    let params = new URLSearchParams();
    if (organisationId != null) {
      params.set('organisationId', organisationId);
    }
    if (userId != null) {
      params.set('userId', userId);
    }

    return vm.http.get('api/audit/auditCount')
      .map((response) => response.json());
  }

  getAuditDetails(id: string): Observable<any> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('auditId', id);
    return vm.http.get('api/audit/getAuditDetail', {search: params})
      .map((response) => response.json());
  }

}
