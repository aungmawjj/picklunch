import { Routes } from '@angular/router';
import { HomePage } from './home-page/home-page';
import { NotFoundPage } from './not-found-page/not-found-page';

export const routes: Routes = [
  { path: 'home', component: HomePage },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', component: NotFoundPage },
];
