import {Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation} from '@angular/core';
import OrgChart from 'orgchart.js';

@Component({
  selector: 'tree-graph',
  templateUrl: './tree-graph.component.html',
  styleUrls: ['./tree-graph.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class TreeGraphComponent implements OnInit {
  orgchart: OrgChart;
  @Input()
  set definition(definition: any) {
      this.create(definition);
  }

  @Output()
  nodeClick = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  create(definition: any) {
    document.querySelector('#chart-container').innerHTML = '';
    if (definition) {
      definition.chartContainer = '#chart-container';
      this.orgchart = new OrgChart(definition);

      document.querySelector('#chart-container').addEventListener('click', (event) => this.doChartClick(this, event));
    }
  }

  doChartClick(vm: TreeGraphComponent, event: any) {
    if (event.target.classList && event.target.classList.contains('node'))
      vm.doNodeClick(event.target)
    else {
      let closestNode = vm.closest(event.target, el => el.classList && el.classList.contains('node'));
      vm.doNodeClick(closestNode);
    }
  }

  doNodeClick(node: any) {
    this.nodeClick.emit(node);
  }

  getSelected() {
    let elements = document.getElementsByClassName('node focused');
    if (elements)
      return elements[0];
    return null;
  }

  addChild(node: any, data: any) {
    if (!node)
      node = this.getSelected();

    console.log(node);

    if (node.parentNode.colSpan > 1) {
      // Add as sibling
      let sibling = this.closest(node, el => el.nodeName === 'TABLE').querySelector('.nodes').querySelector('.node');
      this.orgchart.addSiblings(sibling, {siblings: [data]});
    } else {
      // Add as child
      this.orgchart.addChildren(node, {children: [data]});
    }
  }

  addChildren(node: any, data: any[]) {                                    // CHANGE HERE FOR PASSING ARRAY
    if (!node)
      node = this.getSelected();

    console.log(node);

    if (node.parentNode.colSpan > 1) {
      // Add as sibling
      let sibling = this.closest(node, el => el.nodeName === 'TABLE').querySelector('.nodes').querySelector('.node');
      this.orgchart.addSiblings(sibling, {siblings: data});              // CHANGE HERE AS data IS ALREADY ARRAY
    } else {
      // Add as child
      this.orgchart.addChildren(node, {children: data});              // CHANGE HERE AS data IS ALREADY ARRAY
    }
  }

  addParent(node: any, data: any) {
    if (!node)
      node = this.getSelected();

    this.orgchart.addParent(node, data );
  }

  closest(el, fn) {
    return el && ((fn(el) && el !== document.querySelector('.orgchart')) ? el : this.closest(el.parentNode, fn));
  }
}
