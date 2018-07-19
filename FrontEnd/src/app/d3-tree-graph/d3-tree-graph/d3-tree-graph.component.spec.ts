import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { D3TreeGraphComponent } from './d3-tree-graph.component';

describe('D3TreeGraphComponent', () => {
  let component: D3TreeGraphComponent;
  let fixture: ComponentFixture<D3TreeGraphComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ D3TreeGraphComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(D3TreeGraphComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
