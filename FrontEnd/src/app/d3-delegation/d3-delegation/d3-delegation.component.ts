import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {DelegationData} from "../../delegation/models/DelegationData";
import {DelegationService} from "../../delegation/delegation.service";
import {LoggerService, MessageBoxDialog} from "eds-angular4";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Delegation} from "../../delegation/models/Delegation";
import {D3TreeGraphComponent} from "../../d3-tree-graph/d3-tree-graph/d3-tree-graph.component";
import {Organisation} from "../../organisation/models/Organisation";
import {OrganisationPickerComponent} from "../../organisation/organisation-picker/organisation-picker.component";



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
  selectedOrganisation: DelegationData;
  selectedOrgs: Organisation[];


  @ViewChild("d3tree") d3Tree: D3TreeGraphComponent;
  @ViewChild('graphContainer') graphContainer: any;
  @ViewChild(D3TreeGraphComponent) d3TreeComponent: D3TreeGraphComponent;

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
    this.selectedOrganisation = $event;
    console.log($event);
  }

  addChildOrganisation() {
    const vm = this;
    if (vm.selectedOrganisation == null) {
      MessageBoxDialog.open(vm.$modal, 'Select an organisation', 'You must select an organisation before adding a child', 'Ok', '')
        .result.then(
        () => {return},
        () => {return}
      );
    } else {
      vm.selectOrganisations(vm.selectedOrganisation);
    }
  }

  private selectOrganisations(node: any) {
    const vm = this;
    vm.selectedOrgs = [];
    let parent = vm.selectedOrganisation;
    OrganisationPickerComponent.open(vm.$modal, vm.selectedOrgs, 'organisation', parent.uuid )
      .result.then(function (result: Organisation[]) {
      vm.selectedOrgs = result;
      vm.addChildOrganisationToChart();
    });
  }

  addChildOrganisationToChart() {
    const vm = this;
    let selected = vm.selectedOrganisation;
    console.log(selected);
    if (selected != null) {
      let childData: DelegationData[] = [];
      for(let org of vm.selectedOrgs) {
        let child = new DelegationData();
        child.uuid = org.uuid;
        child.name = org.name;
        child.odsCode = org.odsCode;
        childData.push(child);
      }

      this.d3TreeComponent.addChild(childData);
      // let parent = selected.getAttribute('data-source');
      // this.treeChart.addChildren(selected, childData);
      // this.addToDelegationData(childData, selected);
    }
    /*const vm = this;
    var data = [
      {"name": "Eve",   "parent": ""},
      {"name": "Cain",  "parent": "Eve"},
      {"name": "Seth",  "parent": "Eve"},
      {"name": "Enos",  "parent": "Seth"},
      {"name": "Noam",  "parent": "Seth"},
      {"name": "Abel",  "parent": "Eve"},
      {"name": "Awan",  "parent": "Eve"},
      {"name": "Enoch", "parent": "Awan"},
      {"name": "Azura", "parent": "Eve"}
    ];

    console.log('stratifying');
    var table = vm.d3Tree.stratify(data);

    console.log(table);
    vm.d3Tree.setData(table);
    vm.d3Tree.draw();*/

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