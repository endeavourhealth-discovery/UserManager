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
  title = '';
  detail = '';

  public static open(modalService: NgbModal, purposes: Purpose[]) {
    const modalRef = modalService.open(PurposeAddComponent, { backdrop : 'static'});
    modalRef.componentInstance.resultData = jQuery.extend(true, [], purposes);

    return modalRef;
  }

  constructor(public activeModal: NgbActiveModal,
              private log: LoggerService) { }

  ngOnInit() {
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

  cancel() {
    this.activeModal.dismiss('cancel');
  }

}
