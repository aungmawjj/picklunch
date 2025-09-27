import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolBar } from './tool-bar';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

describe('ToolBar', () => {
  let component: ToolBar;
  let fixture: ComponentFixture<ToolBar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToolBar],
      providers: [provideZonelessChangeDetection(), provideHttpClient(), provideRouter([])],
    }).compileComponents();
  });

  it('should create', () => {
    fixture = TestBed.createComponent(ToolBar);
    component = fixture.componentInstance;
    expect(component).toBeTruthy();
  });
});
