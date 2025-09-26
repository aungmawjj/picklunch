import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LunchPickerDetatils } from './lunch-picker-details';

describe('LunchPicker', () => {
  let component: LunchPickerDetatils;
  let fixture: ComponentFixture<LunchPickerDetatils>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LunchPickerDetatils]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LunchPickerDetatils);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
