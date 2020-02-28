import {Component, Injectable, OnInit} from '@angular/core';
import {DelegationData} from "../../d3-delegation/models/DelegationData";
import {Delegation} from "../../d3-delegation/models/Delegation";
import {Organisation} from "../../organisation/models/Organisation";
import {DelegationRelationship} from "../../d3-delegation/models/DelegationRelationship";
import {UserProject} from "dds-angular8/lib/user-manager/models/UserProject";
import {LoggerService, MessageBoxDialogComponent, UserManagerService} from "dds-angular8";
import {DelegationService} from "../delegation.service";
import {Router} from "@angular/router";
import {MatDialog, MatTreeFlatDataSource, MatTreeFlattener} from "@angular/material";
import {FlatTreeControl} from "@angular/cdk/tree";
import {SelectionModel} from "@angular/cdk/collections";
import {BehaviorSubject} from "rxjs/Rx";
import {OrganisationPickerComponent} from "../../organisation/organisation-picker/organisation-picker.component";


/**
 * Node for delegation item
 */
export class DelegationNode {
  children: DelegationNode[];
  name: string;
  uuid: string;
}

/** Flat delegation item node with expandable and level information */
export class DelegationFlatNode {
  name: string;
  uuid: string;
  level: number;
  expandable: boolean;
  disabled = false;
}

@Component({
  selector: 'app-delegation',
  templateUrl: './delegation.component.html',
  styleUrls: ['./delegation.component.scss']
})
export class DelegationComponent implements OnInit {

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
  treeData: DelegationNode[] = [];

  public activeProject: UserProject;
  admin = false;
  superUser = false;

  constructor(public log:LoggerService,
              private delegationService: DelegationService,
              private router: Router,
              private userManagerService: UserManagerService,
              public dialog: MatDialog,
              private _database: ChecklistDatabase) {

    this.treeFlattener = new MatTreeFlattener(this.transformer, this.getLevel,
      this.isExpandable, this.getChildren);
    this.treeControl = new FlatTreeControl<DelegationFlatNode>(this.getLevel, this.isExpandable);
    this.dataSource = new MatTreeFlatDataSource(this.treeControl, this.treeFlattener);

    _database.dataChange.subscribe(data => {
      this.dataSource.data = data;
      console.log(data);
    });
  }

  ngOnInit() {

    this.userManagerService.onProjectChange.subscribe(active => {
      this.activeProject = active;
      this.roleChanged();
    });
  }

  roleChanged() {

    if (this.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Super User') != null) {
      this.admin = true;
      this.superUser = true;
    } else if (this.activeProject.applicationPolicyAttributes.find(x => x.applicationAccessProfileName == 'Admin') != null) {
      this.admin = true;
      this.superUser = false;
    } else {
      this.admin = false;
      this.superUser = false;
    }
    this.getDelegations();
  }

  getDelegationRelationships(delegationId: string){

    this.delegationService.getDelegationRelationships(delegationId)
      .subscribe(
        (result) => {
          console.log('relationships', result);
          this.delegationRelationships = result;
          this.getDelegationDataD3(delegationId);
        },
        (error) => this.log.error('Error loading delegations')
      );
  }

  getDelegationDataD3(delegationId: string){

    this.delegationService.getTreeData(delegationId)
      .subscribe(
        (result) => {
          this.root = result;
          this.treeData.push(result);
          this._database.initialize(this.treeData);
        },
        (error) => this.log.error('Error loading delegations')
      );
  }

  deleteChildOrganisation() {

    console.log(this.checklistSelection.selected);
    /*if (this.selectedOrganisation == null) {
      MessageBoxDialogComponent.open(this.dialog, 'Select an organisation', 'You must select an organisation before deleting', 'Ok', '')
        .subscribe(
          (result) => {
              return}
      );
    } else {
      this.selectedRelationship.isDeleted = true;
      this.saveRelationship(true);
    }*/
  }

  goToOrganisation() {

    this.router.navigate(['/user', this.selectedOrganisation.uuid]);
  }

  getDelegations() {

    this.delegationService.getDelegations(this.superUser ? null :  this.activeProject.organisationId)
      .subscribe(
        (result) => {
          this.delegations = result;
          console.log('delegations', result);
          if (this.delegations.length > 0) {
            if (this.newDelegation != null) {
              this.selectedDelegation = this.delegations.find(value => value.name === this.newDelegation.name);
            }
            this.selectedDelegation = this.delegations[0];
            this.getDelegationRelationships(this.selectedDelegation.uuid);
          }
        },
        (error) => this.log.error('Error loading delegations')
      );
  }

  loadDelegation() {

    this.getDelegationRelationships(this.selectedDelegation.uuid);
  }

  saveNewRelationship(parentUUID: string, childUUID: string) {

    let childRel = new DelegationRelationship();
    childRel.parentUuid = parentUUID;
    childRel.parentType = 0;
    childRel.delegation = this.selectedDelegation.uuid;
    childRel.childUuid = childUUID;
    childRel.childType = 0;
    childRel.includeAllChildren = 0;
    childRel.createUsers = true;
    childRel.createSuperUsers = true;

    this.delegationService.saveRelationship(childRel, this.activeProject.id)
      .subscribe(
        (result) => {
          this.log.success('Successfully saved changes')
        },
        (error) => this.log.error('Error saving details')
      );
  }

  /** Map from flat node to nested node. This helps us finding the nested node to be modified */
  flatNodeMap = new Map<DelegationFlatNode, DelegationNode>();

  /** Map from nested node to flattened node. This helps us to keep the same object for selection */
  nestedNodeMap = new Map<DelegationNode, DelegationFlatNode>();

  /** A selected parent node to be inserted */
  selectedParent: DelegationFlatNode | null = null;

  treeControl: FlatTreeControl<DelegationFlatNode>;

  treeFlattener: MatTreeFlattener<DelegationNode, DelegationFlatNode>;

  dataSource: MatTreeFlatDataSource<DelegationNode, DelegationFlatNode>;

  /** The selection for checklist */
  checklistSelection = new SelectionModel<DelegationFlatNode>(true /* multiple */);

  getLevel = (node: DelegationFlatNode) => node.level;

  isExpandable = (node: DelegationFlatNode) => node.expandable;

  getChildren = (node: DelegationNode): DelegationNode[] => node.children;

  hasChild = (_: number, _nodeData: DelegationFlatNode) => _nodeData.expandable;

  hasNoContent = (_: number, _nodeData: DelegationFlatNode) => _nodeData.name === '';

  /**
   * Transformer to convert nested node to flat node. Record the nodes in maps for later use.
   */
  transformer = (node: DelegationNode, level: number) => {
    const existingNode = this.nestedNodeMap.get(node);
    const flatNode = existingNode && existingNode.name === node.name
      ? existingNode
      : new DelegationFlatNode();
    flatNode.name = node.name;
    flatNode.uuid = node.uuid;
    flatNode.level = level;
    flatNode.expandable = !!node.children;
    this.flatNodeMap.set(flatNode, node);
    this.nestedNodeMap.set(node, flatNode);
    return flatNode;
  }

  /** Whether all the descendants of the node are selected. */
  descendantsAllSelected(node: DelegationFlatNode): boolean {
    const descendants = this.treeControl.getDescendants(node);
    const descAllSelected = descendants.every(child =>
      this.checklistSelection.isSelected(child)
    );
    return descAllSelected;
  }

  /** Whether part of the descendants are selected */
  descendantsPartiallySelected(node: DelegationFlatNode): boolean {
    const descendants = this.treeControl.getDescendants(node);
    const result = descendants.some(child => this.checklistSelection.isSelected(child));
    return result && !this.descendantsAllSelected(node);
  }

  /** Toggle the to-do item selection. Select/deselect all the descendants node */
  todoItemSelectionToggle(node: DelegationFlatNode): void {
    this.checklistSelection.toggle(node);
    const descendants = this.treeControl.getDescendants(node);
    this.checklistSelection.isSelected(node)
      ? this.checklistSelection.select(...descendants)
      : this.checklistSelection.deselect(...descendants);

    // Force update for the parent
    /*descendants.every(child => {
        this.checklistSelection.isSelected(child);
      }
    );*/
    descendants.forEach(child => {
        child.disabled = this.checklistSelection.isSelected(node);
      }
    );
    this.checkAllParentsSelection(node);
  }

  /* Checks all the parents when a leaf node is selected/unselected */
  checkAllParentsSelection(node: DelegationFlatNode): void {
    let parent: DelegationFlatNode | null = this.getParentNode(node);
    while (parent) {
      this.checkRootNodeSelection(parent);
      parent = this.getParentNode(parent);
    }
  }

  /** Check root node checked state and change it accordingly */
  checkRootNodeSelection(node: DelegationFlatNode): void {
    const nodeSelected = this.checklistSelection.isSelected(node);
    const descendants = this.treeControl.getDescendants(node);
    const descAllSelected = descendants.every(child =>
      this.checklistSelection.isSelected(child)
    );
    if (nodeSelected && !descAllSelected) {
      this.checklistSelection.deselect(node);
    } else if (!nodeSelected && descAllSelected) {
      this.checklistSelection.select(node);
    }
  }

  /* Get the parent node of a node */
  getParentNode(node: DelegationFlatNode): DelegationFlatNode | null {
    const currentLevel = this.getLevel(node);

    if (currentLevel < 1) {
      return null;
    }

    const startIndex = this.treeControl.dataNodes.indexOf(node) - 1;

    for (let i = startIndex; i >= 0; i--) {
      const currentNode = this.treeControl.dataNodes[i];

      if (this.getLevel(currentNode) < currentLevel) {
        return currentNode;
      }
    }
    return null;
  }

  /** Select the category so we can insert the new item. */
  addNewItem(node: DelegationFlatNode) {

    this.selectedOrgs = [];
    const parentNode = this.flatNodeMap.get(node);
    let parent = this.selectedOrganisation;
    const dialogRef = this.dialog.open(OrganisationPickerComponent, {
      minWidth: '50vw',
      data: { existing: [] }  // fix this
    })
    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }
      for (let org of result) {
        this._database.insertItem(parentNode!, org.name, org.uuid);
        this.treeControl.expand(node);
        this.saveNewRelationship(parentNode.uuid, org.uuid);
      }
    })
  }

  /** Save the node to database */
  saveNode(node: DelegationFlatNode, itemValue: string) {
    const nestedNode = this.flatNodeMap.get(node);
    this._database.updateItem(nestedNode!, itemValue);
  }
}

/**
 * Checklist database, it can build a tree structured Json object.
 * Each node in Json object represents a to-do item or a category.
 * If a node is a category, it has children items and new items can be added under the category.
 */
@Injectable()
export class ChecklistDatabase {
  dataChange = new BehaviorSubject<DelegationNode[]>([]);

  get data(): DelegationNode[] { return this.dataChange.value; }

  constructor() {
    // this.initialize(TREE_DATA);
  }

  initialize(treeData: any) {
    // Build the tree nodes from Json object. The result is a list of `DelegationNode` with nested
    //     file node as children.
    // const data = this.buildFileTree(treeData, 0);

    // Notify the change.
    this.dataChange.next(treeData);
  }

  /**
   * Build the file structure tree. The `value` is the Json object, or a sub-tree of a Json object.
   * The return value is the list of `DelegationNode`.
   */
  buildFileTree(obj: {[key: string]: any}, level: number): DelegationNode[] {
    return Object.keys(obj).reduce<DelegationNode[]>((accumulator, key) => {
      const value = obj[key];
      const node = new DelegationNode();
      node.name = key;

      if (value != null) {
        if (typeof value === 'object') {
          node.children = this.buildFileTree(value, level + 1);
        } else {
          node.name = value;
        }
      }

      return accumulator.concat(node);
    }, []);
  }

  /** Add an item to to-do list */
  insertItem(parent: DelegationNode, name: string, uuid: string) {
    if (!parent.children) {
      parent.children = [];
    }
    parent.children.push({name: name, uuid: uuid} as DelegationNode);
    this.dataChange.next(this.data);
  }

  updateItem(node: DelegationNode, name: string) {
    node.name = name;
    this.dataChange.next(this.data);
  }
}
