import {Injectable} from '@angular/core';
import {AbstractMenuProvider } from 'eds-angular4';
import {MenuOption} from 'eds-angular4/dist/layout/models/MenuOption';
import {Routes} from '@angular/router';
import {OrganisationOverviewComponent} from './organisation/organisation-overview/organisation-overview.component';
import {DataSharingSummaryOverviewComponent} from './data-sharing-summary/data-sharing-summary-overview/data-sharing-summary-overview.component';
import {OrganisationComponent} from './organisation/organisation/organisation.component';
import {OrganisationEditorComponent} from './organisation/organisation-editor/organisation-editor.component';
import {RegionComponent} from './region/region/region.component';
import {RegionEditorComponent} from './region/region-editor/region-editor.component';
import {DataFlowComponent} from './data-flow/data-flow/data-flow.component';
import {DataFlowEditorComponent} from './data-flow/data-flow-editor/data-flow-editor.component';
import {DataSharingAgreementComponent} from './data-sharing-agreement/data-sharing-agreement/data-sharing-agreement.component';
import {DataSharingAgreementEditorComponent} from './data-sharing-agreement/data-sharing-agreement-editor/data-sharing-agreement-editor.component';
import {DataProcessingAgreementComponent} from './data-processing-agreement/data-processing-agreement/data-processing-agreement.component';
import {DataProcessingAgreementEditorComponent} from './data-processing-agreement/data-processing-agreement-editor/data-processing-agreement-editor.component';
import {DataSharingSummaryComponent} from './data-sharing-summary/data-sharing-summary/data-sharing-summary.component';
import {DataSharingSummaryEditorComponent} from './data-sharing-summary/data-sharing-summary-editor/data-sharing-summary-editor.component';
import {CohortComponent} from './cohort/cohort/cohort.component';
import {CohortEditorComponent} from './cohort/cohort-editor/cohort-editor.component';
import {DataSetComponent} from './data-set/data-set/data-set.component';
import {DataSetEditorComponent} from './data-set/data-set-editor/data-set-editor.component';

@Injectable()
export class AppMenuService implements  AbstractMenuProvider  {
  static getRoutes(): Routes {
    return [
      { path: '', redirectTo : 'organisationOverview', pathMatch: 'full' }, // Default route
      { path: 'organisationOverview', component: OrganisationOverviewComponent},
      { path: 'sharingOverview', component: DataSharingSummaryOverviewComponent},
      { path: 'organisations', component: OrganisationComponent},
      { path: 'organisation/:id/:mode', component: OrganisationEditorComponent},
      { path: 'regions', component: RegionComponent},
      { path: 'region/:id/:mode', component: RegionEditorComponent},
      { path: 'dataFlows', component: DataFlowComponent},
      { path: 'dataFlow/:id/:mode', component: DataFlowEditorComponent},
      { path: 'dsas', component: DataSharingAgreementComponent},
      { path: 'dsa/:id/:mode', component: DataSharingAgreementEditorComponent},
      { path: 'dpas', component: DataProcessingAgreementComponent},
      { path: 'dpa/:id/:mode', component: DataProcessingAgreementEditorComponent},
      { path: 'dataSharingSummaries', component: DataSharingSummaryComponent},
      { path: 'dataSharingSummary/:id/:mode', component: DataSharingSummaryEditorComponent},
      { path: 'cohorts', component: CohortComponent},
      { path: 'cohort/:id/:mode', component: CohortEditorComponent},
      { path: 'dataSets', component: DataSetComponent},
      { path: 'dataSet/:id/:mode', component: DataSetEditorComponent},
    ];
  }

  getClientId(): string {
    return 'eds-dsa-manager';
  }
  getApplicationTitle(): string {
    return 'Data Sharing Manager';
  }
  getMenuOptions(): MenuOption[] {
    return [
      {caption: 'Organisation', state: 'organisationOverview', icon: 'fa fa-hospital-o', role: 'eds-dsa-manager:org-manager'},
      {caption: 'Sharing', state: 'sharingOverview', icon: 'fa fa-cogs', role: 'eds-dsa-manager:sharing-manager'}
    ];
  }
}
