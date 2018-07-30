import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DelegationCreatorComponent } from './delegation-creator.component';

describe('DelegationCreatorComponent', () => {
  let component: DelegationCreatorComponent;
  let fixture: ComponentFixture<DelegationCreatorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DelegationCreatorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DelegationCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
