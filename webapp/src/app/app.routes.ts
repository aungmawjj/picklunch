import { Routes } from '@angular/router';
import { HomePage } from './home-page/home-page';
import { NotFoundPage } from './not-found-page/not-found-page';
import { HistoryPage } from './history-page/history-page';
import { HistoryDetailsPage } from './history-details-page/history-details-page';

export const routes: Routes = [
  { path: 'home', component: HomePage },
  { path: 'history', component: HistoryPage },
  { path: 'history/:lunchPickerId', component: HistoryDetailsPage },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', component: NotFoundPage },
];
