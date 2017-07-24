import { Component, OnInit } from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Organisation} from '../../organisation/models/Organisation';
import {LoggerService, MessageBoxDialog} from 'eds-angular4';
import {RegionService} from '../region.service';
import {Region} from '../models/Region';
import {Router} from '@angular/router';

@Component({
  selector: 'app-region',
  templateUrl: './region.component.html',
  styleUrls: ['./region.component.css']
})
export class RegionComponent implements OnInit {
  organisations: Organisation[];
  regions: Region[] = [];

  regionDetailsToShow = new Region().getDisplayItems();

  constructor(private $modal: NgbModal,
              private regionService: RegionService,
              private log: LoggerService,
              private router: Router) {  }

  ngOnInit() {
    this.getRegions();
  }

  getRegions() {
    const vm = this;
    vm.regionService.getAllRegions()
      .subscribe(
        result => {
          vm.regions = result;
        },
        error => vm.log.error('Failed to load organisations', error, 'Load organisations')
      );
  }

  add() {
    this.router.navigate(['region', {itemUuid: null, itemAction: 'add'}]);
  }

  edit(item: Region) {
    this.router.navigate(['region', {itemUuid: item.uuid, itemAction: 'edit'}]);
  }

  delete(item: Region) {
    const vm = this;
    MessageBoxDialog.open(vm.$modal, 'Delete Region', 'Are you sure you want to delete the Region?', 'Yes', 'No')
      .result.then(
      () => vm.doDelete(item),
      () => vm.log.info('Delete cancelled')
    );
  }

  doDelete(item: Region) {
    const vm = this;
    vm.regionService.deleteRegion(item.uuid)
      .subscribe(
        () => {
          const index = vm.regions.indexOf(item);
          vm.regions.splice(index, 1);
          vm.log.success('Region deleted', item, 'Delete Region');
        },
        (error) => vm.log.error('Failed to delete Region', error, 'Delete Region')
      );
  }

  close() {
    this.router.navigate(['organisationOverview']);
  }

}
