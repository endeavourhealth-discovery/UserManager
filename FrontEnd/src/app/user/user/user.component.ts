import {Component, OnInit, ViewChild} from '@angular/core';
import {LoggerService, SecurityService} from "eds-angular4";
import {UserService} from "../user.service";
import {NgbModal, NgbTabChangeEvent} from "@ng-bootstrap/ng-bootstrap";
import {User} from "../models/User";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  userList: User[];
  selectedUser : User = null;
  filteredUserList : User[];
  sortReverse : boolean;
  sortField = 'username';

  constructor(public log:LoggerService,
              private userService : UserService,
              private securityService : SecurityService,
              private $modal : NgbModal) {

    this.getUsers();
  }

  ngOnInit() {
  }

  //gets all users in the realm
  getUsers(){
    let vm = this;
    vm.userList = null;
    vm.userService.getUsers()
      .subscribe(
        (result) => {
          vm.userList = result;
          vm.filteredUserList = result;
          console.log(result);
          vm.selectTopUser();
        },
        (error) => vm.log.error('Error loading users and roles', error, 'Error')
      );
  }

  sort(property: string) {
    const vm = this;
    vm.sortField = property;
    vm.sortReverse = !vm.sortReverse;

    vm.filteredUserList = vm.filteredUserList.sort(function(a, b) {
      var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
      if (vm.sortReverse) {
        return result * -1;
      } else {
        return result;
      }
    })
  }

  selectTopUser(){
    let vm = this;
    if (vm.filteredUserList != null && vm.filteredUserList.length > 0)
    {
      let topUserInList = vm.filteredUserList[0];
      vm.selectedUser = topUserInList;
      console.log(vm.selectedUser);
    }
    else {
      vm.selectedUser = null;
    }
  }

  hasPermission(role : string) : boolean {
    return this.securityService.hasPermission('eds-user-manager', role);
  }


}
