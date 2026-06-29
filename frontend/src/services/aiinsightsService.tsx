import api from './api';

export interface CategoryInsight {
  category: string;
  amount: string;
  percentOfTotal: string;
  trend: string;
  percentChange: string;
  budgetStatus: {
    allocated: string;
    spent: string;
    remaining: string;
    percentageUsed: string;
    isOverBudget: boolean;
  };
  insight: string;
}

export interface BudgetInsight {
  allocated: string;
  spent: string;
  overage: string;
  insight: string;
}

export interface InsightsContent {
  overallSummary: string;
  categoryInsights: CategoryInsight[];
  topConcerns: string[];
  positiveAchievements: string[];
}

export interface BudgetRecommendation {
  category: string;
  currentBudget: string;
  recommendedBudget: string;
  reasoning: string;
  confidence: string;
}

export interface RecommendationsContent {
  budgetAdjustments: BudgetRecommendation[];
  savingsOpportunities: string[];
  actionableAdvice: string[];
}

export interface AIInsightResponse {
  month: string;
  generatedAt: string;
  insights: InsightsContent;
  recommendations: RecommendationsContent;
}

export interface CacheStats {
  totalCached: number;
  cacheHitRate: string;
  lastCleared?: string;
}

export interface UsageStats {
  requestsThisMinute: number;
  tokensUsedThisMonth: number;
  monthlyTokenLimit: number;
  requestsPerMinuteLimit: number;
  canMakeRequest: boolean;
}

export const aiInsightsService = {
  // Get insights for a specific month
  async getInsights(month: string): Promise<AIInsightResponse> {
    const response = await api.get<any>(
      `/ai/insights/monthly?month=${month}`
    );
    // Extract insights from response wrapper
    return response.data.insights || response.data;
  },

  // Get insights for current month
  async getCurrentInsights(): Promise<AIInsightResponse> {
    const response = await api.get<any>(
      '/ai/insights/current'
    );
    // Extract insights from response wrapper
    return response.data.insights || response.data;
  },

  // Get all budget recommendations (fallback to empty)
  async getBudgetRecommendations(months: number = 3): Promise<any> {
    try {
      const response = await api.get('/ai/recommendations/budgets', {
        params: { months },
      });
      return response.data;
    } catch (err) {
      console.warn('Budget recommendations endpoint not available:', err);
      return { recommendations: [] };
    }
  },

  // Get high priority recommendations (fallback to empty)
  async getHighPriorityRecommendations(): Promise<any> {
    try {
      const response = await api.get('/ai/recommendations/high-priority');
      return response.data;
    } catch (err) {
      console.warn('High priority recommendations endpoint not available:', err);
      return { recommendations: [] };
    }
  },

  // Get savings opportunities (fallback to empty)
  async getSavingsOpportunities(): Promise<any> {
    try {
      const response = await api.get('/ai/recommendations/savings');
      return response.data;
    } catch (err) {
      console.warn('Savings opportunities endpoint not available:', err);
      return { recommendations: [] };
    }
  },

  // Get budget increase recommendations
  async getBudgetIncreases(): Promise<any> {
    try {
      const response = await api.get('/ai/recommendations/increases');
      return response.data;
    } catch (err) {
      console.warn('Budget increases endpoint not available:', err);
      return { recommendations: [] };
    }
  },

  // Invalidate cache for a month
  async invalidateCache(month: string): Promise<void> {
    await api.post('/ai/insights/invalidate', null, {
      params: { month },
    });
  },

  // Get cache statistics
  async getCacheStats(): Promise<CacheStats> {
    try {
      const response = await api.get<any>('/ai/insights/cache-stats');
      return response.data;
    } catch (err) {
      console.warn('Cache stats endpoint not available:', err);
      return { totalCached: 0, cacheHitRate: '0%' };
    }
  },

  // Get API usage statistics
  async getUsageStats(): Promise<UsageStats> {
    try {
      const response = await api.get<any>('/ai/insights/usage-stats');
      return response.data;
    } catch (err) {
      console.warn('Usage stats endpoint not available:', err);
      return {
        requestsThisMinute: 0,
        tokensUsedThisMonth: 0,
        monthlyTokenLimit: 1000000,
        requestsPerMinuteLimit: 60,
        canMakeRequest: true,
      };
    }
  },

  // Check AI health
  async checkHealth(): Promise<any> {
    try {
      const response = await api.get('/ai/health');
      return response.data;
    } catch (err) {
      console.warn('Health check failed:', err);
      return { status: 'not_configured' };
    }
  },
};
