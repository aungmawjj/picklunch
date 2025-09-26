import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogTitle,
  MatDialogClose,
} from '@angular/material/dialog';

export interface AlertDialogData {
  title: string;
  message: string;
}

@Component({
  selector: 'pl-alert-dialog',
  imports: [MatButtonModule, MatDialogTitle, MatDialogContent, MatDialogActions, MatDialogClose],
  templateUrl: './alert-dialog.html',
  styleUrl: './alert-dialog.scss',
})
export class AlertDialog {
  readonly data = inject<AlertDialogData>(MAT_DIALOG_DATA);
}
