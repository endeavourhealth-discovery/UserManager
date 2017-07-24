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

  public static open(modalService: NgbModal, organisations: Organisation[], searchType: string) {
    const modalRef = modalService.open(OrganisationPickerComponent, { backdrop : 'static'});
    modalRef.componentInstance.resultData = jQuery.extend(true, [], organisations);
    modalRef.componentInstance.searchType = searchType;

    return modalRef;
  }

  constructor(public activeModal: NgbActiveModal,
              private log: LoggerService,
              private organisationService: OrganisationService) { }

  ngOnInit() {
  }

  private search() {
    let vm = this;
    if (vm.searchData.length < 3)
      return;
    vm.organisationService.search(vm.searchData, vm.searchType)
      .subscribe(
        (result) => vm.searchResults = result,
        (error) => vm.log.error(error)
      );
  }

  private addToSelection(match: Organisation) {
    if ($.grep(this.resultData, function(o: Organisation) { return o.uuid === match.uuid; }).length === 0)
      this.resultData.push(match);
  }

  private removeFromSelection(match: Organisation) {
    let index = this.resultData.indexOf(match, 0);
    if (index > -1)
      this.resultData.splice(index, 1);
  }

  ok() {
    this.activeModal.close(this.resultData);
    console.log('OK Pressed');
  }

  cancel() {
    this.activeModal.dismiss('cancel');
    console.log('Cancel Pressed');
  }

}
