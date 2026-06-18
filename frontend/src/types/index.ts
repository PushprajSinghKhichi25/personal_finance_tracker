// User types
export interface User {
  id: number;
  username: string;
  email: string;
}

export interface AuthResponse {
  id:number;
  username:string;
  email:string;
  accessToken:string;
  refreshToken?:string;
  tokenType: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

// Expense types
export interface Expense {
  id: number;
  description: string;
  amount: number;
  date: string;
  categories: Category[];
  createdAt: string;
  updatedAt: string;
}

export interface ExpenseRequest {
  description: string;
  amount: number;
  date: string;
  categoryIds: number[];
}

// Category types
export interface Category {
  id: number;
  name: string;
  description: string;
}

// Income types
export interface Income {
  id: number;
  description: string;
  amount: number;
  source: string;
  date: string;
  createdAt: string;
  updatedAt: string;
}

export interface IncomeRequest {
  description: string;
  amount: number;
  source: string;
  date: string;
}

// Budget types
export interface Budget {
  id: number;
  category: Category;
  monthlyLimit: number;
  currentMonth: string;
  createdAt: string;
  updatedAt: string;
}

export interface BudgetRequest {
  categoryId: number;
  monthlyLimit: number;
}

export interface BudgetStatus {
  limit: number;
  spent: number;
  remaining: number;
}

// Report types
export interface MonthlyReport {
  month: string;
  totalIncome: number;
  totalExpenses: number;
  net: number;
  byCategory: Record<string, number>;
  budgetStatus: Record<string, BudgetStatus>;
}

export interface CategoryBreakdown {
  category: string;
  amount: number;
  percentage: number;
}

export interface TrendData {
  month: string;
  income: number;
  expenses: number;
}

// API Response type
export interface ApiResponse<T> {
  data: T;
  message: string;
  timestamp: string;
}