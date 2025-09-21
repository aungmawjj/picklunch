import { AsyncPipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, AsyncPipe],
  templateUrl: './app.html',
  styleUrl: './app.scss',
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
}
