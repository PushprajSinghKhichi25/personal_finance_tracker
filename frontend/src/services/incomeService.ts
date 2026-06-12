import api from './api';
import { Income, IncomeRequest, ApiResponse } from '@/types';

export const incomeService = {
  createIncome: async (data: IncomeRequest): Promise<Income> => {
    const response = await api.post<ApiResponse<Income>>('/income', data);
    return response.data.data;
  },

  getIncome: async (page = 0, size = 10): Promise<{ content: Income[]; totalElements: number }> => {
    const response = await api.get<ApiResponse<{ content: Income[]; totalElements: number }>>('/income', {
      params: { page, size },
    });
    return response.data.data;
  },

  getIncomeById: async (id: number): Promise<Income> => {
    const response = await api.get<ApiResponse<Income>>(`/income/${id}`);
    return response.data.data;
  },

  updateIncome: async (id: number, data: IncomeRequest): Promise<Income> => {
    const response = await api.put<ApiResponse<Income>>(`/income/${id}`, data);
    return response.data.data;
  },

  deleteIncome: async (id: number): Promise<void> => {
    await api.delete(`/income/${id}`);
  },
};