import { Component, OnInit } from '@angular/core';
import {Organisation} from '../models/Organisation';
import {Address} from '../models/Address';
import {LoggerService} from 'eds-angular4';
import {RegionPickerDialog} from '../region/regionPicker.dialog';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {OrganisationManagerService} from '../organisation.service';
import {Region} from '../region/models/Region';
import {OrganisationPickerDialog} from '../organisations/organisationPicker.dialog';
import {OrganisationManagerPickerDialog} from './organisationManagerPicker.dialog';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-organisation-editor',
  templateUrl: './organisation-editor.component.html',
  styleUrls: ['./organisation-editor.component.css']
})
export class OrganisationEditorComponent implements OnInit {
  public accordionClass = 'accordionClass';
  private paramSubscriber: any;

  region: Region = <Region>{};
  organisation: Organisation = <Organisation>{};
  regions: Region[];
  childOrganisations: Organisation[];
  parentOrganisations: Organisation[];
  services: Organisation[];
  addresses: Address[];
  location: any;
  orgType = 'Organisation';

  orgDetailsToShow = new Organisation().getDisplayItems();
  regionDetailsToShow = new Region().getDisplayItems();

  constructor(private $modal: NgbModal,
              private log: LoggerService,
              private organisationManagerService: OrganisationManagerService,
              private router: Router,
              private route: ActivatedRoute) {
  }

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
      case 'addService':
        this.createService(itemUuid);
        break;
      case 'edit':
        this.load(itemUuid);
        break;
    }
  }

  createService(uuid: string) {
    const vm = this;
    vm.orgType = 'Service';
    this.organisation = {
      name: '',
      isService: 1,
      bulkImported : 0,
      bulkItemUpdated : 0
    } as Organisation;
  }

  createServiceFromOrg() {
    const parent: Organisation = (JSON.parse(JSON.stringify(this.organisation)));
    this.services = null;
    this.childOrganisations = null;
    this.regions = null;
    this.parentOrganisations = [];
    this.parentOrganisations.push(parent);
    this.organisation.uuid = null;
    this.organisation.isService = 1;
    this.orgType = 'Service';
  }

  create(uuid: string) {
    this.organisation = {
      name: '',
      isService: 0,
      bulkImported : 0,
      bulkItemUpdated : 0
    } as Organisation;

  }

  load(uuid: string) {
    const vm = this;
    vm.organisationManagerService.getOrganisation(uuid)
      .subscribe(result =>  {
          vm.organisation = result;
          if (vm.organisation.isService) {
            vm.orgType = 'Service';
          } else { // only get these for organisations, not services
            vm.getOrganisationRegions();
            vm.getOrganisationAddresses();
            vm.getChildOrganisations();
            vm.getServices();
          }
          vm.getParentOrganisations();
        },
        error => vm.log.error('Error loading', error, 'Error')
      );
  }

  save(close: boolean) {
    const vm = this;
    // Populate organisations regions before save
    vm.organisation.regions = {};
    for (const idx of this.regions) {
      const region: Region = this.regions[idx];
      this.organisation.regions[region.uuid] = region.name;
    }

    vm.organisation.childOrganisations = {};
    for (const idx of this.childOrganisations) {
      const org: Organisation = this.childOrganisations[idx];
      this.organisation.childOrganisations[org.uuid] = org.name;
    }

    vm.organisation.parentOrganisations = {};
    for (const idx of this.parentOrganisations) {
      const org: Organisation = this.parentOrganisations[idx];
      this.organisation.parentOrganisations[org.uuid] = org.name;
    }

    vm.organisation.services = {};
    for (const idx of this.services) {
      const org: Organisation = this.services[idx];
      this.organisation.services[org.uuid] = org.name;
    }

    // Populate Addresses before save
    vm.organisation.addresses = this.addresses;


    vm.organisationManagerService.saveOrganisation(vm.organisation)
      .subscribe(saved => {
          vm.log.success('Item saved', vm.organisation, 'Saved');
          if (close) { this.router.navigate(['/organisationOverview']); }
        },
        error => vm.log.error('Error saving', error, 'Error')
      );
  }

  close() {
    this.router.navigate(['/organisationOverview']);
  }

  addAddress() {
    const vm = this;

    console.log(vm.addresses);
    const address: Address = <Address>{};
    address.organisationUuid = vm.organisation.uuid;
    address.buildingName = '';
    address.numberAndStreet = '';
    address.locality = '';
    address.city = '';
    address.county = '';
    address.postcode = '';
    vm.addresses.push(address);
    console.log(vm.addresses);
  }

  private editRegions() {
    const vm = this;
    RegionPickerDialog.open(vm.$modal, vm.regions)
      .result.then(function (result: Region[]) {
      vm.regions = result;
    });
  }

  private editChildOrganisations() {
    const vm = this;
    OrganisationManagerPickerDialog.open(vm.$modal, vm.childOrganisations, 'organisation' )
      .result.then(function (result: Organisation[]) {
      vm.childOrganisations = result;
    });
  }

  private editParentOrganisations() {
    const vm = this;
    OrganisationManagerPickerDialog.open(vm.$modal, vm.parentOrganisations, 'organisation' )
      .result.then(function (result: Organisation[]) {
      vm.parentOrganisations = result;
    });
  }

  private editServices() {
    const vm = this;
    OrganisationManagerPickerDialog.open(vm.$modal, vm.services, 'services' )
      .result.then(function (result: Organisation[]) {
      vm.services = result;
    });
  }

  private getOrganisationRegions() {
    const vm = this;
    vm.organisationManagerService.getOrganisationRegions(vm.organisation.uuid)
      .subscribe(
        result => vm.regions = result,
        error => vm.log.error('Failed to load organisation regions', error, 'Load organisation regions')
      );
  }

  private getOrganisationAddresses() {
    const vm = this;
    vm.organisationManagerService.getOrganisationAddresses(vm.organisation.uuid)
      .subscribe(
        result => vm.addresses = result,
        error => vm.log.error('Failed to load organisation Addresses', error, 'Load organisation Addresses')
      );
  }

  private getChildOrganisations() {
    const vm = this;
    vm.organisationManagerService.getChildOrganisations(vm.organisation.uuid)
      .subscribe(
        result => vm.childOrganisations = result,
        error => vm.log.error('Failed to load child organisations', error, 'Load child organisation')
      );
  }

  private getParentOrganisations() {
    const vm = this;
    vm.organisationManagerService.getParentOrganisations(vm.organisation.uuid, vm.organisation.isService)
      .subscribe(
        result => vm.parentOrganisations = result,
        error => vm.log.error('Failed to load parent organisations', error, 'Load parent organisation')
      );
  }

  private getServices() {
    const vm = this;
    vm.organisationManagerService.getServices(vm.organisation.uuid)
      .subscribe(
        result => vm.services = result,
        error => vm.log.error('Failed to load services', error, 'Load services')
      );
  }

  editOrganisation(item: Organisation) {
    this.router.navigate(['/organisation', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  editRegion(item: Organisation) {
    this.router.navigate(['/region', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }
}
