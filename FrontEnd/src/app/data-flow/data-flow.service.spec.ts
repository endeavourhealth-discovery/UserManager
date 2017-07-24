import { TestBed, inject } from '@angular/core/testing';

import { DataFlowService } from './data-flow.service';

describe('DataFlowService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DataFlowService]
    });
  });

  it('should be created', inject([DataFlowService], (service: DataFlowService) => {
    expect(service).toBeTruthy();
  }));
});
