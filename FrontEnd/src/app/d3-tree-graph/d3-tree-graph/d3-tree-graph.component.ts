import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit, Output,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';

@Component({
  selector: 'd3-tree-graph',
  templateUrl: './d3-tree-graph.component.html',
  styleUrls: ['./d3-tree-graph.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class D3TreeGraphComponent implements AfterViewInit {
  static globalId: number = 1;
  instanceId: string;
  selectedNode: any;

  @Input()
  set definition(definition: any) {
    console.log(definition);
    this.root = definition;
    // this.draw();
  }

  @Output()
  nodeClick = new EventEmitter();

  @ViewChild('treeGraphSvg')
  graphSvg: ElementRef;
  tree: any;
  root: any;
  diagonal: any;
  vis: any;
  i: number;

  constructor() { }

  ngAfterViewInit() {
    this.instanceId = 'svg_' + D3TreeGraphComponent.globalId++;
    this.graphSvg.nativeElement.id = this.instanceId;
    console.log(this.instanceId);
  }

  setData(data: any) {
    this.root = data;
  }

  draw() {
    console.log(this.root);
    if (this.root == null) {
      return;
    }
    d3.selectAll("svg > *").remove();

    var m = [20, 120, 20, 120],
      w = 1280 - m[1] - m[3],
      h = 800 - m[0] - m[2];
    this.i = 0;

    this.tree = d3.layout.tree()
      .size([h, w]);

    this.diagonal = d3.svg.diagonal()
      .projection((d) => {
        return [d.y, d.x];
      });

    this.vis = d3.select("#"+this.instanceId)
      .attr("width", w + m[1] + m[3])
      .attr("height", h + m[0] + m[2])
      .append("svg:g")
      .attr("transform", "translate(" + m[3] + "," + m[0] + ")");


      console.log(this.root);
      this.root.x0 = h / 2;
      this.root.y0 = 0;

      // Initialize the display to show a few nodes.
      this.root.children.forEach((c) => this.toggleAll(this, c));
      /*this.toggle(this.root.children[1]);
      this.toggle(this.root.children[1].children[2]);
      this.toggle(this.root.children[9]);
      this.toggle(this.root.children[9].children[0]);*/

      this.update(this.root);
  }

  toggleAll(vm: D3TreeGraphComponent, d: any) {
    if (d.children) {
      d.children.forEach(this.toggleAll);
      this.toggle(d);
    }
  }

  update(source) {
    this.nodeClick.emit(source);
    this.selectedNode = source;

    console.log('update');
      var duration = 500;

      // Compute the new tree layout.
      var nodes = this.tree.nodes(this.root).reverse();

      // Normalize for fixed-depth.
      nodes.forEach((d) => { d.y = d.depth * 180; });

      // Update the nodes…
      var node = this.vis.selectAll("g.node")
        .data(nodes, (d) => { return d.id || (d.id = ++this.i); });

      // Enter any new nodes at the parent's previous position.
      var nodeEnter = node.enter().append("svg:g")
        .attr("class", "node")
        .attr("transform", (d) => { return "translate(" + source.y0 + "," + source.x0 + ")"; })
        .on("click", (d) => { this.toggle(d); this.update(d); });

      nodeEnter.append("svg:circle")
        .attr("r", 1e-6)
        .style("fill", (d) => { return d._children ? "lightsteelblue" : "#fff"; });

      nodeEnter.append("svg:text")
        .attr("x", (d) => { return d.children || d._children ? -10 : 10; })
        .attr("dy", ".35em")
        .attr("text-anchor", (d) => { return d.children || d._children ? "end" : "start"; })
        .text((d) => { return d.name; })
        .style("fill-opacity", 1e-6);

      // Transition nodes to their new position.
      var nodeUpdate = node.transition()
        .duration(duration)
        .attr("transform", (d) => { return "translate(" + d.y + "," + d.x + ")"; });

      nodeUpdate.select("circle")
        .attr("r", 4.5)
        .style("fill", (d) => { return d._children ? "lightsteelblue" : "#fff"; });

      nodeUpdate.select("text")
        .style("fill-opacity", 1);

      console.log(nodeUpdate.select("circle"));
      nodeUpdate.select("text")
        .style("color", (d) => { return d.selected ? "#11a" : "#332"});

      // Transition exiting nodes to the parent's new position.
      var nodeExit = node.exit().transition()
        .duration(duration)
        .attr("transform", (d) => { return "translate(" + source.y + "," + source.x + ")"; })
        .remove();

      nodeExit.select("circle")
        .attr("r", 1e-6);

      nodeExit.select("text")
        .style("fill-opacity", 1e-6);

      // Update the links…
      var link = this.vis.selectAll("path.link")
        .data(this.tree.links(nodes), (d) => { return d.target.id; });

      // Enter any new links at the parent's previous position.
      link.enter().insert("svg:path", "g")
        .attr("class", "link")
        .attr("d", (d) => {
          var o = {x: source.x0, y: source.y0};
          return this.diagonal({source: o, target: o});
        })
        .transition()
        .duration(duration)
        .attr("d", this.diagonal);

      // Transition links to their new position.
      link.transition()
        .duration(duration)
        .attr("d", this.diagonal);

      // Transition exiting nodes to the parent's new position.
      link.exit().transition()
        .duration(duration)
        .attr("d", (d) => {
          var o = {x: source.x, y: source.y};
          return this.diagonal({source: o, target: o});
        })
        .remove();

      // Stash the old positions for transition.
      nodes.forEach((d) => {
        d.x0 = d.x;
        d.y0 = d.y;
      });
    }

  addRandomChild() {
    console.log(this.root);
    this.root.children[1].children.push( {"name": "SOME ORG", "balls": 283});
    this.update(this.root);
  }

  // Toggle children.
  toggle(d) {
    console.log(d);
    if (d.selected) {
      d.selected = !d.selected;
    } else {
      d.selected = true;
    }
    if (d.children) {
      d._children = d.children;
      d.children = null;
    } else {
      d.children = d._children;
      d._children = null;
    }
  }

}
