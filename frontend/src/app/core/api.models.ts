export type AccountType = 'BANK' | 'CASH' | 'CREDIT_CARD';
export type CategoryType = 'INCOME' | 'EXPENSE';
export type TransactionType = 'INCOME' | 'EXPENSE' | 'TRANSFER';

export interface ApiEnvelope<T> {
  data: T;
  meta: { requestId: string | null; ts: string };
}

export interface AccountRef { id: string; name: string; type: AccountType; }
export interface CategoryRef { id: string; name: string; type: CategoryType; color?: string | null; }

export interface AccountResponse {
  id: string;
  name: string;
  type: AccountType;
  currency: string;
  openingBalance: number;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CategoryResponse {
  id: string;
  name: string;
  type: CategoryType;
  color?: string | null;
  icon?: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface PeriodResponse {
  periodStart: string; // yyyy-MM-dd
  periodEnd: string;
  periodStartDay: number;
}

export interface TxnResponse {
  id: string;
  txnDate: string;
  type: TransactionType;
  amount: number;
  account?: AccountRef | null;
  category?: CategoryRef | null;
  fromAccount?: AccountRef | null;
  toAccount?: AccountRef | null;
  description?: string | null;
  merchant?: string | null;
  paymentMethod?: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface DashboardSummaryResponse {
  from: string;
  to: string;
  incomeTotal: number;
  expenseTotal: number;
  netTotal: number;
  byAccount: Array<{
    account: AccountRef;
    income: number;
    expense: number;
    transfersOut: number;
    transfersIn: number;
  }>;
}

export interface RecentExpenseItem {
  id: string;
  txnDate: string;
  amount: number;
  category: CategoryRef | null;
  account: AccountRef | null;
  description?: string | null;
  merchant?: string | null;
}

export interface ByCategoryResponse {
  from: string;
  to: string;
  items: Array<{ category: CategoryRef | null; total: number; percent: number }>;
}

export interface DailyTrendResponse {
  from: string;
  to: string;
  points: Array<{ date: string; total: number }>;
}
