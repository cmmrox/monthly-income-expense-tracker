import { Component, inject } from '@angular/core';
import { AsyncPipe, CurrencyPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { ApiService } from '../../core/api.service';
import { combineLatest, map, switchMap } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    NgIf,
    NgFor,
    AsyncPipe,
    CurrencyPipe,
    DatePipe,
    MatCardModule,
    MatProgressBarModule,
    MatTableModule,
    MatChipsModule,
    MatIconModule,
  ],
  template: `
    <div class="grid" *ngIf="vm$ | async as vm">
      <mat-card class="card">
        <div class="card-title">Period</div>
        <div class="card-value">{{ vm.period.periodStart }} → {{ vm.period.periodEnd }}</div>
      </mat-card>

      <mat-card class="card">
        <div class="card-title">Income</div>
        <div class="card-value">{{ vm.summary.incomeTotal | currency: 'LKR' }}</div>
      </mat-card>

      <mat-card class="card">
        <div class="card-title">Expenses</div>
        <div class="card-value">{{ vm.summary.expenseTotal | currency: 'LKR' }}</div>
      </mat-card>

      <mat-card class="card">
        <div class="card-title">Net</div>
        <div class="card-value">{{ vm.summary.netTotal | currency: 'LKR' }}</div>
      </mat-card>

      <mat-card class="wide">
        <div class="section-title">Recent Expenses</div>
        <table mat-table [dataSource]="vm.recent" class="table">
          <ng-container matColumnDef="date">
            <th mat-header-cell *matHeaderCellDef>Date</th>
            <td mat-cell *matCellDef="let e">{{ e.txnDate | date: 'MMM d, HH:mm' }}</td>
          </ng-container>
          <ng-container matColumnDef="category">
            <th mat-header-cell *matHeaderCellDef>Category</th>
            <td mat-cell *matCellDef="let e">
              <mat-chip *ngIf="e.category" [style.background]="e.category.color || '#333'" class="chip">
                {{ e.category.name }}
              </mat-chip>
            </td>
          </ng-container>
          <ng-container matColumnDef="amount">
            <th mat-header-cell *matHeaderCellDef>Amount</th>
            <td mat-cell *matCellDef="let e">{{ e.amount | currency: 'LKR' }}</td>
          </ng-container>
          <ng-container matColumnDef="account">
            <th mat-header-cell *matHeaderCellDef>Account</th>
            <td mat-cell *matCellDef="let e">{{ e.account?.name || '-' }}</td>
          </ng-container>

          <tr mat-header-row *matHeaderRowDef="cols"></tr>
          <tr mat-row *matRowDef="let row; columns: cols"></tr>
        </table>
      </mat-card>
    </div>

    <mat-progress-bar mode="indeterminate" *ngIf="loading"></mat-progress-bar>
  `,
  styles: [
    `
      .grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
      .card { padding: 8px; }
      .wide { grid-column: 1 / -1; }
      .card-title { opacity: 0.7; font-size: 12px; }
      .card-value { font-size: 20px; font-weight: 600; margin-top: 4px; }
      .section-title { font-weight: 600; margin-bottom: 12px; }
      .table { width: 100%; }
      .chip { color: white; }
    `,
  ],
})
export class DashboardComponent {
  private api = inject(ApiService);

  cols = ['date', 'category', 'amount', 'account'];
  loading = false;

  vm$ = this.api.currentPeriod().pipe(
    switchMap((p) =>
      combineLatest([
        this.api.dashboardSummary(p.data.periodStart, p.data.periodEnd),
        this.api.recentExpenses(10),
      ]).pipe(
        map(([summary, recent]) => ({
          period: p.data,
          summary: summary.data,
          recent: recent.data,
        }))
      )
    )
  );
}
