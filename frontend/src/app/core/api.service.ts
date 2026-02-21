import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  ApiEnvelope,
  AccountResponse,
  CategoryResponse,
  PeriodResponse,
  DashboardSummaryResponse,
  RecentExpenseItem,
  ByCategoryResponse,
  DailyTrendResponse,
  TxnResponse,
  PageResponse,
  TransactionType,
} from './api.models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  // In Docker Compose we serve the Angular app via Nginx and proxy /api -> backend.
  // In local dev you can use an Angular proxy config to forward /api to http://localhost:8080.
  private readonly baseUrl = '/api';

  constructor(private http: HttpClient) {}

  // Me
  me(): Observable<ApiEnvelope<{ id: string; baseCurrency: string; periodStartDay: number; createdAt: string; updatedAt: string }>> {
    return this.http.get<ApiEnvelope<{ id: string; baseCurrency: string; periodStartDay: number; createdAt: string; updatedAt: string }>>(
      `${this.baseUrl}/me`
    );
  }

  patchSettings(payload: { baseCurrency: string; periodStartDay: number }): Observable<ApiEnvelope<{ id: string; baseCurrency: string; periodStartDay: number; createdAt: string; updatedAt: string }>> {
    return this.http.patch<
      ApiEnvelope<{ id: string; baseCurrency: string; periodStartDay: number; createdAt: string; updatedAt: string }>
    >(`${this.baseUrl}/me/settings`, payload);
  }

  // Accounts
  listAccounts(): Observable<ApiEnvelope<AccountResponse[]>> {
    return this.http.get<ApiEnvelope<AccountResponse[]>>(`${this.baseUrl}/accounts`);
  }

  // Categories
  listCategories(type?: 'INCOME' | 'EXPENSE'): Observable<ApiEnvelope<CategoryResponse[]>> {
    const params = type ? new HttpParams().set('type', type) : undefined;
    return this.http.get<ApiEnvelope<CategoryResponse[]>>(`${this.baseUrl}/categories`, { params });
  }

  // Period / Dashboard
  currentPeriod(): Observable<ApiEnvelope<PeriodResponse>> {
    return this.http.get<ApiEnvelope<PeriodResponse>>(`${this.baseUrl}/period/current`);
  }

  dashboardSummary(from?: string, to?: string): Observable<ApiEnvelope<DashboardSummaryResponse>> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);
    return this.http.get<ApiEnvelope<DashboardSummaryResponse>>(`${this.baseUrl}/dashboard/summary`, { params });
  }

  recentExpenses(limit = 10): Observable<ApiEnvelope<RecentExpenseItem[]>> {
    const params = new HttpParams().set('limit', String(limit));
    return this.http.get<ApiEnvelope<RecentExpenseItem[]>>(`${this.baseUrl}/dashboard/recent-expenses`, { params });
  }

  expensesByCategory(from: string, to: string): Observable<ApiEnvelope<ByCategoryResponse>> {
    const params = new HttpParams().set('from', from).set('to', to);
    return this.http.get<ApiEnvelope<ByCategoryResponse>>(`${this.baseUrl}/reports/expenses/by-category`, { params });
  }

  dailyTrend(from: string, to: string): Observable<ApiEnvelope<DailyTrendResponse>> {
    const params = new HttpParams().set('from', from).set('to', to);
    return this.http.get<ApiEnvelope<DailyTrendResponse>>(`${this.baseUrl}/reports/expenses/daily-trend`, { params });
  }

  // Transactions
  listTransactions(opts: {
    from: string; // ISO instant
    to: string;
    type?: TransactionType;
    accountId?: string;
    categoryId?: string;
    page?: number;
    size?: number;
  }): Observable<ApiEnvelope<PageResponse<TxnResponse>>> {
    let params = new HttpParams().set('from', opts.from).set('to', opts.to);
    if (opts.type) params = params.set('type', opts.type);
    if (opts.accountId) params = params.set('accountId', opts.accountId);
    if (opts.categoryId) params = params.set('categoryId', opts.categoryId);
    params = params.set('page', String(opts.page ?? 0)).set('size', String(opts.size ?? 20));
    return this.http.get<ApiEnvelope<PageResponse<TxnResponse>>>(`${this.baseUrl}/transactions`, { params });
  }

  createExpenseOrIncome(payload: {
    txnDate: string;
    type: 'INCOME' | 'EXPENSE';
    amount: number;
    accountId: string;
    categoryId: string;
    description?: string;
    merchant?: string;
    paymentMethod?: string;
  }): Observable<ApiEnvelope<TxnResponse>> {
    return this.http.post<ApiEnvelope<TxnResponse>>(`${this.baseUrl}/transactions`, payload);
  }

  createTransfer(payload: {
    txnDate: string;
    amount: number;
    fromAccountId: string;
    toAccountId: string;
    description?: string;
  }): Observable<ApiEnvelope<TxnResponse>> {
    return this.http.post<ApiEnvelope<TxnResponse>>(`${this.baseUrl}/transactions/transfer`, payload);
  }
}
