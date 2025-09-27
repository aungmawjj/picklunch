import { AsyncPipe } from '@angular/common';
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { LunchPicker } from '../../types';

@Component({
  selector: 'pl-remaining-wait-time',
  imports: [AsyncPipe],
  templateUrl: './remaining-wait-time.html',
  styleUrl: './remaining-wait-time.scss',
})
export class RemainingWaitTime implements OnInit, OnDestroy {
  @Input({ required: true })
  lunchPicker!: LunchPicker;

  @Output()
  waitTimeOver = new EventEmitter<void>();

  private readonly remainingWaitTimeSubject = new BehaviorSubject<string>('');
  readonly remainingWaitTime$ = this.remainingWaitTimeSubject.asObservable();

  private resolverInterval?: number;

  private waitTimeOverEmitted: { [lunchPickerId: number]: boolean } = {};

  ngOnInit(): void {
    this.setRemainingWaitTime();
    this.resolverInterval = setInterval(() => {
      this.setRemainingWaitTime();
    }, 1000);
  }

  ngOnDestroy(): void {
    clearInterval(this.resolverInterval);
  }

  private setRemainingWaitTime(): void {
    this.remainingWaitTimeSubject.next(this.getRemainingWaitTime());
  }

  private getRemainingWaitTime(): string {
    const waitTimeEnd = new Date(this.lunchPicker.waitTimeEnd).getTime() / 1000;
    const now = new Date().getTime() / 1000;
    if (now >= waitTimeEnd) {
      if (!this.waitTimeOverEmitted[this.lunchPicker.id]) {
        this.waitTimeOver.emit();
        this.waitTimeOverEmitted[this.lunchPicker.id] = true;
      }
      return '00 : 00';
    }
    const remaining = waitTimeEnd - now;
    const seconds = Math.floor(remaining % 60);
    const minutes = Math.floor(remaining / 60);
    return minutes.toString().padStart(2, '0') + ' : ' + seconds.toString().padStart(2, '0');
  }
}
