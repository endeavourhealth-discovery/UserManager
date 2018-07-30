import { Component, OnInit } from '@angular/core';
import {Organisation} from "../../organisation/models/Organisation";
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {OrganisationService} from "../../organisation/organisation.service";
import {LoggerService} from "eds-angular4";
import {Delegation} from "../../delegation/models/Delegation";

@Component({
  selector: 'app-delegation-creator',
  templateUrl: './delegation-creator.component.html',
  styleUrls: ['./delegation-creator.component.css']
})
export class DelegationCreatorComponent implements OnInit {
  resultData : Delegation;
  searchData: string;
  searchResults: Organisation[];
  selectedOrg: Organisation;

  constructor(public activeModal: NgbActiveModal,
              private log: LoggerService,
              private organisationService: OrganisationService) { }

  public static open(modalService: NgbModal) {
    const modalRef = modalService.open(DelegationCreatorComponent, { backdrop : 'static'});

    return modalRef;
  }

  ngOnInit() {
    const vm = this;
    vm.resultData = new Delegation();
    vm.resultData.uuid = '';
    vm.resultData.name = '';
  }

  search() {
    const vm = this;
    if (vm.searchData.length < 3) {
      return;
    }
    vm.organisationService.search(vm.searchData)
      .subscribe(
        (result) => vm.searchResults = result,
        (error) => vm.log.error(error)
      );
  }

  private addToSelection(match: Organisation) {
    this.selectedOrg = match;
  }

  private removeFromSelection(match: Organisation) {
    this.selectedOrg = null;
  }

  ok() {
    this.resultData.rootOrganisation = this.selectedOrg.uuid;
    this.activeModal.close(this.resultData);
    console.log('OK Pressed');
  }

  cancel() {
    this.activeModal.dismiss('cancel');
    console.log('Cancel Pressed');
  }

}
