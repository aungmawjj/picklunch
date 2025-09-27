import { Component, Input, OnDestroy } from '@angular/core';
import { LunchPickerDetatils } from '../lunch-picker-details/lunch-picker-details';
import { LunchPicker } from '../types';
import { BehaviorSubject, Subscription } from 'rxjs';
import { ApiService } from '../api-service';
import { AsyncPipe, Location } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Loading } from '../loading/loading';
import { MatDialog } from '@angular/material/dialog';
import { AlertDialog } from '../alert-dialog/alert-dialog';

@Component({
  selector: 'pl-history-details-page',
  imports: [AsyncPipe, LunchPickerDetatils, MatButtonModule, MatIconModule, Loading],
  templateUrl: './history-details-page.html',
  styleUrl: './history-details-page.scss',
})
export class HistoryDetailsPage implements OnDestroy {
  private _lunchPickerId?: number;

  @Input({ required: true })
  set lunchPickerId(value: number) {
    if (value == undefined) return;
    if (value == this._lunchPickerId) return;
    this._lunchPickerId = value;
    this.fetchLunchPicker(value);
  }

  private readonly lunchPickerSubject = new BehaviorSubject<LunchPicker | null>(null);
  readonly lunchPicker$ = this.lunchPickerSubject.asObservable();

  private fetchLunchPickerSubscription?: Subscription;

  constructor(
    private readonly apiService: ApiService,
    private readonly location: Location,
    private readonly dialog: MatDialog
  ) {}

  ngOnDestroy(): void {
    this.fetchLunchPickerSubscription?.unsubscribe();
  }

  onClickBack() {
    this.location.back();
  }

  private fetchLunchPicker(id: number) {
    this.fetchLunchPickerSubscription = this.apiService.getLunchPickerById(id).subscribe({
      next: (resp) => {
        this.lunchPickerSubject.next(resp);
      },
      error: (err) => {
        console.error('Failed to fetch lunch picker', err);
        const { error, message } = err.error || {};
        this.dialog.open(AlertDialog, {
          data: { title: error || 'Failed to fetch', message },
        });
      },
    });
  }
}
