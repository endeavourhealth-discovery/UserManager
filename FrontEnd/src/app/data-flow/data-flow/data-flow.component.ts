import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {DataFlowService} from '../data-flow.service';
import {DataFlow} from '../models/DataFlow';
import {LoggerService, MessageBoxDialog} from 'eds-angular4';
import {Router} from '@angular/router';
import {ToastsManager} from 'ng2-toastr';

@Component({
  selector: 'app-data-flow',
  templateUrl: './data-flow.component.html',
  styleUrls: ['./data-flow.component.css']
})
export class DataFlowComponent implements OnInit {
  private paramSubscriber: any;
  dataflows: DataFlow[] = [];

  dataflowDetailsToShow = new DataFlow().getDisplayItems();

  constructor(private $modal: NgbModal,
              private dataFlowService: DataFlowService,
              private log: LoggerService,
              private router: Router,
              public toastr: ToastsManager, vcr: ViewContainerRef) {
    this.toastr.setRootViewContainerRef(vcr);
  }

  ngOnInit() {
    this.getDataFlows();
  }

  getDataFlows() {

    const vm = this;
    vm.dataFlowService.getAllDataFlows()
      .subscribe(
        result => vm.dataflows = result,
        error => vm.log.error('Failed to load data flows', error, 'Load data flows')
      );
  }

  add() {
    this.router.navigate(['/dataFlow', 1, 'add']);
  }

  edit(item: DataFlow) {
    this.router.navigate(['/dataFlow', item.uuid, 'edit']);
  }

  delete(item: DataFlow) {
    const vm = this;
    MessageBoxDialog.open(vm.$modal, 'Delete Data Flow', 'Are you sure you want to delete the data flow?', 'Yes', 'No')
      .result.then(
      () => vm.doDelete(item),
      () => vm.log.info('Delete cancelled')
    );
  }

  doDelete(item: DataFlow) {
    const vm = this;
    vm.dataFlowService.deleteDataFlow(item.uuid)
      .subscribe(
        () => {
          const index = vm.dataflows.indexOf(item);
          vm.dataflows.splice(index, 1);
          vm.log.success('Data flow deleted', item, 'Delete Data flow');
        },
        (error) => vm.log.error('Failed to delete Data flow', error, 'Delete Data flow')
      );
  }

  close() {
    this.router.navigate(['/dataSharingOverview']);
  }

}
