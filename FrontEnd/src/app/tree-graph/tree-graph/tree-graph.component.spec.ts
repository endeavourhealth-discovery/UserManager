import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreeGraphComponent } from './tree-graph.component';

describe('TreeGraphComponent', () => {
  let component: TreeGraphComponent;
  let fixture: ComponentFixture<TreeGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TreeGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TreeGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
