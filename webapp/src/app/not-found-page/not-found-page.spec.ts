import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotFoundPage } from './not-found-page';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

describe('NotFoundPage', () => {
  let fixture: ComponentFixture<NotFoundPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotFoundPage],
      providers: [provideZonelessChangeDetection(), provideRouter([])],
    }).compileComponents();
  });

  it('should create', () => {
    fixture = TestBed.createComponent(NotFoundPage);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
