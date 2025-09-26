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

@Injectable({
  providedIn: 'root',
})
export class ApiService {
  constructor(private readonly http: HttpClient) {
    // to retrieve csrf token for non-get calls on app start
    http.post('/api/create-csrf-token', null).subscribe();
  }

  public getCurrentUser() {
    return this.http.get<User>('/api/user/me');
  }

  public logout() {
    return this.http.post('/logout', null);
  }

  public getLunchPickers(params?: { page?: number; size?: number }) {
    return this.http.get<PagedLunchPickers>('/api/lunch-picker', { params });
  }

  public getLunchPickerById(id: number) {
    return this.http.get<LunchPicker>(`/api/lunch-picker/${id}`);
  }

  public createLunchPicker(request: CreateLunchPickerRequest) {
    return this.http.post<LunchPicker>('/api/lunch-picker', request);
  }

  public submitLunchOption(request: SubmitLunchOptionRequest) {
    return this.http.post<LunchPicker>('/api/lunch-picker/option', request);
  }

  public pickLunchOption(request: PickLunchOptionRequest) {
    return this.http.post<LunchPicker>('/api/lunch-picker/pick', request);
  }
}
