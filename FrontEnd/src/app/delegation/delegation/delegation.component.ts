import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {DelegationData} from "../models/DelegationData";
import {DelegationService} from "../delegation.service";
import {LoggerService, MessageBoxDialog} from "eds-angular4";
import {Delegation} from "../models/Delegation";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {TreeGraphComponent} from "../../tree-graph/tree-graph/tree-graph.component";
import {OrganisationPickerComponent} from "../../organisation/organisation-picker/organisation-picker.component";
import {Organisation} from "../../organisation/models/Organisation";
declare var $: any;

@Component({
  selector: 'app-delegation',
  templateUrl: './delegation.component.html',
  styleUrls: ['./delegation.component.css']
})
export class DelegationComponent implements OnInit, AfterViewInit {
  delegationData: DelegationData;
  data: any;
  delegations: Delegation[];
  selectedDelegation: Delegation;
  orgchart: any;
  chartDef : any;
  selectedOrgs: Organisation[];

  root: any;



  @ViewChild('graphContainer') graphContainer: any;
  @ViewChild('delegate-chart') chart: any;
  @ViewChild("treeChart") treeChart: TreeGraphComponent;

  constructor(public log:LoggerService,
              private $modal: NgbModal,
              private delegationService: DelegationService) { }

  ngOnInit() {

    // this.bindEventHandler('.node', 'click', this.clickNode, '#delegate-chart');

    this.getDelegations();
    this.getDelegationData('416fae5a-88e1-11e8-91d9-80fa5b320513');
  }

  //gets all users in the realm
  getDelegationData(delegationId: string){
    let vm = this;
    vm.delegationService.getTreeData(delegationId)
      .subscribe(
        (result) => {
          vm.delegationData = result;
          vm.refresh();
        },
        (error) => vm.log.error('Error loading delegations', error, 'Error')
      );
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
        },
        (error) => vm.log.error('Error loading delegations', error, 'Error')
      );
  }

  loadDelegation() {
    const vm = this;
    vm.getDelegationData(vm.selectedDelegation.uuid);
  }

  refresh() {
    const vm = this;
    /*vm.orgchart = new OrgChart({
      'chartContainer': '#delegate-Chart',
      'data': vm.delegationData,
      'nodeTitle' : 'displayName',
      'nodeContent' : 'odsCode'
    });
*/

    console.log(vm.delegationData);
    vm.chartDef = {
      'data': vm.delegationData,
      'nodeTitle' : 'name',
      'nodeContent' : 'odsCode',
      'direction': 'l2r'
    };
/*
    console.log(vm.orgchart);*/

    /*$('#delegate-chart').empty();
    var delegate = $('#delegate-chart').orgchart({
      'data': this.delegationData,
      'toggleSiblingsResp' : false,
      'nodeTitle' : 'displayName',
      'nodeContent' : 'odsCode'
    });*/
  }

  nodeClick($event) {
    let selected = this.treeChart.getSelected();
    if (selected == null) {
      return;
    }

    let org = JSON.parse(selected.getAttribute('data-source'));
    // this.treeChart.addChild(selected, {'displayName': 'some bloke', 'title': 'Test'});
    let list = [1, 2, 3];

    for (let i of list) {
      // this.actuallyAdd(selected, {'displayName': 'some bloke', 'title': 'Test'});
    }
  }

  actuallyAdd(node: any, data: any) {
    this.treeChart.addChild(node, data);
  }

    /*console.log(selected);
    console.log(selected.getAttribute('data-source'));*/
    //this.treeChart.addChild(selected, { 'name': 'Some bloke', 'title': 'Test' });
    //this.treeChart.addParent(selected, { 'displayName': 'Some bloke', 'odsCode': 'Test' });
  // }



  ngAfterViewInit() {

    // this.graphContainer.nativeElement.style.height = (window.innerHeight * 0.7) + 'px';
  }

  addChildOrganisation() {
    const vm = this;
    let selected = this.treeChart.getSelected();
    if (selected == null) {
      MessageBoxDialog.open(vm.$modal, 'Select an organisation', 'You must select an organisation before adding a child', 'Ok', '')
        .result.then(
        () => {return},
        () => {return}
      );
    } else {
      vm.selectOrganisations(selected);
    }
  }

  private selectOrganisations(node: any) {
    const vm = this;
    vm.selectedOrgs = [];
    let parent = node.getAttribute('data-source');
    OrganisationPickerComponent.open(vm.$modal, vm.selectedOrgs, 'organisation', parent.uuid )
      .result.then(function (result: Organisation[]) {
      vm.selectedOrgs = result;
      vm.addChildOrganisationToChart();
    });
  }

  addChildOrganisationToChart() {
    const vm = this;
    let selected = this.treeChart.getSelected();
    if (selected != null) {
      let childData: DelegationData[] = [];
      for(let org of vm.selectedOrgs) {
        let child = new DelegationData();
        child.uuid = org.uuid;
        child.name = org.name;
        child.createUsers = false;
        child.createSuperUsers = false;
        childData.push(child);
      }
      let parent = selected.getAttribute('data-source');
      this.treeChart.addChildren(selected, childData);
      // this.addToDelegationData(childData, selected);
    }
  }

  showNode() {
    // let selectedNode = document.getElementById(document.getElementById('node.focused').dataset.node);

    // $('#delegate-chart').orgchart.
    // console.log($('#selected-node').val(this.find('.title').text()).data('node', $this););
  }


  /*bindEventHandler(selector, type, fn, parentSelector) {
    if (parentSelector) {
      document.querySelector(parentSelector).addEventListener(type, function (event) {
        if ((event.target.classList && event.target.classList.contains(selector.slice(1))) ||
          this.closest(event.target, el => el.classList && el.classList.contains(selector.slice(1)))) {
          fn(event);
        }
      });
    } else {
      document.querySelectorAll(selector).forEach(element => {
        element.addEventListener(type, fn);
      });
    }
  }*/

  /*clickNode(event) {
    console.log('clicked');
    // let sNode = this.closest(event.target, el => el.classList && el.classList.contains('node')),
    let sNodeInput = document.getElementById('selected-node');

    // sNodeInput.value = sNode.querySelector('.title').textContent;
    // sNodeInput.dataset.node = sNode.id;
  }

  closest(el, fn) {
    return el && ((fn(el) && el !== document.querySelector('.orgchart')) ? el : this.closest(el.parentNode, fn));
  }*/

}
