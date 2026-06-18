import api from './api';
import { Budget, BudgetRequest, ApiResponse } from '@/types';

export const budgetService = {
  createBudget: async (data: BudgetRequest): Promise<Budget> => {
    const response = await api.post<Budget>('/budgets', data);
    return response.data;
  },

  getBudgets: async (page = 0, size = 10): Promise<{ content: Budget[]; totalElements: number }> => {
    const response = await api.get<any>('/budgets', {
      params: { page, size },
    });
    return {
    content: response.data.content,
    totalElements: response.data.totalElements,
    };
  },

  getBudgetById: async (id: number): Promise<Budget> => {
    const response = await api.get<Budget>(`/budgets/${id}`);
    return response.data;
  },

  updateBudget: async (id: number, data: BudgetRequest): Promise<Budget> => {
    const response = await api.put<Budget>(`/budgets/${id}`, data);
    return response.data
  },

  deleteBudget: async (id: number): Promise<void> => {
    await api.delete(`/budgets/${id}`);
  },

  getBudgetStatus: async (id: number) => {
    const response = await api.get<any>(`/budgets/${id}/status`);
    return response.data;
  },
};
