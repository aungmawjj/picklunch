import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RemainingWaitTime } from './remaining-wait-time';

describe('RemainingWaitTime', () => {
  let component: RemainingWaitTime;
  let fixture: ComponentFixture<RemainingWaitTime>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RemainingWaitTime]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RemainingWaitTime);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
