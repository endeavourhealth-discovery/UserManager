import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {GenericTableComponent, LoggerService} from "dds-angular8";
import { MatDialogRef } from '@angular/material';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {StandardPickerData} from "../../models/StandardPickerData";
import {User} from "../../models/User";
import {UserService} from "../user.service";

@Component({
  selector: 'app-user-picker',
  templateUrl: './user-picker.component.html',
  styleUrls: ['./user-picker.component.scss']
})
export class UserPickerComponent implements OnInit {

  searchResults: User[];
  userDetailsToShow = new User().getDisplayItems();
  organisationId: string;

  @ViewChild('picker', { static: false }) picker: GenericTableComponent;

  constructor(public dialogRef: MatDialogRef<UserPickerComponent>,
              @Inject(MAT_DIALOG_DATA) public data: StandardPickerData,
              private log: LoggerService,
              private userService: UserService) {

    this.organisationId = data.userId;
  }

  ngOnInit() {
    this.searchResults = null;
    this.userService.getUsers(this.organisationId, ' ')
      .subscribe(
        (result) => {
          this.searchResults = this.filterResults(result);
        },
        (error) => this.log.error('Error loading users')
      );
  }

  filterResults(results: User[]) {
    let filterResults: User[];
    const existing = this.data.existing;

    filterResults = results.filter((x) => !existing.filter(y => y.uuid === x.uuid).length);

    return filterResults;
  }

  ok() {
    this.dialogRef.close(this.picker.selection.selected);
  }

  cancel() {
    this.dialogRef.close();
  }
}
