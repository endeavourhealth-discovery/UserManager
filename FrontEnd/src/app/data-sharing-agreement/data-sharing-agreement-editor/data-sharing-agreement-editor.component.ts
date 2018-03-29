import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {DataSharingAgreementService} from '../data-sharing-agreement.service';
import {LoggerService, SecurityService} from 'eds-angular4';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal, NgbRadioGroup} from '@ng-bootstrap/ng-bootstrap';
import {Purpose} from '../models/Purpose';
import {Organisation} from '../../organisation/models/Organisation';
import {Region} from '../../region/models/Region';
import {DataFlow} from '../../data-flow/models/DataFlow';
import {Dsa} from '../models/Dsa';
import {DataflowPickerComponent} from '../../data-flow/dataflow-picker/dataflow-picker.component';
import {RegionPickerComponent} from '../../region/region-picker/region-picker.component';
import {OrganisationPickerComponent} from '../../organisation/organisation-picker/organisation-picker.component';
import {PurposeAddComponent} from '../purpose-add/purpose-add.component';
import {ToastsManager} from 'ng2-toastr';
import {Marker} from '../../region/models/Marker';
import {Documentation} from "../../documentation/models/Documentation";
import {DocumentationService} from "../../documentation/documentation.service";

@Component({
  selector: 'app-data-sharing-agreement-editor',
  templateUrl: './data-sharing-agreement-editor.component.html',
  styleUrls: ['./data-sharing-agreement-editor.component.css']
})
export class DataSharingAgreementEditorComponent implements OnInit {
  private paramSubscriber: any;

  dsa: Dsa = <Dsa>{};
  dataFlows: DataFlow[];
  regions: Region[];
  publishers: Organisation[];
  subscribers: Organisation[];
  documentations: Documentation[];
  purposes: Purpose[];
  benefits: Purpose[];
  publisherMarkers: Marker[];
  subscriberMarkers: Marker[];
  mapMarkers: Marker[];
  showPub = true;
  allowEdit = false;
  file: File;
  pdfSrc: any;

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
  documentDetailsToShow = new Documentation().getDisplayItems();

  constructor(private $modal: NgbModal,
              private log: LoggerService,
              private dsaService: DataSharingAgreementService,
              private securityService: SecurityService,
              private documentationService: DocumentationService,
              private router: Router,
              private route: ActivatedRoute,
              public toastr: ToastsManager, vcr: ViewContainerRef) {
    this.toastr.setRootViewContainerRef(vcr);
  }

  ngOnInit() {
    this.checkEditPermission();
    this.paramSubscriber = this.route.params.subscribe(
    params => {
      this.performAction(params['mode'], params['id']);
    });
  }

  checkEditPermission() {
    const vm = this;
    if (vm.securityService.hasPermission('eds-dsa-manager', 'eds-dsa-manager:admin'))
      vm.allowEdit = true;
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
    console.log(uuid);
    vm.dsaService.getDsa(uuid)
      .subscribe(result =>  {
          vm.dsa = result;
          vm.getLinkedDataFlows();
          vm.getLinkedRegions();
          vm.getPublishers();
          vm.getSubscribers();
          vm.getPurposes();
          vm.getBenefits();
          vm.getPublisherMarkers();
          vm.getSubscriberMarkers();
          vm.getAssociatedDocumentation();
        },
        error => vm.log.error('Error loading', error, 'Error')
      );
  }

  save(close: boolean) {
    const vm = this;
    console.log('before');
    console.log(vm.dsa);
    // Populate data flows before save
    vm.dsa.dataFlows = {};
    for (const idx in this.dataFlows) {
      const dataflow: DataFlow = this.dataFlows[idx];
      this.dsa.dataFlows[dataflow.uuid] = dataflow.name;
    }

    // Populate regions before save
    vm.dsa.regions = {};
    for (const idx in this.regions) {
      const region: Region = this.regions[idx];
      this.dsa.regions[region.uuid] = region.name;
    }

    // Populate publishers before save
    vm.dsa.publishers = {};
    for (const idx in this.publishers) {
      const pub: Organisation = this.publishers[idx];
      this.dsa.publishers[pub.uuid] = pub.name;
    }

    // Populate subscribers before save
    vm.dsa.subscribers = {};
    for (const idx in this.subscribers) {
      const sub: Organisation = this.subscribers[idx];
      this.dsa.subscribers[sub.uuid] = sub.name;
    }

    // Populate purposes before save
    vm.dsa.purposes = [];
    vm.dsa.purposes = this.purposes;

    // Populate benefits before save
    vm.dsa.benefits = [];
    vm.dsa.benefits = this.benefits;

    // Populate documents before save
    vm.dsa.documentations = [];
    vm.dsa.documentations = vm.documentations;

    console.log(vm.dsa);
    vm.dsaService.saveDsa(vm.dsa)
      .subscribe(saved => {
          vm.dsa.uuid = saved;
          vm.log.success('Data Sharing Agreement saved', vm.dsa, 'Saved');

          console.log('after');
          console.log(vm.dsa);
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
    PurposeAddComponent.open(vm.$modal, vm.purposes, 'Purpose')
      .result.then(function
      (result: Purpose[]) { vm.purposes = result; },
      () => vm.log.info('Edit Purposes cancelled')
    );
  }

  private editBenefits() {
    const vm = this;
    PurposeAddComponent.open(vm.$modal, vm.benefits, 'Benefit')
      .result.then(function
      (result: Purpose[]) { vm.benefits = result; },
      () => vm.log.info('Edit Benefits cancelled')
    );
  }

  private editDataFlow(item: DataFlow) {
    this.router.navigate(['/dataFlow', item.uuid, 'edit']);
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

  private getAssociatedDocumentation() {
    const vm = this;
    vm.documentationService.getAllAssociatedDocuments(vm.dsa.uuid, '3')
      .subscribe(
        result => vm.documentations = result,
        error => vm.log.error('Failed to load associated documentation', error, 'Load associated documentation')
      );
  }

  private getSubscriberMarkers() {
    const vm = this;
    vm.dsaService.getSubscriberMarkers(vm.dsa.uuid)
      .subscribe(
        result => {
          vm.subscriberMarkers = result;
        },
        error => vm.log.error('Failed to load subscriber markers', error, 'Load subscriber Markers')
      )
  }

  private getPublisherMarkers() {
    const vm = this;
    vm.dsaService.getPublisherMarkers(vm.dsa.uuid)
      .subscribe(
        result => {
          vm.mapMarkers = result;
          vm.publisherMarkers = result;
          console.log(vm.publisherMarkers);
        },
        error => vm.log.error('Failed to load publisher markers', error, 'Load publisher Markers')
      )
  }

  delete($event) {
    console.log($event);
  }

  swapMarkers() {
    const vm = this;
    console.log(vm.showPub);
    if (vm.showPub) {
      console.log('showing pubs');
      vm.mapMarkers = vm.publisherMarkers;
    } else {
      console.log('showing subs');
      vm.mapMarkers = vm.subscriberMarkers;
    }
  }

  private uploadFile() {
    const vm = this;
    const myReader: FileReader = new FileReader();

    myReader.onloadend = function(e){
      // you can perform an action with readed data here
      vm.log.success('Uploading file', null, 'Upload');
      vm.pdfSrc = myReader.result;
      const newDoc: Documentation = new Documentation();
      newDoc.fileData = myReader.result;
      newDoc.title = vm.file.name;
      newDoc.filename = vm.file.name;
      vm.documentations.push(newDoc);
    }


    myReader.readAsDataURL(vm.file);
  }

  fileChange(event) {
    const fileList: FileList = event.target.files;
    if (fileList.length > 0) {
      this.file = fileList[0];
    } else {
      this.file = null;
    }
  }

  ok() {
    this.uploadFile();
  }

  cancel() {
    this.file = null;
  }

}
