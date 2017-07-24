import { Component, OnInit } from '@angular/core';
import {DataSharingAgreementService} from '../data-sharing-agreement.service';
import {LoggerService} from 'eds-angular4';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Purpose} from '../models/Purpose';
import {Organisation} from '../../organisation/models/Organisation';
import {Region} from '../../region/models/Region';
import {DataFlow} from '../../data-flow/models/DataFlow';
import {Dsa} from '../models/Dsa';
import {DataflowPickerComponent} from '../../data-flow/dataflow-picker/dataflow-picker.component';
import {RegionPickerComponent} from '../../region/region-picker/region-picker.component';
import {OrganisationPickerComponent} from '../../organisation/organisation-picker/organisation-picker.component';

@Component({
  selector: 'app-data-sharing-agreement-editor',
  templateUrl: './data-sharing-agreement-editor.component.html',
  styleUrls: ['./data-sharing-agreement-editor.component.css']
})
export class DataSharingAgreementEditorComponent implements OnInit {
  private paramSubscriber: any;
  public accordionClass = 'accordionClass';

  dsa: Dsa = <Dsa>{};
  dataFlows: DataFlow[];
  regions: Region[];
  publishers: Organisation[];
  subscribers: Organisation[];
  purposes: Purpose[];
  benefits: Purpose[];

  status = [
    {num: 0, name : 'Active'},
    {num: 1, name : 'Inactive'}
  ];

  consents = [
    {num: 0, name : 'Explicit Consent'},
    {num: 1, name : 'Implied Consent'}
  ];

  dataflowDetailsToShow = new DataFlow().getDisplayItems();
  regionDetailsToShow = new Region().getDisplayItems();
  orgDetailsToShow = new Organisation().getDisplayItems();
  purposeDetailsToShow = new Purpose().getDisplayItems();

  constructor(private $modal: NgbModal,
              private log: LoggerService,
              private dsaService: DataSharingAgreementService,
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
    this.dsa = {
      name : ''
    } as Dsa;
  }

  load(uuid: string) {
    const vm = this;
    vm.dsaService.getDsa(uuid)
      .subscribe(result =>  {
          vm.dsa = result;
          vm.getLinkedDataFlows();
          vm.getLinkedRegions();
          vm.getPublishers();
          vm.getSubscribers();
          vm.getPurposes();
          vm.getBenefits();
        },
        error => vm.log.error('Error loading', error, 'Error')
      );
  }

  save(close: boolean) {
    const vm = this;
    // Populate data flows before save
    vm.dsa.dataFlows = {};
    for (let idx in this.dataFlows) {
      let dataflow: DataFlow = this.dataFlows[idx];
      this.dsa.dataFlows[dataflow.uuid] = dataflow.name;
    }

    // Populate regions before save
    vm.dsa.regions = {};
    for (let idx in this.regions) {
      let region: Region = this.regions[idx];
      this.dsa.regions[region.uuid] = region.name;
    }

    // Populate publishers before save
    vm.dsa.publishers = {};
    for (let idx in this.publishers) {
      let pub: Organisation = this.publishers[idx];
      this.dsa.publishers[pub.uuid] = pub.name;
    }

    // Populate subscribers before save
    vm.dsa.subscribers = {};
    for (let idx in this.subscribers) {
      let sub: Organisation = this.subscribers[idx];
      this.dsa.subscribers[sub.uuid] = sub.name;
    }

    // Populate purposes before save
    vm.dsa.purposes = [];
    vm.dsa.purposes = this.purposes;

    // Populate benefits before save
    vm.dsa.benefits = [];
    vm.dsa.benefits = this.benefits;

    vm.dsaService.saveDsa(vm.dsa)
      .subscribe(saved => {
          vm.log.success('Data Sharing Agreement saved', vm.dsa, 'Saved');
          if (close) { vm.close(); }
        },
        error => vm.log.error('Error saving Data Sharing Agreement', error, 'Error')
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

  private editRegion(item: DataFlow) {
    this.router.navigate(['/region', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  private editRegions() {
    const vm = this;
    RegionPickerComponent.open(vm.$modal, vm.regions)
      .result.then(function
      (result: Region[]) { vm.regions = result; },
      () => vm.log.info('Edit Regions cancelled')
    );
  }

  private editPublishers() {
    const vm = this;
    OrganisationPickerComponent.open(vm.$modal, vm.publishers, 'organisation')
      .result.then(function
      (result: Organisation[]) { vm.publishers = result; },
      () => vm.log.info('Edit Publishers cancelled')
    );
  }

  private editSubscribers() {
    const vm = this;
    OrganisationPickerComponent.open(vm.$modal, vm.subscribers, 'organisation')
      .result.then(function
      (result: Organisation[]) { vm.subscribers = result; },
      () => vm.log.info('Edit Subscribers cancelled')
    );
  }

  private editPurposes() {
    const vm = this;
    PurposeAddDialog.open(vm.$modal, vm.purposes, 'Purpose')
      .result.then(function
      (result: Purpose[]) { vm.purposes = result; },
      () => vm.log.info('Edit Purposes cancelled')
    );
  }

  private editBenefits() {
    const vm = this;
    PurposeAddDialog.open(vm.$modal, vm.benefits, 'Benefit')
      .result.then(function
      (result: Purpose[]) { vm.benefits = result; },
      () => vm.log.info('Edit Benefits cancelled')
    );
  }

  private editDataFlow(item: DataFlow) {
    this.router.navigate(['/dataFlow', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  private getLinkedDataFlows() {
    const vm = this;
    vm.dsaService.getLinkedDataFlows(vm.dsa.uuid)
      .subscribe(
        result => vm.dataFlows = result,
        error => vm.log.error('Failed to load linked Data Flows', error, 'Load Linked Data Flows')
      );
  }

  private getLinkedRegions() {
    const vm = this;
    vm.dsaService.getLinkedRegions(vm.dsa.uuid)
      .subscribe(
        result => vm.regions = result,
        error => vm.log.error('Failed to load linked Regions', error, 'Load Linked Regions')
      );
  }

  private getPublishers() {
    const vm = this;
    vm.dsaService.getPublishers(vm.dsa.uuid)
      .subscribe(
        result => vm.publishers = result,
        error => vm.log.error('Failed to load publishers', error, 'Load Publishers')
      );
  }

  private getSubscribers() {
    const vm = this;
    vm.dsaService.getSubscribers(vm.dsa.uuid)
      .subscribe(
        result => vm.subscribers = result,
        error => vm.log.error('Failed to load subscribers', error, 'Load Subscribers')
      );
  }

  private getPurposes() {
    const vm = this;
    vm.dsaService.getPurposes(vm.dsa.uuid)
      .subscribe(
        result => vm.purposes = result,
        error => vm.log.error('Failed to load purposes', error, 'Load Purposes')
      );
  }

  private getBenefits() {
    const vm = this;
    vm.dsaService.getBenefits(vm.dsa.uuid)
      .subscribe(
        result => vm.benefits = result,
        error => vm.log.error('Failed to load benefits', error, 'Load Benefits')
      );
  }

}
