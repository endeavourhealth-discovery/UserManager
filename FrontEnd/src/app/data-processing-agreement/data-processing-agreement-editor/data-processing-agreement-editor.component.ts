import { Component, OnInit } from '@angular/core';
import {DataProcessingAgreementService} from '../data-processing-agreement.service';
import {LoggerService} from 'eds-angular4';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DataSet} from '../../data-set/models/Dataset';
import {DataFlow} from '../../data-flow/models/DataFlow';
import {Dpa} from '../models/Dpa';
import {DataflowPickerComponent} from "../../data-flow/dataflow-picker/dataflow-picker.component";
import {Cohort} from "../../cohort/models/Cohort";
import {Documentation} from "../../documentation/models/Documentation";
import {CohortPickerComponent} from "../../cohort/cohort-picker/cohort-picker.component";
import {DataSetPickerComponent} from "../../data-set/data-set-picker/data-set-picker.component";
import {DocumentationService} from "../../documentation/documentation.service";

@Component({
  selector: 'app-data-processing-agreement-editor',
  templateUrl: './data-processing-agreement-editor.component.html',
  styleUrls: ['./data-processing-agreement-editor.component.css']
})
export class DataProcessingAgreementEditorComponent implements OnInit {
  private paramSubscriber: any;
  public accordionClass = 'accordionClass';

  dpa: Dpa = <Dpa>{};
  dataFlows: DataFlow[];
  cohorts: Cohort[];
  dataSets: DataSet[];
  documentations: Documentation[];
  editDisabled = false;
  processor = 'Discovery';
  private file: File;
  pdfSrc: any;
  page = 1;

  status = [
    {num: 0, name: 'Active'},
    {num: 1, name: 'Inactive'}
  ];

  dataFlowDetailsToShow = new DataFlow().getDisplayItems();
  datasetDetailsToShow = new DataSet().getDisplayItems();
  cohortDetailsToShow = new Cohort().getDisplayItems();
  documentDetailsToShow = new Documentation().getDisplayItems();

  constructor(private $modal: NgbModal,
              private log: LoggerService,
              private dpaService: DataProcessingAgreementService,
              private documentationService: DocumentationService,
              private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit() {
    this.paramSubscriber = this.route.params.subscribe(
      params => {
        this.performAction(params['itemAction'], params['itemUuid']);
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
    this.dpa = {
      name: ''
    } as Dpa;
  }

  load(uuid: string) {
    const vm = this;
    vm.dpaService.getDpa(uuid)
      .subscribe(result => {
          vm.dpa = result;
          vm.getLinkedDataFlows();
          vm.getLinkedCohorts();
          vm.getLinkedDataSets();
          vm.getAssociatedDocumentation();
        },
        error => vm.log.error('Error loading', error, 'Error')
      );
  }

  save(close: boolean) {
    const vm = this;

    // Populate Data Flows before save
    vm.dpa.dataFlows = {};
    for (const idx in this.dataFlows) {
      const dataflow: DataFlow = this.dataFlows[idx];
      this.dpa.dataFlows[dataflow.uuid] = dataflow.name;
    }

    // Populate Cohorts before save
    vm.dpa.cohorts = {};
    for (const idx in this.cohorts) {
      const cohort: Cohort = this.cohorts[idx];
      this.dpa.cohorts[cohort.uuid] = cohort.name;
    }

    // Populate DataSets before save
    vm.dpa.dataSets = {};
    for (const idx in this.dataSets) {
      const dataset: DataSet = this.dataSets[idx];
      this.dpa.dataSets[dataset.uuid] = dataset.name;
    }

    // Populate DataSets before save
    vm.dpa.documentations = [];
    vm.dpa.documentations = vm.documentations;

    vm.dpaService.saveDpa(vm.dpa)
      .subscribe(saved => {
          vm.log.success('Data Processing Agreement saved', vm.dpa, 'Saved');
          if (close) { vm.close(); }
        },
        error => vm.log.error('Error saving Data Processing Agreement', error, 'Error')
      );
  }

  close() {
    window.history.back();
  }

  private editDataFlows() {
    const vm = this;
    DataflowPickerComponent.open(vm.$modal, vm.dataFlows)
      .result.then(function
      (result: DataFlow[]) { vm.dataFlows = result; },
      () => vm.log.info('Edit Data Flows cancelled')
    );
  }

  private editCohorts() {
    const vm = this;
    CohortPickerComponent.open(vm.$modal, vm.cohorts)
      .result.then(function
      (result: Cohort[]) { vm.cohorts = result; },
      () => vm.log.info('Edit Cohorts cancelled')
    );
  }

  private editDataSets() {
    const vm = this;
    DataSetPickerComponent.open(vm.$modal, vm.dataSets)
      .result.then(function
      (result: DataSet[]) { vm.dataSets = result; },
      () => vm.log.info('Edit Data Sets cancelled')
    );
  }

  private editDataFlow(item: DataFlow) {
    this.router.navigate(['/dataFlow', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  private editCohort(item: Cohort) {
    this.router.navigate(['/cohort', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  private editDataSet(item: DataSet) {
    this.router.navigate(['/dataSet', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  private getLinkedCohorts() {
    const vm = this;
    vm.dpaService.getLinkedCohorts(vm.dpa.uuid)
      .subscribe(
        result => vm.cohorts = result,
        error => vm.log.error('Failed to load linked Cohorts', error, 'Load Linked Cohorts')
      );
  }

  private getLinkedDataFlows() {
    const vm = this;
    vm.dpaService.getLinkedDataFlows(vm.dpa.uuid)
      .subscribe(
        result => vm.dataFlows = result,
        error => vm.log.error('Failed to load linked Data Flows', error, 'Load Linked Data Flows')
      );
  }

  private getLinkedDataSets() {
    const vm = this;
    vm.dpaService.getLinkedDataSets(vm.dpa.uuid)
      .subscribe(
        result => vm.dataSets = result,
        error => vm.log.error('Failed to load linked Data Sets', error, 'Load Linked Data Sets')
      );
  }

  private getAssociatedDocumentation() {
    const vm = this;
    vm.documentationService.getAllAssociatedDocuments(vm.dpa.uuid, '5')
      .subscribe(
        result => vm.documentations = result,
        error => vm.log.error('Failed to load associated documentation', error, 'Load associated documentation')
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

  private uploadFile() {
    const vm = this;
    const myReader: FileReader = new FileReader();

    myReader.onloadend = function(e){
      // you can perform an action with readed data here
      vm.log.success('Uploading File', null, 'Upload');
      vm.pdfSrc = myReader.result;
      const newDoc: Documentation = new Documentation();
      newDoc.fileData = myReader.result;
      newDoc.title = vm.file.name;
      newDoc.filename = vm.file.name;
      vm.documentations.push(newDoc);
    }


    myReader.readAsDataURL(vm.file);
  }

  ok() {
    this.uploadFile();
  }

  cancel() {
    this.file = null;
  }

  delete($event) {
    console.log($event);
  }

}