import { DatePipe } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { LunchPicker } from '../types';

@Component({
  selector: 'pl-lunch-picker-details',
  imports: [
    DatePipe,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MatTooltipModule,
    MatDividerModule,
  ],
  templateUrl: './lunch-picker-details.html',
  styleUrl: './lunch-picker-details.scss',
})
export class LunchPickerDetatils {
  @Input({ required: true })
  lunchPicker!: LunchPicker;

  hasOptions(): boolean {
    return !!this.lunchPicker.lunchOptions && this.lunchPicker.lunchOptions.length > 0;
  }
}
