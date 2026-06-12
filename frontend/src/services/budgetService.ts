import api from './api';
import { Budget, BudgetRequest, ApiResponse } from '@/types';

export const budgetService = {
  createBudget: async (data: BudgetRequest): Promise<Budget> => {
    const response = await api.post<ApiResponse<Budget>>('/budgets', data);
    return response.data.data;
  },

  getBudgets: async (page = 0, size = 10): Promise<{ content: Budget[]; totalElements: number }> => {
    const response = await api.get<ApiResponse<{ content: Budget[]; totalElements: number }>>('/budgets', {
      params: { page, size },
    });
    return response.data.data;
  },

  getBudgetById: async (id: number): Promise<Budget> => {
    const response = await api.get<ApiResponse<Budget>>(`/budgets/${id}`);
    return response.data.data;
  },

  updateBudget: async (id: number, data: BudgetRequest): Promise<Budget> => {
    const response = await api.put<ApiResponse<Budget>>(`/budgets/${id}`, data);
    return response.data.data;
  },

  deleteBudget: async (id: number): Promise<void> => {
    await api.delete(`/budgets/${id}`);
  },

  getBudgetStatus: async (id: number) => {
    const response = await api.get<ApiResponse<any>>(`/budgets/${id}/status`);
    return response.data.data;
  },
};
