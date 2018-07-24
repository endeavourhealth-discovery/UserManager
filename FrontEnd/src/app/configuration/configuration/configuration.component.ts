import { Component, OnInit } from '@angular/core';
import {LoggerService, SecurityService} from "eds-angular4";
import {ConfigurationService} from "../configuration.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {RoleType} from "../models/RoleType";

@Component({
  selector: 'app-configuration',
  templateUrl: './configuration.component.html',
  styleUrls: ['./configuration.component.css']
})
export class ConfigurationComponent implements OnInit {
  roleTypes: RoleType[];

  constructor(public log:LoggerService,
              private configurationService : ConfigurationService,
              private securityService : SecurityService,
              private $modal : NgbModal) { }

  ngOnInit() {
    const vm = this;
    vm.getRoleTypes()
  }

  getRoleTypes(){
    let vm = this;
    vm.configurationService.getRoleTypes()
      .subscribe(
        (result) => {
          vm.roleTypes = result;
        },
        (error) => vm.log.error('Error loading roles types', error, 'Error')
      );
  }

}
