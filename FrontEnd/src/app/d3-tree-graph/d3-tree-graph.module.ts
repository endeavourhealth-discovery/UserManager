import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {NvD3Module} from 'ng2-nvd3';
import {D3TreeGraphComponent} from "./d3-tree-graph/d3-tree-graph.component";

import 'd3';
import 'nvd3';

@NgModule({
  imports: [
    CommonModule,
    NvD3Module
  ],
  declarations: [D3TreeGraphComponent],
  entryComponents: [D3TreeGraphComponent],
  exports: [D3TreeGraphComponent]
})
export class D3TreeGraphModule { }
