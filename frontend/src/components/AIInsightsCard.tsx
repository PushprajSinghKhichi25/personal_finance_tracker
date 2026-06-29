import { useState, useEffect } from 'react';
import { Brain, TrendingUp, TrendingDown, AlertCircle } from 'lucide-react';
import { aiInsightsService, AIInsightResponse } from '@/services/aiInsightsService';

interface AIInsightsCardProps {
  onViewFull: () => void;
}

export default function AIInsightsCard({ onViewFull }: AIInsightsCardProps) {
  const [insights, setInsights] = useState<AIInsightResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadInsights();
  }, []);

  const loadInsights = async () => {
    try {
      const insightsData = await aiInsightsService.getCurrentInsights();
      setInsights(insightsData);
    } catch (err) {
      console.error('Failed to load AI insights:', err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="bg-gradient-to-br from-cyan-50 to-blue-50 rounded-lg shadow p-6 border border-cyan-200">
        <div className="flex items-center space-x-2 mb-4">
          <Brain className="h-6 w-6 text-cyan-600" />
          <h3 className="text-lg font-bold text-gray-900">AI Insights</h3>
        </div>
        <div className="animate-pulse">
          <div className="h-4 bg-gray-300 rounded mb-2"></div>
          <div className="h-4 bg-gray-300 rounded w-5/6"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-gradient-to-br from-cyan-50 to-blue-50 rounded-lg shadow p-6 border border-cyan-200">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-2">
          <Brain className="h-6 w-6 text-cyan-600" />
          <h3 className="text-lg font-bold text-gray-900">AI Insights</h3>
        </div>
      </div>

      {insights && insights.insights ? (
        <div className="space-y-3">
          <p className="text-gray-700 text-sm leading-relaxed line-clamp-2">
            {insights.insights.overallSummary}
          </p>

          {insights.insights.topConcerns && insights.insights.topConcerns.length > 0 && (
            <div className="flex items-start space-x-2 bg-red-50 p-3 rounded border border-red-200">
              <AlertCircle className="h-5 w-5 text-red-600 flex-shrink-0 mt-0.5" />
              <p className="text-sm text-red-800">{insights.insights.topConcerns[0]}</p>
            </div>
          )}

          {insights.insights.categoryInsights && insights.insights.categoryInsights.length > 0 && (
            <div className="grid grid-cols-2 gap-2">
              {insights.insights.categoryInsights.slice(0, 2).map((cat, idx) => (
                <div key={idx} className="bg-white rounded p-3 border border-gray-200">
                  <p className="text-xs text-gray-600 font-medium">{cat.category}</p>
                  <div className="flex items-center space-x-1 mt-1">
                    {cat.percentChange?.startsWith('-') ? (
                      <TrendingDown size={16} className="text-green-600" />
                    ) : (
                      <TrendingUp size={16} className="text-red-600" />
                    )}
                    <span className={`text-sm font-bold ${
                      cat.percentChange?.startsWith('-') ? 'text-green-600' : 'text-red-600'
                    }`}>
                      {cat.percentChange}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      ) : (
        <p className="text-gray-600 text-sm">No insights available yet. Start tracking your expenses to get personalized insights.</p>
      )}

      <button
        onClick={onViewFull}
        className="w-full mt-4 bg-cyan-600 hover:bg-cyan-700 text-white font-semibold px-4 py-2 rounded-lg transition"
      >
        View Full Insights
      </button>
    </div>
  );
}
