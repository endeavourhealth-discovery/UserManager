import {Component, Input, OnInit} from '@angular/core';
import {DataSharingAgreementService} from '../data-sharing-agreement.service';
import {LoggerService} from 'eds-angular4';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {Dsa} from '../models/Dsa';
import { LinqService } from 'ng2-linq';

@Component({
  selector: 'app-data-sharing-agreement-picker',
  templateUrl: './data-sharing-agreement-picker.component.html',
  styleUrls: ['./data-sharing-agreement-picker.component.css']
})
export class DataSharingAgreementPickerComponent implements OnInit {
  @Input() resultData: Dsa[];
  searchData: string;
  searchResults: Dsa[];

  public static open(modalService: NgbModal, dsas: Dsa[]) {
    const modalRef = modalService.open(DataSharingAgreementPickerComponent, { backdrop : 'static'});
    modalRef.componentInstance.resultData = dsas;

    return modalRef;
  }

  constructor(public activeModal: NgbActiveModal,
              private log: LoggerService,
              private dsaService: DataSharingAgreementService,
              private linq: LinqService) { }

  ngOnInit() {
  }

  private search() {
    const vm = this;
    if (vm.searchData.length < 3) {
      return;
    }
    vm.dsaService.search(vm.searchData)
      .subscribe(
        (result) => vm.searchResults = result,
        (error) => vm.log.error(error)
      );
  }

  private addToSelection(match: Dsa) {
    if (this.linq.Enumerable()
      .From(this.resultData)
      .Where(r => r.uuid === match.uuid).ToArray().length === 0) {
      this.resultData.push(match);
    }
    // if ($.grep(this.resultData, function(o: Dsa) { return o.uuid === match.uuid; }).length === 0) {
    //   this.resultData.push(match);
    // }
  }

  private removeFromSelection(match: Dsa) {
    const index = this.resultData.indexOf(match, 0);
    if (index > -1) {
      this.resultData.splice(index, 1);
    }
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
