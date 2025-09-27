import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RemainingWaitTime } from './remaining-wait-time';
import { provideZonelessChangeDetection } from '@angular/core';

describe('RemainingWaitTime', () => {
  let fixture: ComponentFixture<RemainingWaitTime>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RemainingWaitTime],
      providers: [provideZonelessChangeDetection()],
    }).compileComponents();
  });

  it('should create', () => {
    fixture = TestBed.createComponent(RemainingWaitTime);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
