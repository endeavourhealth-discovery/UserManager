import { Component, OnInit } from '@angular/core';
import {OrganisationManagerStatistics} from '../../organisation/models/OrganisationManagerStatistics';
import {DataSharingSummaryService} from '../data-sharing-summary.service';
import {LoggerService} from 'eds-angular4';
import {Router} from "@angular/router";
import {OrganisationService} from "../../organisation/organisation.service";

@Component({
  selector: 'app-data-sharing-summary-overview',
  templateUrl: './data-sharing-summary-overview.component.html',
  styleUrls: ['./data-sharing-summary-overview.component.css']
})
export class DataSharingSummaryOverviewComponent implements OnInit {
  private file: File;

  summaryStats: OrganisationManagerStatistics[];
  dpaStats: OrganisationManagerStatistics[];
  dsaStats: OrganisationManagerStatistics[];
  dataflowStats: OrganisationManagerStatistics[];
  cohortStats: OrganisationManagerStatistics[];
  datasetStats: OrganisationManagerStatistics[];
  dataExchangeStats: OrganisationManagerStatistics[];

  constructor(private dataSharingSummaryService: DataSharingSummaryService,
              private organisationService: OrganisationService,
              private log: LoggerService,
              private router: Router) { }

  ngOnInit() {
    this.getOverview();
  }

  getOverview() {
    const vm = this;
    // vm.getSummaryStatistics();
    vm.getDpaStatistics();
    vm.getDsaStatistics();
    vm.getDataFlowStatistics();
    vm.getCohortStatistics();
    vm.getDataSetStatistics();
    vm.getSummaryStatistics();
    vm.getDataExchangeStatistics();
  }

  getSummaryStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('summary')
      .subscribe(result => {
          vm.summaryStats = result
        },
        error => vm.log.error('Failed to load data sharing summary statistics', error, 'Load data sharing summary statistics')
      );
  }

  getDpaStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('dpa')
      .subscribe(result => {
          vm.dpaStats = result
        },
        error => vm.log.error('Failed to load data processing agreement statistics', error, 'Load data processing agreement statistics')
      );
  }

  getDsaStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('dsa')
      .subscribe(result => {
          vm.dsaStats = result
        },
        error => vm.log.error('Failed to load data sharing agreement statistics', error, 'load data sharing agreement statistics')
      );
  }

  getDataFlowStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('dataflow')
      .subscribe(result => {
          vm.dataflowStats = result
        },
        error => vm.log.error('Failed to load data flow statistics', error, 'Load data flow statistics')
      );
  }

  getCohortStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('cohort')
      .subscribe(result => {
          vm.cohortStats = result
        },
        error => vm.log.error('Failed to load Cohort statistics', error, 'Load Cohort statistics')
      );
  }

  getDataSetStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('dataset')
      .subscribe(result => {
          vm.datasetStats = result
        },
        error => vm.log.error('Failed to load dataset statistics', error, 'Load dataset statistics')
      );
  }

  getDataExchangeStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('exchange')
      .subscribe(result => {
          vm.dataExchangeStats = result
        },
        error => vm.log.error('Failed to load data exchange statistics', error, 'Load data exchange statistics')
      );
  }

  fileChange(event) {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.file = fileList[0];
    } else {
      this.file = null;
    }
  }

  goToSummary() {
    this.router.navigate(['/dataSharingSummaries']);
  }

  goToDpa() {
    this.router.navigate(['/dpas']);
  }

  goToDsa() {
    this.router.navigate(['/dsas']);
  }

  goToDataFlow() {
    this.router.navigate(['/dataFlows']);
  }

  goToCohorts() {
    this.router.navigate(['/cohorts']);
  }

  goToDataSets() {
    this.router.navigate(['/dataSets']);
  }

  goToDataExchanges() {
    this.router.navigate(['/dataExchanges']);
  }

}
