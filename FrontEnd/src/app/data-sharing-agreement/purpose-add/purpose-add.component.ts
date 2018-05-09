import {Component, Input, OnInit} from '@angular/core';
import {Purpose} from '../models/Purpose';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {LoggerService} from 'eds-angular4';

@Component({
  selector: 'app-purpose-add',
  templateUrl: './purpose-add.component.html',
  styleUrls: ['./purpose-add.component.css']
})
export class PurposeAddComponent implements OnInit {
  @Input() resultData: Purpose[];
  @Input() type: string;
  @Input() index: number;
  title = '';
  detail = '';
  editMode = false;

  public static open(modalService: NgbModal, purposes: Purpose[], type : string, index: number) {
    const modalRef = modalService.open(PurposeAddComponent, { backdrop : 'static'});
    modalRef.componentInstance.resultData = purposes;
    modalRef.componentInstance.type = type;
    modalRef.componentInstance.index = index;

    return modalRef;
  }

  constructor(public activeModal: NgbActiveModal,
              private log: LoggerService) {

  }

  ngOnInit() {
    if (this.index > -1) {
      this.editMode = true;
      this.edit();
    }
  }

  Add() {
    const newPurpose: Purpose = new Purpose();
    newPurpose.title = this.title;
    newPurpose.detail = this.detail;
    this.resultData.push(newPurpose);
    this.activeModal.close(this.resultData);
  }

  AddAnother() {
    const newPurpose: Purpose = new Purpose();
    newPurpose.title = this.title;
    newPurpose.detail = this.detail;
    this.resultData.push(newPurpose);
    this.title = '';
    this.detail = '';
  }

  save() {
    const vm = this;
    vm.resultData[vm.index].title = vm.title;
    vm.resultData[vm.index].detail = vm.detail;
    vm.activeModal.close(this.resultData);
  }

  saveAndAddAnother() {
    const vm = this;
    vm.resultData[vm.index].title = vm.title;
    vm.resultData[vm.index].detail = vm.detail;
    this.editMode = false;
    this.title = '';
    this.detail = '';
  }

  edit() {
    const vm = this;
    vm.title = vm.resultData[vm.index].title;
    vm.detail = vm.resultData[vm.index].detail;
  }

  cancel() {
    this.activeModal.dismiss('cancel');
  }

}
