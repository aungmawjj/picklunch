import { TestBed } from '@angular/core/testing';
import { HistoryPage } from './history-page';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

describe('HistoryPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HistoryPage],
      providers: [provideZonelessChangeDetection(), provideHttpClient(), provideRouter([])],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(HistoryPage);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
