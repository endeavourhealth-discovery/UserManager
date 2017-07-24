import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PurposeAddComponent } from './purpose-add.component';

describe('PurposeAddComponent', () => {
  let component: PurposeAddComponent;
  let fixture: ComponentFixture<PurposeAddComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PurposeAddComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PurposeAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
