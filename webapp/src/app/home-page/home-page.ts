import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { ApiService } from '../api-service';
import { BehaviorSubject } from 'rxjs';
import { LunchPicker } from '../types';
import { AsyncPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatExpansionModule } from '@angular/material/expansion';
import { DataService } from '../data-service';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { RemainingWaitTime } from './remaining-wait-time/remaining-wait-time';
import { LunchPickerDetatils } from '../lunch-picker-details/lunch-picker-details';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

const snackBarConfig: MatSnackBarConfig = {
  horizontalPosition: 'end',
  duration: 3000,
} as const;

@Component({
  selector: 'app-home-page',
  imports: [
    AsyncPipe,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MatTooltipModule,
    MatDividerModule,
    RemainingWaitTime,
    LunchPickerDetatils,
  ],
  templateUrl: './home-page.html',
  styleUrl: './home-page.scss',
})
export class HomePage implements OnInit, OnDestroy {
  private readonly currentPickerSubject = new BehaviorSubject<LunchPicker | null>(null);
  public readonly currentPicker$ = this.currentPickerSubject.asObservable();

  private readonly restartFlagSubject = new BehaviorSubject<boolean>(false);
  public readonly restartFlag$ = this.restartFlagSubject.asObservable();

  private autoRefreshInterval?: number;

  waitTimeOptions = [
    { value: 'PT10S', label: '10 Seconds' }, // to test
    { value: 'PT10M', label: '10 Minutes' },
    { value: 'PT30M', label: '30 Minutes' },
    { value: 'PT1H', label: '1 Hour' },
  ];

  startForm = {
    waitTime: this.waitTimeOptions[0].value,
  };

  optionForm = {
    shopName: undefined,
    shopUrl: undefined,
  };

  constructor(
    private readonly apiService: ApiService,
    private readonly dataService: DataService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.fetchLatestLunchPicker();
    this.autoRefreshInterval = setInterval(() => {
      this.fetchLatestLunchPicker();
    }, 30000);
  }

  ngOnDestroy(): void {
    clearInterval(this.autoRefreshInterval);
    this.snackBar.ngOnDestroy();
  }

  fetchLatestLunchPicker() {
    this.apiService.getLunchPickers({ size: 1 }).subscribe({
      next: (resp) => {
        if (resp.content.length == 0) return;
        this.setCurrentPicker(resp.content[0]);
        this.currentPickerSubject.next(resp.content[0]);
      },
    });
  }

  private setCurrentPicker(picker: LunchPicker) {
    this.currentPickerSubject.next(picker);
  }

  myOption(picker: LunchPicker) {
    const user = this.dataService.getUser();
    const myOption = picker?.lunchOptions?.find(
      (option) => option.submitter.username == user?.username
    );
    console.info('myOption:', myOption?.shopName);
    return myOption;
  }

  canPick(picker: LunchPicker) {
    const user = this.dataService.getUser();
    return user?.username == picker?.firstSubmittedUsername;
  }

  onStartPicker() {
    this.apiService.createLunchPicker(this.startForm).subscribe({
      next: (resp) => {
        this.setCurrentPicker(resp);
        this.setRestartFlag(false);
        console.info('created lunch picker', resp);
        this.snackBar.open('Started lunch picker!', 'Ok', snackBarConfig);
      },
      error: (err) => console.error(err),
    });
  }

  onSubmitOptioin() {
    const shopName = this.optionForm.shopName!;
    const shopUrl = this.optionForm.shopUrl;

    const lunchPickerId = this.currentPickerSubject.getValue()?.id;
    if (!lunchPickerId) {
      console.error('Something is wrong, missing current lunch picker id');
      return;
    }

    this.apiService.submitLunchOption({ lunchPickerId, shopName, shopUrl }).subscribe({
      next: (resp) => {
        this.setCurrentPicker(resp);
        console.info('submitted option', resp);
        this.snackBar.open('Submitted lunch option!', 'Ok', snackBarConfig);
      },
      error: (err) => console.error(err),
    });
  }

  onClickPick() {
    const lunchPickerId = this.currentPickerSubject.getValue()?.id;

    if (!lunchPickerId) {
      console.error('something is wrong, missing current lunch picker id');
      return;
    }

    this.apiService.pickLunchOption({ lunchPickerId }).subscribe({
      next: (resp) => {
        console.info('picked option', resp);
        this.setCurrentPicker(resp);
        this.snackBar.open('Picked random lunch option!', 'Ok', snackBarConfig);
      },
      error: (err) => console.error(err),
    });
  }

  onClickRestart() {
    this.setRestartFlag(true);
  }

  onClickCancelReStart() {
    this.setRestartFlag(false);
  }

  private setRestartFlag(flag: boolean) {
    this.restartFlagSubject.next(flag);
  }
}
