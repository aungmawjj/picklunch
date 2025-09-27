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
import { SharedDataService } from '../shared-data-service';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { RemainingWaitTime } from './remaining-wait-time/remaining-wait-time';
import { LunchPickerDetatils } from '../lunch-picker-details/lunch-picker-details';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { Loading } from '../loading/loading';

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
    Loading,
  ],
  templateUrl: './home-page.html',
  styleUrl: './home-page.scss',
})
export class HomePage implements OnInit, OnDestroy {
  private readonly lunchPickerSubject = new BehaviorSubject<LunchPicker | null>(null);
  readonly lunchPicker$ = this.lunchPickerSubject.asObservable();

  private readonly restartFlagSubject = new BehaviorSubject<boolean>(false);
  readonly restartFlag$ = this.restartFlagSubject.asObservable();

  private readonly loadingFlagSubject = new BehaviorSubject<boolean>(true);
  readonly loadingFlag$ = this.loadingFlagSubject.asObservable();

  private autoRefreshInterval?: number;

  waitTimeOptions = [
    { value: 'PT10M', label: '10 Minutes' },
    { value: 'PT30M', label: '30 Minutes' },
    { value: 'PT1H', label: '1 Hour' },
    { value: 'PT10S', label: '10 Seconds [ TEST ]' },
  ];

  startForm = {
    waitTime: this.waitTimeOptions[0].value,
  };

  optionForm: { shopName?: string; shopUrl?: string } = {};

  constructor(
    private readonly apiService: ApiService,
    private readonly dataService: SharedDataService,
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

  fetchLatestLunchPicker(): void {
    this.setLoadingFlag(true);
    this.apiService.getLunchPickers({ size: 1 }).subscribe({
      next: (resp) => {
        this.setLoadingFlag(false);
        if (resp.content.length > 0) {
          this.setCurrentPicker(resp.content[0]);
        }
      },
      error: (err) => {
        this.setLoadingFlag(false);
      },
    });
  }

  showStartForm(
    loadingFlag: boolean | null,
    restartFlag: boolean | null,
    lunchPicker: LunchPicker | null
  ): boolean {
    return !loadingFlag && (restartFlag || !lunchPicker);
  }

  showSubmitOptionForm(lunchPicker: LunchPicker): boolean {
    if (lunchPicker.state == 'PICKED') {
      return false;
    }
    const user = this.dataService.getUser();
    const myOption = lunchPicker?.lunchOptions?.find(
      (option) => option.submitter.username == user?.username
    );
    return !myOption;
  }

  canPick(picker: LunchPicker): boolean {
    const user = this.dataService.getUser();
    return user?.username == picker?.firstSubmittedUsername;
  }

  onStartPicker(): void {
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

  onSubmitOption(): void {
    const shopName = this.optionForm.shopName!;
    const shopUrl = this.optionForm.shopUrl;

    const lunchPickerId = this.lunchPickerSubject.getValue()?.id;
    if (!lunchPickerId) {
      console.error('Something is wrong, missing current lunch picker id');
      return;
    }

    this.apiService.submitLunchOption({ lunchPickerId, shopName, shopUrl }).subscribe({
      next: (resp) => {
        this.setCurrentPicker(resp);
        this.optionForm = {};
        console.info('submitted option', resp);
        this.snackBar.open('Submitted lunch option!', 'Ok', snackBarConfig);
      },
      error: (err) => console.error(err),
    });
  }

  onClickPick(): void {
    const lunchPickerId = this.lunchPickerSubject.getValue()?.id;

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

  onClickRestart(): void {
    this.setRestartFlag(true);
  }

  onClickCancelReStart(): void {
    this.setRestartFlag(false);
  }

  private setCurrentPicker(picker: LunchPicker): void {
    this.lunchPickerSubject.next(picker);
  }

  private setRestartFlag(flag: boolean): void {
    this.restartFlagSubject.next(flag);
  }

  private setLoadingFlag(flag: boolean): void {
    this.loadingFlagSubject.next(flag);
  }
}
