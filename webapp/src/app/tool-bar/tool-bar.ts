import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ApiService } from '../api-service';
import { AsyncPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { SharedDataService } from '../shared-data-service';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { MatDividerModule } from '@angular/material/divider';
import { Subscription } from 'rxjs';

@Component({
  selector: 'pl-tool-bar',
  imports: [
    AsyncPipe,
    MatToolbarModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    RouterLink,
    RouterLinkActive,
    MatDividerModule,
  ],
  templateUrl: './tool-bar.html',
  styleUrl: './tool-bar.scss',
})
export class ToolBar implements OnInit, OnDestroy {
  readonly user$;

  private fetchUserSubscription?: Subscription;

  constructor(private readonly apiService: ApiService, private readonly dataService: SharedDataService) {
    this.user$ = this.dataService.user$;
  }

  ngOnInit(): void {
    this.fetchUser();
  }

  ngOnDestroy(): void {
    this.fetchUserSubscription?.unsubscribe();
  }

  private fetchUser(): void {
    this.fetchUserSubscription = this.apiService.getCurrentUser().subscribe({
      next: (user) => this.dataService.setUser(user),
      error: (err) => console.error('Failed to fetch current user', err),
    });
  }

  onClickLogout(): void {
    this.apiService.logout().subscribe({
      next: () => window.location.replace('/login?logout'),
    });
  }
}
