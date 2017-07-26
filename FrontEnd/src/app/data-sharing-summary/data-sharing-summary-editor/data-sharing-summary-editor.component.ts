import { Component, OnInit } from '@angular/core';
import {DataSharingSummary} from '../models/DataSharingSummary';
import {DataSharingSummaryService} from '../data-sharing-summary.service';
import {LoggerService} from 'eds-angular4';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-data-sharing-summary-editor',
  templateUrl: './data-sharing-summary-editor.component.html',
  styleUrls: ['./data-sharing-summary-editor.component.css']
})
export class DataSharingSummaryEditorComponent implements OnInit {
  private paramSubscriber: any;
  dataSharingSummary: DataSharingSummary = <DataSharingSummary>{};

  NatureOfInformation = [
    {num: 0, name : 'Personal'},
    {num: 1, name : 'Personal Sensitive'},
    {num: 2, name : 'Commercial'}
  ];

  FormatTypes = [
    {num: 0, name : 'Removable Media'},
    {num: 1, name : 'Electronic Structured Data'}
  ];

  DataSubjectTypes = [
    {num: 0, name : 'Patient'}
  ];

  ReviewCycles = [
    {num: 0, name : 'Annually'},
    {num: 1, name : 'Monthly'},
    {num: 2, name : 'Weekly'}
  ];

  constructor(private $modal: NgbModal,
              private log: LoggerService,
              private dataSharingSummaryService: DataSharingSummaryService,
              private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.paramSubscriber = this.route.params.subscribe(
      params => {
        this.performAction(params['mode'], params['id']);
      });
  }

  protected performAction(action: string, itemUuid: string) {
    switch (action) {
      case 'add':
        this.create(itemUuid);
        break;
      case 'edit':
        this.load(itemUuid);
        break;
    }
  }

  create(uuid: string) {
    this.dataSharingSummary = {
      name : ''
    } as DataSharingSummary;
  }

  load(uuid: string) {
    const vm = this;
    vm.dataSharingSummaryService.getDataSharingSummary(uuid)
      .subscribe(result =>  {
          vm.dataSharingSummary = result;
        },
        error => vm.log.error('Error loading', error, 'Error')
      );
  }

  save(close: boolean) {
    const vm = this;
    console.log(vm.dataSharingSummary);

    vm.dataSharingSummaryService.saveDataSharingSummary(vm.dataSharingSummary)
      .subscribe(saved => {
          vm.log.success('Item saved', vm.dataSharingSummary, 'Saved');
          if (close) { this.router.navigate(['/dataSharingOverview']); }
        },
        error => vm.log.error('Error saving', error, 'Error')
      );
  }

  close() {
    this.router.navigate(['/dataSharingOverview']);
  }

}
