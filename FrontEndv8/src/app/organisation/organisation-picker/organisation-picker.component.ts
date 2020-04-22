import {Component, OnInit, Input, Inject, ViewChild} from '@angular/core';
import {Organisation} from '../models/Organisation';
import {OrganisationService} from '../organisation.service';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material";
import {GenericTableComponent, LoggerService} from "dds-angular8";

export interface OrganisationPickerData {
  existingOrgs: Organisation[];
}

@Component({
  selector: 'app-organisation-picker',
  templateUrl: './organisation-picker.component.html',
  styleUrls: ['./organisation-picker.component.css']
})
export class OrganisationPickerComponent implements OnInit {
  searchData: string;
  searchResults: Organisation[];
  orgDetailsToShow = new Organisation().getDisplayItems();

  @ViewChild('orgPicker', { static: false }) orgPicker: GenericTableComponent;

  constructor(
    public dialogRef: MatDialogRef<OrganisationPickerComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OrganisationPickerData,
    private organisationService: OrganisationService,
    public dialog: MatDialog,
    private log: LoggerService) {


  }

  ngOnInit() {
  }

  search() {
    this.organisationService.search(this.searchData)
      .subscribe(
        (result) => {
          this.searchResults = result; // this.filterResults(result)
        },
        (error) => this.log.error(error)
      );
  }

  filterResults(results: Organisation[]) {
    let filterResults: Organisation[];
    const existing = this.data.existingOrgs;

    filterResults = results.filter((x) => !existing.filter(y => y.uuid === x.uuid).length);

    return filterResults;
  }

  ok() {
    this.dialogRef.close(this.orgPicker.selection.selected);
  }

  cancel() {
    this.dialogRef.close();
  }


  clear() {
    this.searchData = '';
  }
}
