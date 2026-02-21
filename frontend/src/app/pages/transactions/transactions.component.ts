import { Component, inject } from '@angular/core';
import { AsyncPipe, CurrencyPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import {
  NbCardModule,
  NbTabsetModule,
  NbInputModule,
  NbSelectModule,
  NbButtonModule,
  NbSpinnerModule,
} from '@nebular/theme';

import { ApiService } from '../../core/api.service';
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
    NbCardModule,
    NbTabsetModule,
    NbInputModule,
    NbSelectModule,
    NbButtonModule,
    NbSpinnerModule,
  ],
  template: `
    <nb-card>
      <nb-card-header>New Transaction</nb-card-header>
      <nb-card-body>
        <nb-tabset>
          <nb-tab tabTitle="Expense">
            <form class="form" [formGroup]="expenseForm" (ngSubmit)="submitExpense()">
              <div class="row">
                <input nbInput fullWidth placeholder="Date ISO (e.g. 2026-02-25T10:00:00Z)" formControlName="txnDate" />
                <input nbInput fullWidth type="number" placeholder="Amount" formControlName="amount" />
              </div>
              <div class="row">
                <nb-select fullWidth placeholder="Account" formControlName="accountId">
                  <nb-option *ngFor="let a of accounts$ | async" [value]="a.id">{{ a.name }}</nb-option>
                </nb-select>
                <nb-select fullWidth placeholder="Category" formControlName="categoryId">
                  <nb-option *ngFor="let c of expenseCategories$ | async" [value]="c.id">{{ c.name }}</nb-option>
                </nb-select>
              </div>
              <input nbInput fullWidth placeholder="Description" formControlName="description" />
              <button nbButton status="primary" [disabled]="expenseForm.invalid">Save Expense</button>
            </form>
          </nb-tab>

          <nb-tab tabTitle="Income">
            <form class="form" [formGroup]="incomeForm" (ngSubmit)="submitIncome()">
              <div class="row">
                <input nbInput fullWidth placeholder="Date ISO (e.g. 2026-02-25T09:00:00Z)" formControlName="txnDate" />
                <input nbInput fullWidth type="number" placeholder="Amount" formControlName="amount" />
              </div>
              <div class="row">
                <nb-select fullWidth placeholder="Account" formControlName="accountId">
                  <nb-option *ngFor="let a of accounts$ | async" [value]="a.id">{{ a.name }}</nb-option>
                </nb-select>
                <nb-select fullWidth placeholder="Category" formControlName="categoryId">
                  <nb-option *ngFor="let c of incomeCategories$ | async" [value]="c.id">{{ c.name }}</nb-option>
                </nb-select>
              </div>
              <input nbInput fullWidth placeholder="Description" formControlName="description" />
              <button nbButton status="primary" [disabled]="incomeForm.invalid">Save Income</button>
            </form>
          </nb-tab>

          <nb-tab tabTitle="Transfer">
            <form class="form" [formGroup]="transferForm" (ngSubmit)="submitTransfer()">
              <div class="row">
                <input nbInput fullWidth placeholder="Date ISO (e.g. 2026-02-25T10:00:00Z)" formControlName="txnDate" />
                <input nbInput fullWidth type="number" placeholder="Amount" formControlName="amount" />
              </div>
              <div class="row">
                <nb-select fullWidth placeholder="From" formControlName="fromAccountId">
                  <nb-option *ngFor="let a of accounts$ | async" [value]="a.id">{{ a.name }}</nb-option>
                </nb-select>
                <nb-select fullWidth placeholder="To" formControlName="toAccountId">
                  <nb-option *ngFor="let a of accounts$ | async" [value]="a.id">{{ a.name }}</nb-option>
                </nb-select>
              </div>
              <input nbInput fullWidth placeholder="Description" formControlName="description" />
              <button nbButton status="primary" [disabled]="transferForm.invalid">Save Transfer</button>
            </form>
          </nb-tab>
        </nb-tabset>
      </nb-card-body>
    </nb-card>

    <nb-card>
      <nb-card-header>
        <div class="header">
          <div>Transactions (current period)</div>
          <button nbButton size="small" status="basic" (click)="reload()">Reload</button>
        </div>
      </nb-card-header>
      <nb-card-body>
        <ng-container *ngIf="txns$ | async as txns; else loading">
          <div class="table">
            <div class="row head">
              <div>Date</div>
              <div>Type</div>
              <div>Amount</div>
              <div>Account</div>
              <div>Category</div>
              <div>Description</div>
            </div>
            <div class="row" *ngFor="let t of txns">
              <div>{{ t.txnDate | date: 'MMM d, HH:mm' }}</div>
              <div>{{ t.type }}</div>
              <div>{{ t.amount | currency: 'LKR' }}</div>
              <div>{{ t.account?.name || (t.fromAccount?.name + ' → ' + t.toAccount?.name) }}</div>
              <div>{{ t.category?.name || '-' }}</div>
              <div>{{ t.description || '-' }}</div>
            </div>
            <div *ngIf="txns.length === 0" class="empty">No transactions yet.</div>
          </div>
        </ng-container>
        <ng-template #loading>
          <nb-spinner status="basic"></nb-spinner>
        </ng-template>
      </nb-card-body>
    </nb-card>
  `,
  styles: [
    `
      .form {
        display: grid;
        gap: 12px;
        padding: 12px 0;
      }
      .row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 12px;
      }
      .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 100%;
      }
      .table {
        display: grid;
        gap: 8px;
      }
      .table .row {
        grid-template-columns: 1.2fr 0.7fr 0.8fr 1fr 1fr 1.2fr;
        padding: 8px 10px;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.04);
      }
      .table .row.head {
        background: transparent;
        font-weight: 600;
        opacity: 0.8;
      }
      .empty {
        padding: 10px;
        opacity: 0.7;
      }
      @media (max-width: 1000px) {
        .table .row {
          grid-template-columns: 1fr;
        }
        .row {
          grid-template-columns: 1fr;
        }
      }
    `,
  ],
})
export class TransactionsComponent {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);

  private reload$ = new BehaviorSubject<void>(undefined);

  accounts$ = this.api.listAccounts().pipe(map((r) => r.data));
  expenseCategories$ = this.api.listCategories('EXPENSE').pipe(map((r) => r.data));
  incomeCategories$ = this.api.listCategories('INCOME').pipe(map((r) => r.data));

  expenseForm = this.fb.group({
    txnDate: ['', [Validators.required]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    accountId: ['', [Validators.required]],
    categoryId: ['', [Validators.required]],
    description: [''],
  });

  incomeForm = this.fb.group({
    txnDate: ['', [Validators.required]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    accountId: ['', [Validators.required]],
    categoryId: ['', [Validators.required]],
    description: [''],
  });

  transferForm = this.fb.group({
    txnDate: ['', [Validators.required]],
    amount: [null as number | null, [Validators.required, Validators.min(0.01)]],
    fromAccountId: ['', [Validators.required]],
    toAccountId: ['', [Validators.required]],
    description: [''],
  });

  txns$ = this.reload$.pipe(
    switchMap(() => this.api.currentPeriod()),
    switchMap((p) => {
      const from = `${p.data.periodStart}T00:00:00Z`;
      const to = `${p.data.periodEnd}T23:59:59Z`;
      return this.api.listTransactions({ from, to, page: 0, size: 50 });
    }),
    map((r) => r.data.items),
  );

  constructor() {
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
