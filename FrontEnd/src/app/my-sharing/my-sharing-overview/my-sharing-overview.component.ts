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
  userOrgs : string[] = [];
  currentOrg = 'c45ccafd-f86a-4778-845a-96269cad6c3d';  // d79f403b-963d-4817-b4a5-0fbf6a516cb0
  dpaPublishing: Dpa[] = [];
  dsaPublishing: Dsa[] = [];
  dsaSubscribing: Dsa[] = [];

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
    console.log(vm.currentUser);
    vm.getOrganisationsForUser(vm.currentUser);
    // vm.currentOrg = vm.currentUser.organisation;
  }

  private getOrganisationsForUser(user: User) {
    const vm = this;
    vm.userOrgs = user.organisationGroups.map(a => a.organisationId);
    console.log(vm.userOrgs);
    for (let idx in vm.userOrgs) {
      vm.getDPAsPublishingTo(vm.userOrgs[idx]);
      vm.getDSAsPublishingTo(vm.userOrgs[idx]);
      vm.getDSAsSubscribingTo(vm.userOrgs[idx]);
    }
  }

  private getDPAsPublishingTo(org: string) {
    const vm = this;
    vm.organisationService.getDPAPublishing(org)
      .subscribe(
        result => {vm.dpaPublishing.push.apply(vm.dpaPublishing, result);},
        error => vm.log.error('Failed to load DPAs organisation publishing to', error, 'Load organisation DPA Publishers')
      );
  }

  private getDSAsPublishingTo(org: string) {
    const vm = this;
    vm.organisationService.getDSAPublishing(org)
      .subscribe(
        result => {vm.dsaPublishing.push.apply(vm.dsaPublishing, result);},
        error => vm.log.error('Failed to load DSAs organisation publishing to', error, 'Load organisation DSA Publishers')
      );
  }

  private getDSAsSubscribingTo(org: string) {
    const vm = this;
    vm.organisationService.getDSASubscribing(org)
      .subscribe(
        result => {vm.dsaSubscribing.push.apply(vm.dsaSubscribing, result);},
        error => vm.log.error('Failed to load DSAs organisation subscribing to', error, 'Load organisation DSA Publishers')
      );
  }
}
