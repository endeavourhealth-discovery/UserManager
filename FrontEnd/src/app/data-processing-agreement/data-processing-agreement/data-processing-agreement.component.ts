import { Component, OnInit } from '@angular/core';
import {DataProcessingAgreementService} from '../data-processing-agreement.service';
import {LoggerService, MessageBoxDialog} from 'eds-angular4';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Dpa} from '../models/Dpa';

@Component({
  selector: 'app-data-processing-agreement',
  templateUrl: './data-processing-agreement.component.html',
  styleUrls: ['./data-processing-agreement.component.css']
})
export class DataProcessingAgreementComponent implements OnInit {
  dpas : Dpa[] = [];

  dpaDetailsToShow = new Dpa().getDisplayItems();

  constructor(private $modal: NgbModal,
              private dpaService: DataProcessingAgreementService,
              private log: LoggerService,
              private router: Router) { }

  ngOnInit() {
    this.getDsas();
  }

  getDsas() {
    const vm = this;
    vm.dpaService.getAllDpas()
      .subscribe(
        result => vm.dpas = result,
        error => vm.log.error('Failed to load dpas', error, 'Load dpa')
      );
  }

  add() {
    this.router.navigate(['/dpa', {itemUuid: null, itemAction: 'add'}]);
  }

  edit(item: Dpa) {
    this.router.navigate(['/dpa', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  delete(item: Dpa) {
    const vm = this;
    MessageBoxDialog.open(vm.$modal, 'Delete Data Processing Agreement', 'Are you sure you want to delete the Data Processing Agreement?', 'Yes', 'No')
      .result.then(
      () => vm.doDelete(item),
      () => vm.log.info('Delete cancelled')
    );
  }

  doDelete(item: Dpa) {
    const vm = this;
    vm.dpaService.deleteDpa(item.uuid)
      .subscribe(
        () => {
          const index = vm.dpas.indexOf(item);
          vm.dpas.splice(index, 1);
          vm.log.success('Data Sharing Processing deleted', item, 'Delete Data Processing Agreement');
        },
        (error) => vm.log.error('Failed to delete Data Processing Agreement', error, 'Delete Data Processing Agreement')
      );
  }

  close() {
    this.router.navigate(['/dataSharingOverview']);
  }

}
