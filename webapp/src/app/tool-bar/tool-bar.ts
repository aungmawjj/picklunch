import { Component, OnInit } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ApiService } from '../api-service';
import { AsyncPipe } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { DataService } from '../data-service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'pl-tool-bar',
  imports: [AsyncPipe, MatToolbarModule, MatIconModule, MatButtonModule, MatMenuModule, RouterLink],
  templateUrl: './tool-bar.html',
  styleUrl: './tool-bar.scss',
})
export class ToolBar implements OnInit {
  public readonly user$;

  constructor(private readonly apiService: ApiService, private readonly dataService: DataService) {
    this.user$ = this.dataService.user$;
  }

  ngOnInit(): void {
    this.fetchUser();
  }

  private fetchUser() {
    this.apiService.getCurrentUser().subscribe({
      next: (user) => this.dataService.setUser(user),
      error: (err) => console.error('get current user failed', err),
    });
  }

  onClickLogout() {
    this.apiService.logout().subscribe({
      next: () => window.location.replace('/'),
    });
  }
}
