import { TestBed } from '@angular/core/testing';

import { DelegationService } from './delegation.service';

describe('DelegationService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: DelegationService = TestBed.get(DelegationService);
    expect(service).toBeTruthy();
  });
});
