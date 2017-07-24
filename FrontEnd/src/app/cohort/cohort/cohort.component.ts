import { Component, OnInit } from '@angular/core';
import {CohortService} from '../cohort.service';
import {LoggerService, MessageBoxDialog} from 'eds-angular4';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Cohort} from '../models/Cohort';

@Component({
  selector: 'app-cohort',
  templateUrl: './cohort.component.html',
  styleUrls: ['./cohort.component.css']
})
export class CohortComponent implements OnInit {
  cohorts : Cohort[] = [];

  constructor(private $modal: NgbModal,
              private cohortService: CohortService,
              private log: LoggerService,
              private router: Router) { }

  ngOnInit() {
    this.getCohorts();
  }

  getCohorts() {
    const vm = this;
    vm.cohortService.getAllCohorts()
      .subscribe(
        result => vm.cohorts = result,
        error => vm.log.error('Failed to load cohorts', error, 'Load cohorts')
      );
  }

  add() {
    this.router.navigate(['/cohort', {itemUuid: null, itemAction: 'add'}]);
  }

  edit(item: Cohort) {
    this.router.navigate(['/cohort', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  delete(item: Cohort) {
    const vm = this;
    MessageBoxDialog.open(vm.$modal, 'Delete Cohort', 'Are you sure you want to delete the Cohort?', 'Yes', 'No')
      .result.then(
      () => vm.doDelete(item),
      () => vm.log.info('Delete cancelled')
    );
  }

  doDelete(item: Cohort) {
    const vm = this;
    vm.cohortService.deleteCohort(item.uuid)
      .subscribe(
        () => {
          const index = vm.cohorts.indexOf(item);
          vm.cohorts.splice(index, 1);
          vm.log.success('Cohort deleted', item, 'Delete Cohort');
        },
        (error) => vm.log.error('Failed to delete Cohort', error, 'Delete Cohort')
      );
  }

  close() {
    this.router.navigate(['/dataSharingOverview']);
  }

}
