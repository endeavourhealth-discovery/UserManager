import {Component, Input, OnInit} from '@angular/core';
import {DataExchange} from '../models/DataExchange';
import {DataExchangeService} from '../data-exchange.service';
import {NgbModal, NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {LoggerService} from 'eds-angular4';

@Component({
  selector: 'app-dataflow-picker',
  templateUrl: './data-exchange-picker.component.html',
  styleUrls: ['./data-exchange-picker.component.css']
})
export class DataExchangePickerComponent implements OnInit {
  @Input() resultData: DataExchange[];
  searchData: string;
  searchResults: DataExchange[];

  public static open(modalService: NgbModal, dataFlows: DataExchange[]) {
    const modalRef = modalService.open(DataExchangePickerComponent, { backdrop : 'static'});
    modalRef.componentInstance.resultData = Object.assign([], dataFlows);

    return modalRef;
  }

  constructor(public activeModal: NgbActiveModal,
              private log: LoggerService,
              private dataFlowService: DataExchangeService) { }

  ngOnInit() {
  }

  search() {
    const vm = this;
    if (vm.searchData.length < 3) {
      return;
    }
    vm.dataFlowService.search(vm.searchData)
      .subscribe(
        (result) => vm.searchResults = result,
        (error) => vm.log.error(error)
      );
  }

  private addToSelection(match: DataExchange) {
    if (!this.resultData.some(x => x.uuid === match.uuid)) {
      this.resultData.push(match);
    }
  }

  private removeFromSelection(match: DataExchange) {
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
