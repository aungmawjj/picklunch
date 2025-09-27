import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertDialog } from './alert-dialog';
import { provideZonelessChangeDetection } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('AlertDialog', () => {
  let fixture: ComponentFixture<AlertDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlertDialog],
      providers: [provideZonelessChangeDetection(), { provide: MAT_DIALOG_DATA, useValue: {} }],
    }).compileComponents();
  });

  it('should create', () => {
    fixture = TestBed.createComponent(AlertDialog);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
