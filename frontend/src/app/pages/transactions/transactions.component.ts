import { Component, inject, OnInit } from '@angular/core';
import { AsyncPipe, CurrencyPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { ApiService } from '../../core/api.service';
import { AccountResponse, CategoryResponse } from '../../core/api.models';
import { BehaviorSubject, combineLatest, map, switchMap } from 'rxjs';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [
    NgIf,
    NgFor,
    AsyncPipe,
    DatePipe,
    CurrencyPipe,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatTabsModule,
    MatTableModule,
  ],
  template: `
    <mat-card>
      <mat-tab-group>
        <mat-tab label="Expense">
          <form class="form" [formGroup]="expenseForm" (ngSubmit)="submitExpense()">
            <div class="row">
              <mat-form-field appearance="outline">
                <mat-label>Date (ISO)</mat-label>
                <input matInput formControlName="txnDate" placeholder="2026-02-25T10:00:00Z" />
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Amount</mat-label>
                <input matInput type="number" formControlName="amount" />
              </mat-form-field>
            </div>

            <div class="row">
              <mat-form-field appearance="outline">
                <mat-label>Account</mat-label>
                <mat-select formControlName="accountId">
                  <mat-option *ngFor="let a of accounts$ | async" [value]="a.id">{{ a.name }}</mat-option>
                </mat-select>
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Category</mat-label>
                <mat-select formControlName="categoryId">
                  <mat-option *ngFor="let c of expenseCategories$ | async" [value]="c.id">{{ c.name }}</mat-option>
                </mat-select>
              </mat-form-field>
            </div>

            <mat-form-field appearance="outline" class="full">
              <mat-label>Description</mat-label>
              <input matInput formControlName="description" />
            </mat-form-field>

            <button mat-raised-button color="primary" type="submit" [disabled]="expenseForm.invalid">Save Expense</button>
          </form>
        </mat-tab>

        <mat-tab label="Income">
          <form class="form" [formGroup]="incomeForm" (ngSubmit)="submitIncome()">
            <div class="row">
              <mat-form-field appearance="outline">
                <mat-label>Date (ISO)</mat-label>
                <input matInput formControlName="txnDate" placeholder="2026-02-25T09:00:00Z" />
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Amount</mat-label>
                <input matInput type="number" formControlName="amount" />
              </mat-form-field>
            </div>

            <div class="row">
              <mat-form-field appearance="outline">
                <mat-label>Account</mat-label>
                <mat-select formControlName="accountId">
                  <mat-option *ngFor="let a of accounts$ | async" [value]="a.id">{{ a.name }}</mat-option>
                </mat-select>
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Category</mat-label>
                <mat-select formControlName="categoryId">
                  <mat-option *ngFor="let c of incomeCategories$ | async" [value]="c.id">{{ c.name }}</mat-option>
                </mat-select>
              </mat-form-field>
            </div>

            <mat-form-field appearance="outline" class="full">
              <mat-label>Description</mat-label>
              <input matInput formControlName="description" />
            </mat-form-field>

            <button mat-raised-button color="primary" type="submit" [disabled]="incomeForm.invalid">Save Income</button>
          </form>
        </mat-tab>

        <mat-tab label="Transfer">
          <form class="form" [formGroup]="transferForm" (ngSubmit)="submitTransfer()">
            <div class="row">
              <mat-form-field appearance="outline">
                <mat-label>Date (ISO)</mat-label>
                <input matInput formControlName="txnDate" placeholder="2026-02-25T10:00:00Z" />
              </mat-form-field>
              <mat-form-field appearance="outline">
                <mat-label>Amount</mat-label>
                <input matInput type="number" formControlName="amount" />
              </mat-form-field>
            </div>

            <div class="row">
              <mat-form-field appearance="outline">
                <mat-label>From</mat-label>
                <mat-select formControlName="fromAccountId">
                  <mat-option *ngFor="let a of accounts$ | async" [value]="a.id">{{ a.name }}</mat-option>
                </mat-select>
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>To</mat-label>
                <mat-select formControlName="toAccountId">
                  <mat-option *ngFor="let a of accounts$ | async" [value]="a.id">{{ a.name }}</mat-option>
                </mat-select>
              </mat-form-field>
            </div>

            <mat-form-field appearance="outline" class="full">
              <mat-label>Description</mat-label>
              <input matInput formControlName="description" />
            </mat-form-field>

            <button mat-raised-button color="primary" type="submit" [disabled]="transferForm.invalid">Save Transfer</button>
          </form>
        </mat-tab>
      </mat-tab-group>
    </mat-card>

    <mat-card style="margin-top: 12px;">
      <div class="row" style="align-items: center;">
        <div style="font-weight: 600;">Transactions (current period)</div>
        <span style="flex: 1;"></span>
        <button mat-stroked-button (click)="reload()">Reload</button>
      </div>

      <table mat-table [dataSource]="txns$ | async" class="table">
        <ng-container matColumnDef="date">
          <th mat-header-cell *matHeaderCellDef>Date</th>
          <td mat-cell *matCellDef="let t">{{ t.txnDate | date: 'MMM d, HH:mm' }}</td>
        </ng-container>
        <ng-container matColumnDef="type">
          <th mat-header-cell *matHeaderCellDef>Type</th>
          <td mat-cell *matCellDef="let t">{{ t.type }}</td>
        </ng-container>
        <ng-container matColumnDef="amount">
          <th mat-header-cell *matHeaderCellDef>Amount</th>
          <td mat-cell *matCellDef="let t">{{ t.amount | currency: 'LKR' }}</td>
        </ng-container>
        <ng-container matColumnDef="account">
          <th mat-header-cell *matHeaderCellDef>Account</th>
          <td mat-cell *matCellDef="let t">{{ t.account?.name || t.fromAccount?.name + ' → ' + t.toAccount?.name }}</td>
        </ng-container>
        <ng-container matColumnDef="category">
          <th mat-header-cell *matHeaderCellDef>Category</th>
          <td mat-cell *matCellDef="let t">{{ t.category?.name || '-' }}</td>
        </ng-container>
        <ng-container matColumnDef="desc">
          <th mat-header-cell *matHeaderCellDef>Description</th>
          <td mat-cell *matCellDef="let t">{{ t.description || '-' }}</td>
        </ng-container>

        <tr mat-header-row *matHeaderRowDef="cols"></tr>
        <tr mat-row *matRowDef="let row; columns: cols"></tr>
      </table>
    </mat-card>
  `,
  styles: [
    `
      .form { padding: 16px; display: grid; gap: 12px; }
      .row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
      .full { width: 100%; }
      .table { width: 100%; margin-top: 12px; }
      @media (max-width: 900px) {
        .row { grid-template-columns: 1fr; }
      }
    `,
  ],
})
export class TransactionsComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);

  private reload$ = new BehaviorSubject<void>(undefined);

  accounts$ = this.api.listAccounts().pipe(map((r) => r.data));
  expenseCategories$ = this.api.listCategories('EXPENSE').pipe(map((r) => r.data));
  incomeCategories$ = this.api.listCategories('INCOME').pipe(map((r) => r.data));

  cols = ['date', 'type', 'amount', 'account', 'category', 'desc'];

  expenseForm = this.fb.group({
    txnDate: ['' as string, [Validators.required]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    accountId: ['' as string, [Validators.required]],
    categoryId: ['' as string, [Validators.required]],
    description: ['' as string],
  });

  incomeForm = this.fb.group({
    txnDate: ['' as string, [Validators.required]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    accountId: ['' as string, [Validators.required]],
    categoryId: ['' as string, [Validators.required]],
    description: ['' as string],
  });

  transferForm = this.fb.group({
    txnDate: ['' as string, [Validators.required]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    fromAccountId: ['' as string, [Validators.required]],
    toAccountId: ['' as string, [Validators.required]],
    description: ['' as string],
  });

  txns$ = this.reload$.pipe(
    switchMap(() => this.api.currentPeriod()),
    switchMap((p) => {
      const from = `${p.data.periodStart}T00:00:00Z`;
      const to = `${p.data.periodEnd}T23:59:59Z`;
      return this.api.listTransactions({ from, to, page: 0, size: 50 });
    }),
    map((r) => r.data.items)
  );

  ngOnInit(): void {
    const nowIso = new Date().toISOString();
    this.expenseForm.patchValue({ txnDate: nowIso });
    this.incomeForm.patchValue({ txnDate: nowIso });
    this.transferForm.patchValue({ txnDate: nowIso });
  }

  reload() {
    this.reload$.next();
  }

  submitExpense() {
    const v = this.expenseForm.getRawValue();
    this.api
      .createExpenseOrIncome({
        txnDate: v.txnDate!,
        type: 'EXPENSE',
        amount: Number(v.amount),
        accountId: v.accountId!,
        categoryId: v.categoryId!,
        description: v.description ?? undefined,
      })
      .subscribe(() => this.reload());
  }

  submitIncome() {
    const v = this.incomeForm.getRawValue();
    this.api
      .createExpenseOrIncome({
        txnDate: v.txnDate!,
        type: 'INCOME',
        amount: Number(v.amount),
        accountId: v.accountId!,
        categoryId: v.categoryId!,
        description: v.description ?? undefined,
      })
      .subscribe(() => this.reload());
  }

  submitTransfer() {
    const v = this.transferForm.getRawValue();
    this.api
      .createTransfer({
        txnDate: v.txnDate!,
        amount: Number(v.amount),
        fromAccountId: v.fromAccountId!,
        toAccountId: v.toAccountId!,
        description: v.description ?? undefined,
      })
      .subscribe(() => this.reload());
  }
}
