import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { User } from './types';

@Injectable({
  providedIn: 'root',
})
export class SharedDataService {
  private readonly userSubject = new BehaviorSubject<User | null>(null);

  public readonly user$ = this.userSubject.asObservable();

  public setUser(user: User): void {
    return this.userSubject.next(user);
  }

  public getUser(): User | null {
    return this.userSubject.getValue();
  }
}
