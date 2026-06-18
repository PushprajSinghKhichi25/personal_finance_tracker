import api from './api';
import { Income, IncomeRequest, ApiResponse } from '@/types';

export const incomeService = {
  createIncome: async (data: IncomeRequest): Promise<Income> => {
    const response = await api.post<Income>('/income', data);
    return response.data;
  },

  getIncome: async (page = 0, size = 10): Promise<{ content: Income[]; totalElements: number }> => {
    const response = await api.get<any>('/income', {
      params: { page, size },
    });
    return {
        content: response.data.content,
        totalElements: response.data.totalElements,
    };
  },

  getIncomeById: async (id: number): Promise<Income> => {
    const response = await api.get<Income>(`/income/${id}`);
    return response.data;
  },

  updateIncome: async (id: number, data: IncomeRequest): Promise<Income> => {
    const response = await api.put<Income>(`/income/${id}`, data);
    return response.data;
  },

  deleteIncome: async (id: number): Promise<void> => {
    await api.delete(`/income/${id}`);
  },
};