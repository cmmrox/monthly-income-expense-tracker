import { Component, inject } from '@angular/core';
import { AsyncPipe, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../core/api.service';
import { map, tap } from 'rxjs';

import { NbCardModule, NbInputModule, NbButtonModule } from '@nebular/theme';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [NgIf, AsyncPipe, ReactiveFormsModule, NbCardModule, NbInputModule, NbButtonModule],
  template: `
    <nb-card>
      <nb-card-header>Settings</nb-card-header>
      <nb-card-body>
        <form class="form" [formGroup]="form" (ngSubmit)="save()">
          <label>
            Base currency
            <input nbInput fullWidth formControlName="baseCurrency" placeholder="LKR" />
          </label>

          <label>
            Period start day
            <input nbInput fullWidth type="number" min="1" max="28" formControlName="periodStartDay" />
            <small class="hint">Salary cycle start day (1–28). Example: 25</small>
          </label>

          <button nbButton status="primary" [disabled]="form.invalid">Save</button>

          <div *ngIf="saved" class="saved">Saved.</div>
        </form>
      </nb-card-body>
    </nb-card>
  `,
  styles: [
    `
      .form {
        display: grid;
        gap: 14px;
        max-width: 420px;
      }
      label {
        display: grid;
        gap: 6px;
      }
      .hint {
        opacity: 0.7;
      }
      .saved {
        margin-top: 6px;
        opacity: 0.85;
      }
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
    map((r) => r.data),
  );

  constructor() {
    this.vm$.subscribe();
  }

  save() {
    this.saved = false;
    const v = this.form.getRawValue();
    this.api.patchSettings({ baseCurrency: v.baseCurrency!, periodStartDay: Number(v.periodStartDay) }).subscribe(() => {
      this.saved = true;
    });
  }
}
