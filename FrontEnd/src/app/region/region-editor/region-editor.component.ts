import { Component, OnInit } from '@angular/core';
import {Region} from '../models/Region';
import {Organisation} from '../../organisation/models/Organisation';
import {Marker} from '../models/Marker';
import {RegionService} from '../region.service';
import {LoggerService} from 'eds-angular4';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {OrganisationService} from '../../organisation/organisation.service';
import {ActivatedRoute, Router} from '@angular/router';
import {OrganisationPickerComponent} from "../../organisation/organisation-picker/organisation-picker.component";
import {RegionPickerComponent} from "../region-picker/region-picker.component";
import {Dsa} from "../../data-sharing-agreement/models/Dsa";
import {DataSharingAgreementPickerComponent} from "../../data-sharing-agreement/data-sharing-agreement-picker/data-sharing-agreement-picker.component";

@Component({
  selector: 'app-region-editor',
  templateUrl: './region-editor.component.html',
  styleUrls: ['./region-editor.component.css']
})
export class RegionEditorComponent implements OnInit {
  public accordionClass = 'accordionClass';
  private paramSubscriber: any;

  region: Region = <Region>{};
  organisations: Organisation[];
  parentRegions: Region[];
  childRegions: Region[];
  sharingAgreements: Dsa[];
  lat = 54.4347266;
  lng: number = -4.7194005;
  zoom = 6.01;
  markers: Marker[];
  editDisabled = false;

  orgDetailsToShow = new Organisation().getDisplayItems();
  regionDetailsToShow = new Region().getDisplayItems();
  sharingAgreementsDetailsToShow = new Dsa().getDisplayItems();

  constructor(private $modal: NgbModal,
              private log: LoggerService,
              private organisationManagerService: OrganisationService,
              private regionService: RegionService,
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
    this.region = {
      name : ''
    } as Region;
  }

  load(uuid: string) {
    const vm = this;
    vm.regionService.getRegion(uuid)
      .subscribe(result =>  {
          vm.region = result;
          vm.getRegionOrganisations();
          vm.getParentRegions();
          vm.getChildRegions();
          vm.getOrganisationMarkers();
          vm.getSharingAgreements();
        },
        error => vm.log.error('Error loading', error, 'Error')
      );
  }

  save(close: boolean) {
    const vm = this;

    // Populate organisations before save
    vm.region.organisations = {};
    for (const idx in this.organisations) {
      let organisation: Organisation = this.organisations[idx];
      this.region.organisations[organisation.uuid] = organisation.name;
    }

    // populate Parent Regions
    vm.region.parentRegions = {};
    for (const idx in this.parentRegions) {
      let region: Region = this.parentRegions[idx];
      this.region.parentRegions[region.uuid] = region.name;
    }

    // populate Parent Regions
    vm.region.childRegions = {};
    for (const idx in this.childRegions) {
      let region: Region = this.childRegions[idx];
      this.region.childRegions[region.uuid] = region.name;
    }

    //populate sharing agreements
    vm.region.sharingAgreements = {};
    for (const idx in this.sharingAgreements) {
      let dsa: Dsa = this.sharingAgreements[idx];
      this.region.sharingAgreements[dsa.uuid] = dsa.name;
    }

    vm.regionService.saveRegion(vm.region)
      .subscribe(saved => {
          vm.log.success('Item saved', vm.region, 'Saved');
          if (close) { this.router.navigate(['/organisationManagerOverview']); }
        },
        error => vm.log.error('Error saving', error, 'Error')
      );
  }

  close() {
    this.router.navigate(['/organisationManagerOverview']);
  }

  private getRegionOrganisations() {
    const vm = this;
    vm.regionService.getRegionOrganisations(vm.region.uuid)
      .subscribe(
        result => vm.organisations = result,
        error => vm.log.error('Failed to load region organisations', error, 'Load region organisation')
      );
  }

  private getParentRegions() {
    const vm = this;
    vm.regionService.getParentRegions(vm.region.uuid)
      .subscribe(
        result => vm.parentRegions = result,
        error => vm.log.error('Failed to load parent regions', error, 'Load parent regions')
      );
  }

  private getChildRegions() {
    const vm = this;
    vm.regionService.getChildRegions(vm.region.uuid)
      .subscribe(
        result => vm.childRegions = result,
        error => vm.log.error('Failed to load child regions', error, 'Load child regions')
      );
  }

  private getSharingAgreements() {
    const vm = this;
    vm.regionService.getSharingAgreements(vm.region.uuid)
      .subscribe(
        result => {
          vm.sharingAgreements = result;
        },
        error => vm.log.error('Failed to load sharing agreements', error, 'Load sharing agreements')

      );
  }

  private getOrganisationMarkers() {
    const vm = this;
    vm.organisationManagerService.getOrganisationMarkers(vm.region.uuid)
      .subscribe(
        result => vm.markers = result,
        error => vm.log.error('Failed to load organisation markers', error, 'Load organisation Markers')
      );
  }

  private editOrganisations() {
    const vm = this;
    OrganisationPickerComponent.open(vm.$modal, vm.organisations, 'organisations')
      .result.then(function (result: Organisation[]) {
      vm.organisations = result;
    });
  }

  private editParentRegions() {
    const vm = this;
    RegionPickerComponent.open(vm.$modal, vm.parentRegions)
      .result.then(function (result: Region[]) {
      vm.parentRegions = result;
    });
  }

  private editChildRegions() {
    const vm = this;
    RegionPickerComponent.open(vm.$modal, vm.childRegions)
      .result.then(function (result: Region[]) {
      vm.childRegions = result;
    });
  }

  private editSharingAgreements() {
    const vm = this;
    DataSharingAgreementPickerComponent.open(vm.$modal, vm.sharingAgreements)
      .result.then(function (result: Dsa[]) {
      vm.sharingAgreements = result;
    });
  }

  editOrganisation(item: Organisation) {
    this.router.navigate(['/organisation', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  editRegion(item: Organisation) {
    this.router.navigate(['/region', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  editSharingAgreement(item: Dsa) {
    this.router.navigate(['/dsa', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

}
