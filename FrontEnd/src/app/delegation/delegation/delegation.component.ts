import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {Delegation} from "../models/Delegation";
import {DelegationService} from "../delegation.service";
import {LoggerService} from "eds-angular4";
declare var $: any;

@Component({
  selector: 'app-delegation',
  templateUrl: './delegation.component.html',
  styleUrls: ['./delegation.component.css']
})
export class DelegationComponent implements OnInit, AfterViewInit {
  delegation: Delegation;
  data: any;
  selectedNode : any;

  treeData : any;

  westData : any = {
    'id': 'rootNode', // It's a optional property which will be used as id attribute of node
    // and data-parent attribute, which contains the id of the parent node
    'collapsed': false, // By default, the children nodes of current node is hidden.
    'className': 'top-level', // It's a optional property which will be used as className attribute of node.
    'name': 'BRUNSWICK MEDICAL CENTRE UHPC',
    'ods': '(F83048)', // Note: when you activate ondemand loading nodes feature,
    // you should use json datsource (local or remote) and set this property.
    // This property implies that whether this node has parent node, siblings nodes or children nodes.
    // relationshipValue is a string composed of three "0/1" identifier.
    // First character stands for wether current node has parent node;
    // Scond character stands for wether current node has siblings nodes;
    // Third character stands for wether current node has children node.
    'children': [ // The property stands for nested nodes.
      { 'name': 'Bo Miao', 'ods': 'department manager', 'relationship': '001' },
      { 'name': 'Su Miao', 'ods': 'department manager', 'relationship': '111',
        'children': [
          { 'name': 'Tie Hua', 'ods': 'senior engineer', 'relationship': '110' },
          { 'name': 'Hei Hei', 'ods': 'senior engineer', 'relationship': '110' }
        ]
      },
      { 'name': 'Yu Jie', 'ods': 'department manager', 'relationship': '110' }
    ]
  };

  public treeConfig = {
    nodeWidth: 350,
    nodeHeight: 100,
  };

  @ViewChild('graphContainer') graphContainer: any;

  constructor(public log:LoggerService,
              private delegationService: DelegationService) { }

  ngOnInit() {
    this.getDelegations();
  }

  //gets all users in the realm
  getDelegations(){
    let vm = this;
    vm.delegationService.getDelegations('416fae5a-88e1-11e8-91d9-80fa5b320513')
      .subscribe(
        (result) => {
          vm.delegation = result;
          console.log(result);
          vm.refresh();
        },
        (error) => vm.log.error('Error loading delegations', error, 'Error')
      );
  }

  refresh() {
    const vm = this;
    console.log('display');
    vm.treeData = {
      json : vm.delegation,
      config : vm.treeConfig
    };
    var delegate = $('#delegate-chart').orgchart({
      'data': this.delegation,
      'toggleSiblingsResp' : false,
      'nodeTitle' : 'displayName',
      'nodeContent' : 'odsCode'
    });
  }

  ngAfterViewInit() {

    this.graphContainer.nativeElement.style.height = (window.innerHeight * 0.7) + 'px';
  }

  Check() {
    console.log(this.treeData);
  }

}
