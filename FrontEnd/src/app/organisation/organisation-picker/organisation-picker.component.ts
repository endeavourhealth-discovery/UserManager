import { Component, OnInit, Input } from '@angular/core';
import {Organisation} from '../models/Organisation';
import {OrganisationService} from '../organisation.service';
import {LoggerService} from 'eds-angular4';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-organisation-picker',
  templateUrl: './organisation-picker.component.html',
  styleUrls: ['./organisation-picker.component.css']
})
export class OrganisationPickerComponent implements OnInit {
  @Input() resultData: Organisation[];
  searchData: string;
  searchResults: Organisation[];
  searchType: string;
  uuid: string;

  public static open(modalService: NgbModal, organisations: Organisation[], searchType: string, uuid: string = '') {
    const modalRef = modalService.open(OrganisationPickerComponent, { backdrop : 'static'});
    modalRef.componentInstance.resultData = Object.assign([], organisations);
    modalRef.componentInstance.searchType = searchType;
    modalRef.componentInstance.uuid = uuid;

    return modalRef;
  }

  constructor(public activeModal: NgbActiveModal,
              private log: LoggerService,
              private organisationService: OrganisationService) { }

  ngOnInit() {
  }

  search() {
    const vm = this;
    if (vm.searchData.length < 3) {
      return;
    }
    vm.organisationService.search(vm.searchData)
      .subscribe(
        (result) => vm.searchResults = result.filter(function(x) {return x.uuid != vm.uuid; }),
        (error) => vm.log.error(error)
      );
  }

  private addToSelection(match: Organisation) {
    if (!this.resultData.some(x => x.uuid === match.uuid)) {
      match.createSuperUsers = false;
      match.createUsers = false;
      this.resultData.push(match);
    }
  }

  private removeFromSelection(match: Organisation) {
    const index = this.resultData.indexOf(match, 0);
    if (index > -1) {
      this.resultData.splice(index, 1);
    }
  }

  ok() {
    this.activeModal.close(this.resultData);
  }

  cancel() {
    this.activeModal.dismiss('cancel');
  }

  checkAllUsers(ev) {
    this.resultData.forEach(x => x.createUsers = ev.target.checked);
  }

  checkAllSuperUsers(ev) {
    this.resultData.forEach(x => x.createSuperUsers = ev.target.checked);
  }
}
