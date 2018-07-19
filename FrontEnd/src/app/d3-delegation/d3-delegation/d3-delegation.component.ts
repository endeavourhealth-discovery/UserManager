import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {DelegationData} from "../../delegation/models/DelegationData";
import {DelegationService} from "../../delegation/delegation.service";
import {LoggerService} from "eds-angular4";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Delegation} from "../../delegation/models/Delegation";
import {D3TreeGraphComponent} from "../../d3-tree-graph/d3-tree-graph/d3-tree-graph.component";

@Component({
  selector: 'app-d3-delegation',
  templateUrl: './d3-delegation.component.html',
  styleUrls: ['./d3-delegation.component.css']
})
export class D3DelegationComponent implements OnInit, AfterViewInit {
  root: any;
  delegationData: DelegationData;
  delegations: Delegation[];
  selectedDelegation: Delegation;


  @ViewChild("d3tree") d3Tree: D3TreeGraphComponent;
  @ViewChild('graphContainer') graphContainer: any;

  constructor(public log:LoggerService,
              private $modal: NgbModal,
              private delegationService: DelegationService) { }

  ngOnInit() {
    this.getDelegations();
    this.getDelegationDataD3('416fae5a-88e1-11e8-91d9-80fa5b320513');
  }

  getDelegationDataD3(delegationId: string){
    let vm = this;
    vm.delegationService.getDelegationRelationshipsD3(delegationId)
      .subscribe(
        (result) => {
          vm.root = result;
          vm.d3Tree.setData(result);
          vm.d3Tree.draw();
          console.log(result);
          // vm.refresh();
        },
        (error) => vm.log.error('Error loading delegations', error, 'Error')
      );
  }

  nodeClick($event) {
    console.log($event);
  }

  getDelegations() {
    let vm = this;
    vm.delegationService.getDelegations()
      .subscribe(
        (result) => {
          vm.delegations = result;
          if (vm.delegations.length > 0) {
            vm.selectedDelegation = vm.delegations[0];
          }
          console.log(result);
        },
        (error) => vm.log.error('Error loading delegations', error, 'Error')
      );
  }

  loadDelegation() {
    const vm = this;
    vm.getDelegationDataD3(vm.selectedDelegation.uuid);
  }

  ngAfterViewInit() {

    this.graphContainer.nativeElement.style.height = (window.innerHeight * 7) + 'px';
  }

}
