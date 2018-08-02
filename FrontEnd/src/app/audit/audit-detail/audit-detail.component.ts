import { Component, OnInit } from '@angular/core';
import {LoggerService} from "eds-angular4";
import {AuditService} from "../audit.service";
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {AuditSummary} from "../models/AuditSummary";
import {DisplayDetails} from "../models/DisplayDetails";

@Component({
  selector: 'app-audit-detail',
  templateUrl: './audit-detail.component.html',
  styleUrls: ['./audit-detail.component.css']
})
export class AuditDetailComponent implements OnInit {
  audit: AuditSummary;
  auditDetails: any;
  displayItems: any;

  constructor(public activeModal: NgbActiveModal,
              public log:LoggerService,
              private auditService: AuditService) { }

  ngOnInit() {
    const vm = this;
    vm.displayItems = vm.getDetailsToShow(vm.audit.itemType);
    vm.getDetails();
  }

  public static open(modalService: NgbModal, audit: AuditSummary) {
    const modalRef = modalService.open(AuditDetailComponent, { backdrop: 'static' });
    modalRef.componentInstance.audit = audit;

    return modalRef;
  }



  getDetails() {
    let vm = this;

    console.log(vm.audit);
    vm.auditService.getAuditDetails(vm.audit.id)
      .subscribe(
        (result) => {
          vm.auditDetails = result;
          console.log(result);
        },
        (error) => {
          vm.log.error('Error loading audit details', error, 'Error');
        }
      );
  }

  ok() {
    this.activeModal.close(true);
  }

  cancel() {
    this.activeModal.dismiss(false);
  }

  getDetailsToShow(itemType: string) {
    const dd = new DisplayDetails();
    switch (itemType) {
      case "Role": return dd.getRoleDisplayDetails();
    }
  }

}
