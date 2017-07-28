import { Component, OnInit } from '@angular/core';
import {CohortService} from '../cohort.service';
import {LoggerService} from 'eds-angular4';
import {ActivatedRoute, Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Dpa} from '../../data-processing-agreement/models/Dpa';
import {DataProcessingAgreementPickerComponent} from '../../data-processing-agreement/data-processing-agreement-picker/data-processing-agreement-picker.component';
import {Cohort} from "../models/Cohort";

@Component({
  selector: 'app-cohort-editor',
  templateUrl: './cohort-editor.component.html',
  styleUrls: ['./cohort-editor.component.css']
})
export class CohortEditorComponent implements OnInit {
  private paramSubscriber: any;
  cohort: Cohort = <Cohort>{};
  dpas: Dpa[];

  dpaDetailsToShow = new Dpa().getDisplayItems();

  constructor(private $modal: NgbModal,
              private log: LoggerService,
              private cohortService: CohortService,
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
    this.cohort = {
      name : ''
    } as Cohort;
  }

  load(uuid: string) {
    const vm = this;
    vm.cohortService.getCohort(uuid)
      .subscribe(result =>  {
          vm.cohort = result;
          vm.getLinkedDpas();
        },
        error => vm.log.error('Error loading', error, 'Error')
      );
  }

  save(close: boolean) {
    const vm = this;

    // Populate Data Processing Agreements before save
    vm.cohort.dpas = {};
    for (const idx in this.dpas) {
      let dpa: Dpa = this.dpas[idx];
      this.cohort.dpas[dpa.uuid] = dpa.name;
    }

    vm.cohortService.saveCohort(vm.cohort)
      .subscribe(saved => {
          vm.log.success('Cohort saved', vm.cohort, 'Saved');
          if (close) { vm.close(); }
        },
        error => vm.log.error('Error saving Cohort', error, 'Error')
      );
  }

  close() {
    window.history.back();
  }

  private editDataProcessingAgreements() {
    const vm = this;
    DataProcessingAgreementPickerComponent.open(vm.$modal, vm.dpas)
      .result.then(function
      (result: Dpa[]) { vm.dpas = result; },
      () => vm.log.info('Edit Data Processing Agreements cancelled')
    );
  }

  private editDataProcessingAgreement(item: Dpa) {
    this.router.navigate(['/dpa', item.uuid, 'edit']);
  }

  private getLinkedDpas() {
    const vm = this;
    vm.cohortService.getLinkedDpas(vm.cohort.uuid)
      .subscribe(
        result => vm.dpas = result,
        error => vm.log.error('Failed to load linked Data Processing Agreement', error, 'Load Linked Data Processing Agreement')
      );
  }

}
