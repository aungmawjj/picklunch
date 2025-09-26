import { HttpClient } from '@angular/common/http';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { ToolBar } from './tool-bar/tool-bar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToolBar],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class App {
  private readonly healthResponseSubject = new BehaviorSubject('');
  healthResponse = this.healthResponseSubject.asObservable();

  constructor(private readonly http: HttpClient) {
    this.http.get('/api/health').subscribe({
      next: (resp) => {
        this.healthResponseSubject.next(JSON.stringify(resp, null, 4));
      },
      error: (err) => {},
    });
  }

  onClickStart() {
    this.http.post('/api/lunch-picker-blabla', {}).subscribe({
      next: (resp) => {
        console.info('Start success', resp);
      },
      error: (err) => {
        console.error('Failed to start', err);
      },
    });
  }

  onClickLogout() {
    this.http.post('logout', null).subscribe({
      next: (resp) => {
        console.info('Logout success', resp);
      },
      error: (err) => {
        console.error('Failed to logout', err);
      },
    });
  }
}
