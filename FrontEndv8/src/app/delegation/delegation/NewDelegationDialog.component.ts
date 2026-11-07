import {Delegation} from "../../d3-delegation/models/Delegation";
import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {OrganisationService} from "../../organisation/organisation.service";
import {LoggerService} from "dds-angular8";
import {Organisation} from "../../organisation/models/Organisation";
import {OrganisationPickerData} from "../../organisation/organisation-picker/organisation-picker.component";
import {NewUserDialogComponent} from "../../user/new-user-dialog/new-user-dialog.component";
import {GenericTableComponent} from "dds-angular8";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'new-delegation-dialog',
  templateUrl: './NewDelegationDialog.component.html',
  styleUrls: ['./NewDelegationDialog.component.scss']
})


export class NewDelegationDialogComponent implements OnInit {
  resultData: Delegation;
  searchData: string;
  searchResults: Organisation[];
  selectedOrg: Organisation;
  orgDetailsToShow = new Organisation().getDisplayItems();
  @ViewChild('orgPicker', { static: false }) orgPicker: GenericTableComponent;
  constructor(
    public dialogRef: MatDialogRef<NewDelegationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OrganisationPickerData,
    private organisationService: OrganisationService,
    public dialog: MatDialog,
    private log: LoggerService) {


  }

  ngOnInit() {
    const vm = this;
    vm.resultData = new Delegation();
    vm.resultData.uuid = null;
    vm.resultData.name = '';
  }

  search() {
    const vm = this;
    if (vm.searchData.length < 3) {
      return;
    }
    vm.organisationService.search(vm.searchData)
      .subscribe(
        (result) => vm.searchResults = result,
        (error) => vm.log.error(error)
      );
  }

  public addToSelection(match: Organisation) {
    this.selectedOrg = match;
  }

  public removeFromSelection(match: Organisation) {
    this.selectedOrg = null;
  }

  ok() {
    this.resultData.rootOrganisation = this.selectedOrg.uuid;
    this.dialogRef.close(this.resultData);
  }

  cancel() {
    this.dialogRef.close();
  }

  clear() {
    this.searchData = '';
  }
}
