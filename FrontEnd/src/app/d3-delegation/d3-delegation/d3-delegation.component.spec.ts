import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { D3DelegationComponent } from './d3-delegation.component';

describe('D3DelegationComponent', () => {
  let component: D3DelegationComponent;
  let fixture: ComponentFixture<D3DelegationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ D3DelegationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(D3DelegationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
