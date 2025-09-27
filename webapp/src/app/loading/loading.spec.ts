import { TestBed } from '@angular/core/testing';
import { Loading } from './loading';
import { provideZonelessChangeDetection } from '@angular/core';

describe('Loading', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Loading],
      providers: [provideZonelessChangeDetection()],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(Loading);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
