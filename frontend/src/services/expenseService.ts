import api from './api';
import { Expense, ExpenseRequest, ApiResponse } from '@/types';

export const expenseService = {
  createExpense: async (data: ExpenseRequest): Promise<Expense> => {
    const response = await api.post<ApiResponse<Expense>>('/expenses', data);
    return response.data.data;
  },

  getExpenses: async (page = 0, size = 10): Promise<{ content: Expense[]; totalElements: number }> => {
    const response = await api.get<ApiResponse<{ content: Expense[]; totalElements: number }>>('/expenses', {
      params: { page, size },
    });
    return response.data.data;
  },

  getExpenseById: async (id: number): Promise<Expense> => {
    const response = await api.get<ApiResponse<Expense>>(`/expenses/${id}`);
    return response.data.data;
  },

  updateExpense: async (id: number, data: ExpenseRequest): Promise<Expense> => {
    const response = await api.put<ApiResponse<Expense>>(`/expenses/${id}`, data);
    return response.data.data;
  },

  deleteExpense: async (id: number): Promise<void> => {
    await api.delete(`/expenses/${id}`);
  },

  getCategories: async () => {
    const response = await api.get<ApiResponse<any>>('/categories');
    return response.data.data;
  },
};
