import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {AuditSummary} from "./models/AuditSummary";

@Injectable()
export class AuditService {

  constructor(private http: Http) { }

  getAuditSummary(pageNumber: number, pageSize: number): Observable<AuditSummary[]> {
    const vm = this;
    let params = new URLSearchParams();
    params.set('pageNumber', pageNumber.toString());
    params.set('pageSize', pageSize.toString());

    return vm.http.get('api/audit/getAudit', {search: params})
      .map((response) => response.json());
  }

  getAuditCount(): Observable<number> {
    const vm = this;

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
