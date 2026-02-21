import { Component, inject } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { ApiService } from '../../core/api.service';
import { map, tap } from 'rxjs';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [
    NgIf,
    AsyncPipe,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
  ],
  template: `
    <mat-card>
      <div style="font-weight: 600; margin-bottom: 12px;">Settings</div>

      <form class="form" [formGroup]="form" (ngSubmit)="save()">
        <mat-form-field appearance="outline">
          <mat-label>Base currency</mat-label>
          <input matInput formControlName="baseCurrency" placeholder="LKR" />
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Period start day</mat-label>
          <input matInput type="number" formControlName="periodStartDay" min="1" max="28" />
          <mat-hint>Salary cycle start day (e.g., 25). Allowed: 1–28</mat-hint>
        </mat-form-field>

        <button mat-raised-button color="primary" type="submit" [disabled]="form.invalid">Save</button>
      </form>

      <div *ngIf="saved" style="margin-top: 12px; opacity: 0.8;">Saved.</div>
    </mat-card>
  `,
  styles: [
    `
      .form { display: grid; gap: 12px; max-width: 420px; }
    `,
  ],
})
export class SettingsComponent {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);

  saved = false;

  form = this.fb.group({
    baseCurrency: ['LKR', [Validators.required, Validators.minLength(3), Validators.maxLength(3)]],
    periodStartDay: [25, [Validators.required, Validators.min(1), Validators.max(28)]],
  });

  vm$ = this.api.me().pipe(
    tap((r) => {
      this.form.patchValue({
        baseCurrency: r.data.baseCurrency,
        periodStartDay: r.data.periodStartDay,
      });
    }),
    map((r) => r.data)
  );

  constructor() {
    // trigger load
    this.vm$.subscribe();
  }

  save() {
    this.saved = false;
    const v = this.form.getRawValue();
    this.api
      .patchSettings({ baseCurrency: v.baseCurrency!, periodStartDay: Number(v.periodStartDay) })
      .subscribe(() => (this.saved = true));
  }
}
