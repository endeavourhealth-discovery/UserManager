import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {User} from "eds-angular4/dist/security/models/User";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {OrganisationService} from "../../organisation/organisation.service";
import {LoggerService, SecurityService} from "eds-angular4";
import {Router} from "@angular/router";
import {ToastsManager} from "ng2-toastr";
import {Dpa} from "../../data-processing-agreement/models/Dpa";
import {Dsa} from "../../data-sharing-agreement/models/Dsa";

@Component({
  selector: 'app-my-sharing-overview',
  templateUrl: './my-sharing-overview.component.html',
  styleUrls: ['./my-sharing-overview.component.css']
})
export class MySharingOverviewComponent implements OnInit {
  currentUser: User;
  currentOrg = '2dbe7e2a-85a4-3313-a375-391e13182aa4';
  dpaPublishing: Dpa[];
  dsaPublishing: Dsa[];
  dsaSubscribing: Dsa[];

  dpaDetailsToShow = new Dpa().getDisplayItems();
  dsaDetailsToShow = new Dsa().getDisplayItems();

  constructor(private $modal: NgbModal,
              private organisationService: OrganisationService,
              private log: LoggerService,
              private securityService: SecurityService,
              private router: Router,
              public toastr: ToastsManager, vcr: ViewContainerRef) {
    this.toastr.setRootViewContainerRef(vcr);
  }

  ngOnInit() {
    const vm = this;
    vm.currentUser = vm.securityService.getCurrentUser();
    console.log(this.currentUser);
    vm.getDPAsPublishingTo();
    vm.getDSAsPublishingTo();
    vm.getDSAsSubscribingTo();
  }

  private getDPAsPublishingTo() {
    const vm = this;
    vm.organisationService.getDPAPublishing(vm.currentOrg)
      .subscribe(
        result => {vm.dpaPublishing = result; console.log(result);},
        error => vm.log.error('Failed to load DPAs organisation publishing to', error, 'Load organisation DPA Publishers')
      );
  }

  private getDSAsPublishingTo() {
    const vm = this;
    vm.organisationService.getDSAPublishing(vm.currentOrg)
      .subscribe(
        result => {vm.dsaPublishing = result; console.log(result);},
        error => vm.log.error('Failed to load DSAs organisation publishing to', error, 'Load organisation DSA Publishers')
      );
  }

  private getDSAsSubscribingTo() {
    const vm = this;
    vm.organisationService.getDSASubscribing(vm.currentOrg)
      .subscribe(
        result => {vm.dsaSubscribing = result; console.log(result);},
        error => vm.log.error('Failed to load DSAs organisation subscribing to', error, 'Load organisation DSA Publishers')
      );
  }
}
