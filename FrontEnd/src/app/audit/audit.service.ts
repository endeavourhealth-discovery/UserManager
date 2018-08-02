import { Injectable } from '@angular/core';
import {Http, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {AuditSummary} from "./models/AuditSummary";

@Injectable()
export class AuditService {

  constructor(private http: Http) { }

  getAuditSummary(): Observable<AuditSummary[]> {
    const vm = this;
    return vm.http.get('api/audit/getAudit')
      .map((response) => response.json());
  }

  getAuditDetails(id: string): Observable<any> {
    const vm = this;
    let params = new URLSearchParams();
    console.log(id);
    params.set('auditId', id);
    return vm.http.get('api/audit/getAuditDetail', {search: params})
      .map((response) => response.json());
  }

}
