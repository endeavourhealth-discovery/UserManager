import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {DelegationData} from "../models/DelegationData";
import {DelegationService} from "../delegation.service";
import {LoggerService, MessageBoxDialog, UserManagerNotificationService, UserManagerService} from "eds-angular4";
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {Delegation} from "../models/Delegation";
import {D3TreeGraphComponent} from "../../d3-tree-graph/d3-tree-graph/d3-tree-graph.component";
import {Organisation} from "../../organisation/models/Organisation";
import {OrganisationPickerComponent} from "../../organisation/organisation-picker/organisation-picker.component";
import {DelegationRelationship} from "../models/DelegationRelationship";
import {DelegationCreatorComponent} from "../delegation-creator/delegation-creator.component";
import {Router} from "@angular/router";
import {UserProject} from "../../user/models/UserProject";



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
  delegationRelationships : DelegationRelationship[];
  selectedRelationship: DelegationRelationship;
  selectedIsRoot: boolean;
  newDelegation: Delegation;

  public activeProject: UserProject;
  admin = false;
  superUser = false;


  @ViewChild("d3tree") d3Tree: D3TreeGraphComponent;
  @ViewChild('graphContainer') graphContainer: any;
  @ViewChild(D3TreeGraphComponent) d3TreeComponent: D3TreeGraphComponent;

  constructor(public log:LoggerService,
              private $modal: NgbModal,
              private delegationService: DelegationService,
              private router: Router,
              private userManagerNotificationService: UserManagerNotificationService) { }

  ngOnInit() {

    this.userManagerNotificationService.activeUserProject.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });
  }

  roleChanged() {
    const vm = this;
    if (vm.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Super User') != null) {
      vm.admin = true;
      vm.superUser = true;
    } else if (vm.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Admin') != null) {
      vm.admin = true;
      vm.superUser = false;
    } else {
      vm.admin = false;
      vm.superUser = false;
    }
    this.getDelegations();
  }

  getDelegationRelationships(delegationId: string){
    let vm = this;
    vm.delegationService.getDelegationRelationships(delegationId)
      .subscribe(
        (result) => {
          vm.delegationRelationships = result;
          vm.getDelegationDataD3(delegationId);
        },
        (error) => vm.log.error('Error loading delegations', error, 'Error')
      );
  }

  getDelegationDataD3(delegationId: string){
    let vm = this;
    vm.delegationService.getTreeData(delegationId)
      .subscribe(
        (result) => {
          vm.root = result;
          vm.d3Tree.setData(result);
          vm.d3Tree.draw();
          // vm.refresh();
        },
        (error) => vm.log.error('Error loading delegations', error, 'Error')
      );
  }

  nodeClick($event) {
    const vm = this;
    vm.selectedIsRoot = false;
    vm.selectedOrganisation = $event;
    var result = vm.delegationRelationships.find(e => e.childUuid === $event.uuid);
    if (result == null) {
      result = vm.delegationRelationships.find(e => e.parentUuid === $event.uuid);
      vm.selectedIsRoot = true;
    }
    vm.selectedRelationship = result;
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
      vm.selectOrganisations();
    }
  }

  deleteChildOrganisation() {
    const vm = this;
    if (vm.selectedOrganisation == null) {
      MessageBoxDialog.open(vm.$modal, 'Select an organisation', 'You must select an organisation before deleting', 'Ok', '')
        .result.then(
        () => {return},
        () => {return}
      );
    } else {
      vm.selectedRelationship.isDeleted = true;
      vm.saveRelationship(true);
    }
  }

  private selectOrganisations() {
    const vm = this;
    vm.selectedOrgs = [];
    let parent = vm.selectedOrganisation;
    OrganisationPickerComponent.open(vm.$modal, vm.selectedOrgs, 'organisation', parent.uuid )
      .result.then(function (result: Organisation[]) {
      vm.selectedOrgs = result;
      vm.addChildOrganisationToChart();
    });
  }

  goToOrganisation() {
    const vm = this;
    this.router.navigate(['/user', vm.selectedOrganisation.uuid]);
  }

  createDelegation() {
    const vm = this;
    DelegationCreatorComponent.open(vm.$modal)
      .result.then(function (result: Delegation) {
      vm.newDelegation = result;
      vm.saveDelegation();
    });
  }

  saveDelegation() {
    const vm = this;
    vm.delegationService.saveDelegation(vm.newDelegation, vm.activeProject.id)
      .subscribe(
        (result) => {
          vm.log.success('Successfully saved changes', null, 'Success');
          vm.getDelegations();
        },
        (error) => vm.log.error('Error saving details', error, 'Error')
      );
  }

  addChildOrganisationToChart() {
    const vm = this;
    let selected = vm.selectedOrganisation;
    if (selected != null) {
      let childData: DelegationData[] = [];
      for(let org of vm.selectedOrgs) {
        let child = new DelegationData();
        child.uuid = org.uuid;
        child.name = org.name;
        child.createUsers = false;
        child.createSuperUsers = false;
        childData.push(child);
        vm.saveNewRelationship(org);

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
    vm.delegationService.getDelegations(vm.superUser ? null :  vm.activeProject.organisationId)
      .subscribe(
        (result) => {
          vm.delegations = result;
          if (vm.delegations.length > 0) {
            if (vm.newDelegation != null) {
              vm.selectedDelegation = vm.delegations.find(value => value.name === vm.newDelegation.name);
            }
            vm.selectedDelegation = vm.delegations[0];
            vm.getDelegationRelationships(vm.selectedDelegation.uuid);
          }
        },
        (error) => vm.log.error('Error loading delegations', error, 'Error')
      );
  }

  loadDelegation() {
    const vm = this;
    vm.getDelegationRelationships(vm.selectedDelegation.uuid);
  }

  ngAfterViewInit() {

    this.graphContainer.nativeElement.style.height = (window.innerHeight * 7) + 'px';
  }

  saveRelationship(deleting: boolean = false) {
    const vm = this;
    let message = 'Successfully saved changes to organisation';
    let title = 'Save organisation';
    let errorMessage = 'The organisation could not be saved. Please try again.'
    if (deleting) {
      message = 'Successfully deleted organisation.';
      title = 'Delete organisation';
      errorMessage = 'The organisation could not be deleted. Please try again.';
    }
    vm.delegationService.saveRelationship(vm.selectedRelationship, vm.activeProject.id)
      .subscribe(
        (result) => {
          vm.log.success(message, null, title);
          vm.loadDelegation();
        },
        (error) => vm.log.error(errorMessage, error, title)
      );
  }

  saveNewRelationship(org: Organisation) {
    const vm = this;
    let childRel = new DelegationRelationship();
    childRel.parentUuid = vm.selectedOrganisation.uuid;
    childRel.parentType = 0;
    childRel.delegation = vm.selectedDelegation.uuid;
    childRel.childUuid = org.uuid;
    childRel.childType = 0;
    childRel.includeAllChildren = 0;
    childRel.createUsers = org.createUsers;
    childRel.createSuperUsers = org.createSuperUsers;

    vm.delegationService.saveRelationship(childRel, vm.activeProject.id)
      .subscribe(
        (result) => {
          vm.log.success('Successfully saved changes', null, 'Success')
        },
        (error) => vm.log.error('Error saving details', error, 'Error')
      );
  }

  deleteDelegation() {
    const vm = this;
    MessageBoxDialog.open(vm.$modal, "Confirmation", "Delete delegation: " + vm.selectedDelegation.name + "?", "Yes", "No")
      .result.then(
      (result) => {
        vm.delegationService.deleteDelegation(vm.selectedDelegation.uuid, vm.activeProject.id)
          .subscribe(
            (result) => {
              vm.log.success('Successfully deleted delegation', null, 'Success');
              vm.getDelegations();
            },
            (error) => vm.log.error('Error deleting delegation', error, 'Error')
          );
      },
      (reason) => {
      }
    )
  }

}
