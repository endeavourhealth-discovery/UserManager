import { Component, OnInit } from '@angular/core';
import {DataSetService} from '../data-set.service';
import {LoggerService, MessageBoxDialog} from 'eds-angular4';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DataSet} from '../models/Dataset';

@Component({
  selector: 'app-data-set',
  templateUrl: './data-set.component.html',
  styleUrls: ['./data-set.component.css']
})
export class DataSetComponent implements OnInit {
  datasets: DataSet[] = [];

  constructor(private $modal: NgbModal,
              private dataSetService: DataSetService,
              private log: LoggerService,
              private router: Router) { }

  ngOnInit() {
    this.getDataSets();
  }

  getDataSets() {

    const vm = this;
    vm.dataSetService.getAllDataSets()
      .subscribe(
        result => vm.datasets = result,
        error => vm.log.error('Failed to load data flows', error, 'Load data flows')
      );
  }

  add() {
    this.router.navigate(['/dataSet', {itemUuid: null, itemAction: 'add'}]);
  }

  edit(item: DataSet) {
    this.router.navigate(['/dataSet', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  delete(item: DataSet) {
    const vm = this;
    MessageBoxDialog.open(vm.$modal, 'Delete Data Set', 'Are you sure you want to delete the data set?', 'Yes', 'No')
      .result.then(
      () => vm.doDelete(item),
      () => vm.log.info('Delete cancelled')
    );
  }

  doDelete(item: DataSet) {
    const vm = this;
    vm.dataSetService.deleteDataSet(item.uuid)
      .subscribe(
        () => {
          const index = vm.datasets.indexOf(item);
          vm.datasets.splice(index, 1);
          vm.log.success('Data set deleted', item, 'Delete Data set');
        },
        (error) => vm.log.error('Failed to delete Data set', error, 'Delete Data flow')
      );
  }

  close() {
    this.router.navigate(['dataSharingOverview']);
  }

}