import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TreeGraphComponent } from './tree-graph/tree-graph.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [TreeGraphComponent],
  exports: [TreeGraphComponent]
})
export class TreeGraphModule { }
