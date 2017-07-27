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
  }

  getSummaryStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('summary')
      .subscribe(result => {
          vm.summaryStats = result
        },
        error => vm.log.error('Failed to load Data Sharing Summary statistics', error, 'Load Data Sharing Summary statistics')
      );
  }

  getDpaStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('dpa')
      .subscribe(result => {
          vm.dpaStats = result
        },
        error => vm.log.error('Failed to load Data Processing Agreement statistics', error, 'Load Data Processing Agreement statistics')
      );
  }

  getDsaStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('dsa')
      .subscribe(result => {
          vm.dsaStats = result
        },
        error => vm.log.error('Failed to load Data Sharing Agreement statistics', error, 'Load Data Sharing Agreement statistics')
      );
  }

  getDataFlowStatistics() {
    const vm = this;
    vm.organisationService.getStatistics('dataflow')
      .subscribe(result => {
          vm.dataflowStats = result
        },
        error => vm.log.error('Failed to load Data Flow statistics', error, 'Load Data Flow statistics')
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
        error => vm.log.error('Failed to load Dataset statistics', error, 'Load Dataset statistics')
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
    this.router.navigate(['/dataSharingSummary']);
  }

  goToDpa() {
    this.router.navigate(['/dpa']);
  }

  goToDsa() {
    this.router.navigate(['/dsa']);
  }

  goToDataFlow() {
    this.router.navigate(['/dataFlow']);
  }

  goToCohorts() {
    this.router.navigate(['/cohort']);
  }

  goToDataSets() {
    this.router.navigate(['/dataSet']);
  }

}
