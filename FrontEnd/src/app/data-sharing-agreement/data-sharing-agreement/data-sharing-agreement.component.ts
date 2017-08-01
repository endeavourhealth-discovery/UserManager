import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {Dsa} from '../models/Dsa';
import {LoggerService, MessageBoxDialog} from 'eds-angular4';
import {Router} from '@angular/router';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DataSharingAgreementService} from '../data-sharing-agreement.service';
import {ToastsManager} from 'ng2-toastr';

@Component({
  selector: 'app-data-sharing-agreement',
  templateUrl: './data-sharing-agreement.component.html',
  styleUrls: ['./data-sharing-agreement.component.css']
})
export class DataSharingAgreementComponent implements OnInit {
  dsas: Dsa[] = [];

  dsaDetailsToShow = new Dsa().getDisplayItems();

  constructor(private $modal: NgbModal,
              private dsaService: DataSharingAgreementService,
              private log: LoggerService,
              private router: Router,
              public toastr: ToastsManager, vcr: ViewContainerRef) {
    this.toastr.setRootViewContainerRef(vcr);
  }

  ngOnInit() {
    this.getDsas();
  }

  getDsas() {
    const vm = this;
    vm.dsaService.getAllDsas()
      .subscribe(
        result => vm.dsas = result,
        error => vm.log.error('Failed to load dsas', error, 'Load dsa')
      );
  }

  add() {
    this.router.navigate(['/dsa', 1, 'add']);
  }

  edit(item: Dsa) {
    this.router.navigate(['/dsa', item.uuid, 'edit']);
  }

  delete(item: Dsa) {
    const vm = this;
    MessageBoxDialog.open(vm.$modal, 'Delete Data Sharing Agreement', 'Are you sure you want to delete the Data Sharing Agreement?', 'Yes', 'No')
      .result.then(
      () => vm.doDelete(item),
      () => vm.log.info('Delete cancelled')
    );
  }

  doDelete(item: Dsa) {
    const vm = this;
    vm.dsaService.deleteDsa(item.uuid)
      .subscribe(
        () => {
          const index = vm.dsas.indexOf(item);
          vm.dsas.splice(index, 1);
          vm.log.success('Data Sharing Agreement deleted', item, 'Delete Data Sharing Agreement');
        },
        (error) => vm.log.error('Failed to delete Data Sharing Agreement', error, 'Delete Data Sharing Agreement')
      );
  }

  close() {
    this.router.navigate(['/dataSharingOverview']);
  }

}
