import { Component, OnInit } from '@angular/core';
import {AuditSummary} from "../models/AuditSummary";
import {LoggerService} from "eds-angular4";
import {AuditService} from "../audit.service";
import {AuditDetailComponent} from "../audit-detail/audit-detail.component";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-audit',
  templateUrl: './audit.component.html',
  styleUrls: ['./audit.component.css']
})
export class AuditComponent implements OnInit {
  auditSummaries: AuditSummary[];
  loadingComplete = false;

  constructor(private $modal: NgbModal,
              public log:LoggerService,
              private auditService: AuditService) { }

  ngOnInit() {
    const vm = this;
    vm.getAudit();
  }

  getAudit(){
    let vm = this;
    vm.loadingComplete = false;
    vm.auditService.getAuditSummary()
      .subscribe(
        (result) => {
          vm.auditSummaries = result;
          vm.loadingComplete = true;
        },
        (error) => {
          vm.log.error('Error loading audit data', error, 'Error');
          vm.loadingComplete = true;
        }
      );
  }

  showDetails(audit: AuditSummary) {
    const vm = this;
    AuditDetailComponent.open(vm.$modal, audit)
      .result.then(function
        (result: boolean) {
          return;
    })
  }

}
