import api from './api';
import { Expense, ExpenseRequest, ApiResponse } from '@/types';

export const expenseService = {
  createExpense: async (data: ExpenseRequest): Promise<Expense> => {
    const response = await api.post<Expense>('/expenses', data);
    return response.data;
  },

  getExpenses: async (page = 0, size = 10): Promise<{ content: Expense[]; totalElements: number }> => {
    const response = await api.get<any>('/expenses', {
      params: { page, size },
    });
    return {
        content: response.data.content,
        totalElements:response.data.totalElements,
        };
  },

  getExpenseById: async (id: number): Promise<Expense> => {
    const response = await api.get<Expense>(`/expenses/${id}`);
    return response.data;
  },

  updateExpense: async (id: number, data: ExpenseRequest): Promise<Expense> => {
    const response = await api.put<Expense>(`/expenses/${id}`, data);
    return response.data;
  },

  deleteExpense: async (id: number): Promise<void> => {
    await api.delete(`/expenses/${id}`);
  },

  getCategories: async () => {
    const response = await api.get<any>('/categories');
    return response.data;
  },
};
