import {Component, OnInit, ViewContainerRef} from '@angular/core';
import {DataSetService} from '../data-set.service';
import {ActivatedRoute, Router} from '@angular/router';
import {LoggerService} from 'eds-angular4';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ToastsManager} from 'ng2-toastr';

@Component({
  selector: 'app-data-set-editor',
  templateUrl: './data-set-editor.component.html',
  styleUrls: ['./data-set-editor.component.css']
})
export class DataSetEditorComponent implements OnInit {
  private paramSubscriber: any;

  constructor(private $modal: NgbModal,
              private log: LoggerService,
              private dataSetService: DataSetService,
              private router: Router,
              private route: ActivatedRoute,
              public toastr: ToastsManager, vcr: ViewContainerRef) {
    this.toastr.setRootViewContainerRef(vcr);
  }

  ngOnInit() {
    this.paramSubscriber = this.route.params.subscribe(
      params => {
        this.performAction(params['mode'], params['id']);
      });
  }

  protected performAction(action: string, itemUuid: string) {
    switch (action) {
      case 'add':
        // this.create(itemUuid);
        break;
      case 'edit':
        // this.load(itemUuid);
        break;
    }
  }

}
