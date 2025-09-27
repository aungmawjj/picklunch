import { Component, Input, OnInit } from '@angular/core';
import { ApiService } from '../api-service';
import { PagedLunchPickers } from '../types';
import { BehaviorSubject } from 'rxjs';
import { AsyncPipe, DatePipe } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { Router, RouterLink } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'pl-history-page',
  imports: [AsyncPipe, DatePipe, MatTableModule, MatPaginatorModule, RouterLink],
  templateUrl: './history-page.html',
  styleUrl: './history-page.scss',
})
export class HistoryPage implements OnInit {
  private _pageIndex = 0;
  private _pageSize = 10;

  @Input({ required: true })
  set pageIndex(value: number) {
    if (value == undefined) return;
    if (value == this.pageIndex) return;
    this._pageIndex = value;
    this.fetchLunchPickers();
  }

  @Input({ required: true })
  set pageSize(value: number) {
    if (value == undefined) return;
    if (value == this.pageSize) return;
    this._pageSize = value;
    this.fetchLunchPickers();
  }

  get pageIndex() {
    return this._pageIndex;
  }

  get pageSize() {
    return this._pageSize;
  }

  pageSizeOptions = [5, 10, 25, 50];

  private readonly pagedLunchPickersSubject = new BehaviorSubject<PagedLunchPickers | null>(null);
  readonly pagedLunchPickers$ = this.pagedLunchPickersSubject.asObservable();

  displayedColumns: string[] = ['startTime', 'pickedLunchOption'];

  constructor(
    private readonly apiService: ApiService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.fetchLunchPickers();
  }

  onPageEvent(pageEvent: PageEvent) {
    const { pageIndex, pageSize } = pageEvent;
    this.router.navigate([], {
      queryParams: { pageIndex, pageSize },
    });
  }

  private fetchLunchPickers() {
    this.apiService.getLunchPickers({ page: this.pageIndex, size: this.pageSize }).subscribe({
      next: (resp) => {
        this.setPagedLunchPickers(resp);
      },
      error: (err) => {
        console.error('Failed to fetch history', err);
        this.snackBar.open(err.error.message || 'Failed to fetch history', 'Dismiss');
      },
    });
  }

  private setPagedLunchPickers(value: PagedLunchPickers): void {
    this.pagedLunchPickersSubject.next(value);
  }
}
