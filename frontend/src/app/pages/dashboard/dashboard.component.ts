import { Component, inject } from '@angular/core';
import { AsyncPipe, CurrencyPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { combineLatest, map, switchMap } from 'rxjs';

import { NbCardModule, NbBadgeModule, NbSpinnerModule } from '@nebular/theme';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgIf, NgFor, AsyncPipe, CurrencyPipe, DatePipe, NbCardModule, NbBadgeModule, NbSpinnerModule],
  template: `
    <ng-container *ngIf="vm$ | async as vm; else loading">
      <div class="grid">
        <nb-card class="kpi">
          <nb-card-header>Period</nb-card-header>
          <nb-card-body>{{ vm.period.periodStart }} → {{ vm.period.periodEnd }}</nb-card-body>
        </nb-card>

        <nb-card class="kpi">
          <nb-card-header>Income</nb-card-header>
          <nb-card-body>{{ vm.summary.incomeTotal | currency: 'LKR' }}</nb-card-body>
        </nb-card>

        <nb-card class="kpi">
          <nb-card-header>Expenses</nb-card-header>
          <nb-card-body>{{ vm.summary.expenseTotal | currency: 'LKR' }}</nb-card-body>
        </nb-card>

        <nb-card class="kpi">
          <nb-card-header>Net</nb-card-header>
          <nb-card-body>{{ vm.summary.netTotal | currency: 'LKR' }}</nb-card-body>
        </nb-card>

        <nb-card class="wide">
          <nb-card-header>Recent Expenses</nb-card-header>
          <nb-card-body>
            <div class="table">
              <div class="row head">
                <div>Date</div>
                <div>Category</div>
                <div>Amount</div>
                <div>Account</div>
              </div>
              <div class="row" *ngFor="let e of vm.recent">
                <div>{{ e.txnDate | date: 'MMM d, HH:mm' }}</div>
                <div>{{ e.category?.name || '-' }}</div>
                <div>{{ e.amount | currency: 'LKR' }}</div>
                <div>{{ e.account?.name || '-' }}</div>
              </div>
              <div *ngIf="vm.recent.length === 0" class="empty">No expenses yet.</div>
            </div>
          </nb-card-body>
        </nb-card>
      </div>
    </ng-container>

    <ng-template #loading>
      <div style="padding: 24px;">
        <nb-spinner status="basic"></nb-spinner>
      </div>
    </ng-template>
  `,
  styles: [
    `
      .grid {
        display: grid;
        grid-template-columns: repeat(4, minmax(0, 1fr));
        gap: 1rem;
      }
      .wide {
        grid-column: 1 / -1;
      }
      .kpi nb-card-body {
        font-size: 1.15rem;
        font-weight: 600;
      }
      .table {
        display: grid;
        gap: 8px;
      }
      .row {
        display: grid;
        grid-template-columns: 1.2fr 1fr 0.8fr 0.8fr;
        gap: 12px;
        padding: 8px 10px;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.04);
      }
      .row.head {
        background: transparent;
        font-weight: 600;
        opacity: 0.8;
      }
      .empty {
        padding: 10px;
        opacity: 0.7;
      }
      @media (max-width: 1100px) {
        .grid {
          grid-template-columns: repeat(2, minmax(0, 1fr));
        }
      }
      @media (max-width: 700px) {
        .grid {
          grid-template-columns: 1fr;
        }
        .row {
          grid-template-columns: 1fr;
        }
      }
    `,
  ],
})
export class DashboardComponent {
  private api = inject(ApiService);

  vm$ = this.api
    .currentPeriod()
    .pipe(
      switchMap((p) =>
        combineLatest([
          this.api.dashboardSummary(p.data.periodStart, p.data.periodEnd),
          this.api.recentExpenses(10),
        ]).pipe(
          map(([summary, recent]) => ({
            period: p.data,
            summary: summary.data,
            recent: recent.data,
          })),
        ),
      ),
    );
}
