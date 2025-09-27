import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  CreateLunchPickerRequest,
  LunchPicker,
  PagedLunchPickers,
  PickLunchOptionRequest,
  SubmitLunchOptionRequest,
  User,
} from './types';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private readonly http: HttpClient) {
    // to retrieve csrf token for non-get calls on app start
    http.post('/api/create-csrf-token', null).subscribe({
      error: (err) => {
        console.info('Got error response as expected!', err);
      },
    });
  }

  public getCurrentUser(): Observable<User> {
    return this.http.get<User>('/api/user/me');
  }

  public logout(): Observable<void> {
    return this.http.post<void>('/logout', null);
  }

  public getLunchPickers(params?: { page?: number; size?: number }): Observable<PagedLunchPickers> {
    return this.http.get<PagedLunchPickers>('/api/lunch-picker', { params });
  }

  public getLunchPickerById(id: number): Observable<LunchPicker> {
    return this.http.get<LunchPicker>(`/api/lunch-picker/${id}`);
  }

  public createLunchPicker(request: CreateLunchPickerRequest): Observable<LunchPicker> {
    return this.http.post<LunchPicker>('/api/lunch-picker', request);
  }

  public submitLunchOption(request: SubmitLunchOptionRequest): Observable<LunchPicker> {
    return this.http.post<LunchPicker>('/api/lunch-picker/option', request);
  }

  public pickLunchOption(request: PickLunchOptionRequest): Observable<LunchPicker> {
    return this.http.post<LunchPicker>('/api/lunch-picker/pick', request);
  }
}
