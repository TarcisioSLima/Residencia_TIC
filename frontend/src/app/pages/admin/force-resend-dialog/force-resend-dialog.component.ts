import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';

export interface ForceResendDialogResult {
  userIds: string[];
  bypassPreferences: boolean;
}

@Component({
  selector: 'app-force-resend-dialog',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatCheckboxModule,
  ],
  templateUrl: './force-resend-dialog.component.html',
  styleUrls: ['./force-resend-dialog.component.scss'],
})
export class ForceResendDialogComponent {
  userIdsRaw = '';
  bypassPreferences = false;

  constructor(private dialogRef: MatDialogRef<ForceResendDialogComponent>) {}

  get parsedIds(): string[] {
    return this.userIdsRaw
      .split(/[\n,;]+/)
      .map(s => s.trim())
      .filter(s => s.length > 0);
  }

  confirm() {
    this.dialogRef.close({ userIds: this.parsedIds, bypassPreferences: this.bypassPreferences } as ForceResendDialogResult);
  }

  cancel() {
    this.dialogRef.close(null);
  }
}
