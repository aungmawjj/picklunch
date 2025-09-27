import { TestBed } from '@angular/core/testing';
import { HistoryDetailsPage } from './history-details-page';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

describe('HistoryDetailsPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryDetailsPage],
      providers: [provideZonelessChangeDetection(), provideHttpClient(), provideRouter([])],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(HistoryDetailsPage);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
