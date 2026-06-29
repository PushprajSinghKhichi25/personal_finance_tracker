import { useState, useEffect } from 'react';
import Navbar from '@/components/Navbar';
import { aiInsightsService, AIInsightResponse, BudgetRecommendation } from '@/services/aiInsightsService';
import { TrendingUp, TrendingDown, AlertCircle, CheckCircle, Lightbulb, RefreshCw } from 'lucide-react';

export default function AIInsightsDashboard() {
  const [insights, setInsights] = useState<AIInsightResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [activeTab, setActiveTab] = useState<'insights' | 'recommendations'>('insights');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);

      // Load insights
      const insightsData = await aiInsightsService.getCurrentInsights();
      setInsights(insightsData);
    } catch (err) {
      console.error('Failed to load AI insights:', err);
      setError('Failed to load AI insights. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <RefreshCw className="animate-spin h-12 w-12 text-blue-600 mx-auto mb-4" />
          <p className="text-gray-600">Loading AI insights...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gray-50 p-4">
        <Navbar />
        <div className="max-w-7xl mx-auto mt-8">
          <div className="bg-red-50 border border-red-200 rounded-lg p-6">
            <div className="flex items-center space-x-3">
              <AlertCircle className="h-6 w-6 text-red-600" />
              <div>
                <h3 className="font-semibold text-red-900">Error Loading Insights</h3>
                <p className="text-red-700">{error}</p>
                <button
                  onClick={loadData}
                  className="mt-3 text-red-700 hover:text-red-900 font-semibold underline"
                >
                  Try Again
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="p-4 sm:p-6 lg:p-8">
        <div className="max-w-7xl mx-auto">
          //{ Header }
          <div className="flex justify-between items-center mb-8">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">AI Insights & Recommendations</h1>
              <p className="text-gray-600 mt-1">
                {insights?.month ? `Insights for ${insights.month}` : 'Latest insights'}
              </p>
            </div>
            <button
              onClick={loadData}
              className="flex items-center space-x-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg transition"
            >
              <RefreshCw size={20} />
              <span>Refresh</span>
            </button>
          </div>

          //{ Tabs }
          <div className="flex space-x-4 mb-6 border-b">
            <button
              onClick={() => setActiveTab('insights')}
              className={`px-4 py-3 font-medium transition ${
                activeTab === 'insights'
                  ? 'text-blue-600 border-b-2 border-blue-600'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Monthly Insights
            </button>
            <button
              onClick={() => setActiveTab('recommendations')}
              className={`px-4 py-3 font-medium transition ${
                activeTab === 'recommendations'
                  ? 'text-blue-600 border-b-2 border-blue-600'
                  : 'text-gray-600 hover:text-gray-900'
              }`}
            >
              Budget Recommendations
            </button>
          </div>

         // { Insights Tab }
          {activeTab === 'insights' && insights && (
            <div className="space-y-6">
//               { Overall Summary }
              <div className="bg-white rounded-lg shadow p-6">
                <h2 className="text-xl font-bold text-gray-900 mb-4">Monthly Summary</h2>
                <p className="text-gray-700 leading-relaxed">{insights.insights.overallSummary}</p>
              </div>

              //{ Top Concerns }
              {insights.insights.topConcerns && insights.insights.topConcerns.length > 0 && (
                <div className="bg-red-50 rounded-lg shadow p-6 border border-red-200">
                  <div className="flex items-center space-x-3 mb-4">
                    <AlertCircle className="h-6 w-6 text-red-600" />
                    <h3 className="text-lg font-bold text-red-900">Areas of Concern</h3>
                  </div>
                  <ul className="space-y-3">
                    {insights.insights.topConcerns.map((concern, idx) => (
                      <li key={idx} className="flex items-start space-x-3">
                        <span className="text-red-600 font-bold">•</span>
                        <span className="text-red-800">{concern}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              //{ Positive Achievements }
              {insights.insights.positiveAchievements && insights.insights.positiveAchievements.length > 0 && (
                <div className="bg-green-50 rounded-lg shadow p-6 border border-green-200">
                  <div className="flex items-center space-x-3 mb-4">
                    <CheckCircle className="h-6 w-6 text-green-600" />
                    <h3 className="text-lg font-bold text-green-900">Positive Achievements</h3>
                  </div>
                  <ul className="space-y-3">
                    {insights.insights.positiveAchievements.map((achievement, idx) => (
                      <li key={idx} className="flex items-start space-x-3">
                        <span className="text-green-600 font-bold">✓</span>
                        <span className="text-green-800">{achievement}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              //{ Category Insights }
              {insights.insights.categoryInsights && insights.insights.categoryInsights.length > 0 && (
                <div className="space-y-4">
                  <h3 className="text-lg font-bold text-gray-900">Category Breakdown</h3>
                  {insights.insights.categoryInsights.map((cat, idx) => (
                    <div key={idx} className="bg-white rounded-lg shadow p-6">
                      <div className="flex justify-between items-start mb-4">
                        <div>
                          <h4 className="text-lg font-semibold text-gray-900">{cat.category}</h4>
                          <p className="text-gray-600 text-sm mt-1">{cat.insight}</p>
                        </div>
                        <div className={`flex items-center space-x-1 ${
                          cat.percentChange?.startsWith('-') ? 'text-green-600' : 'text-red-600'
                        }`}>
                          {cat.percentChange?.startsWith('-') ? (
                            <TrendingDown size={20} />
                          ) : (
                            <TrendingUp size={20} />
                          )}
                          <span className="font-bold">{cat.percentChange}</span>
                        </div>
                      </div>

                      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <div className="bg-gray-50 rounded p-3">
                          <p className="text-sm text-gray-600">Amount</p>
                          <p className="text-lg font-bold text-gray-900">{cat.amount}</p>
                        </div>
                        <div className="bg-gray-50 rounded p-3">
                          <p className="text-sm text-gray-600">% of Total</p>
                          <p className="text-lg font-bold text-gray-900">{cat.percentOfTotal}</p>
                        </div>
                        <div className="bg-gray-50 rounded p-3">
                          <p className="text-sm text-gray-600">Budget Used</p>
                          <p className="text-lg font-bold text-gray-900">{cat.budgetStatus?.percentageUsed || 'N/A'}</p>
                        </div>
                        <div className={`rounded p-3 ${cat.budgetStatus?.isOverBudget ? 'bg-red-50' : 'bg-green-50'}`}>
                          <p className="text-sm text-gray-600">Status</p>
                          <p className={`text-lg font-bold ${cat.budgetStatus?.isOverBudget ? 'text-red-600' : 'text-green-600'}`}>
                            {cat.budgetStatus?.isOverBudget ? 'Over Budget' : 'On Track'}
                          </p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

         // { Recommendations Tab }
          {activeTab === 'recommendations' && insights && (
            <div className="space-y-6">
              //{ Budget Adjustments }
              {insights.recommendations?.budgetAdjustments && insights.recommendations.budgetAdjustments.length > 0 && (
                <div>
                  <h3 className="text-lg font-bold text-gray-900 mb-4">Budget Adjustments</h3>
                  <div className="space-y-4">
                    {insights.recommendations.budgetAdjustments.map((rec, idx) => (
                      <RecommendationCard key={idx} recommendation={rec} />
                    ))}
                  </div>
                </div>
              )}

              //{ Savings Opportunities }
              {insights.recommendations?.savingsOpportunities && insights.recommendations.savingsOpportunities.length > 0 && (
                <div className="bg-blue-50 rounded-lg shadow p-6 border border-blue-200">
                  <h3 className="text-lg font-bold text-blue-900 mb-4">Savings Opportunities</h3>
                  <ul className="space-y-3">
                    {insights.recommendations.savingsOpportunities.map((opp, idx) => (
                      <li key={idx} className="flex items-start space-x-3">
                        <span className="text-blue-600 font-bold">💡</span>
                        <span className="text-blue-800">{opp}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              //{ Actionable Advice }
              {insights.recommendations?.actionableAdvice && insights.recommendations.actionableAdvice.length > 0 && (
                <div className="bg-indigo-50 rounded-lg shadow p-6 border border-indigo-200">
                  <h3 className="text-lg font-bold text-indigo-900 mb-4">Actionable Advice</h3>
                  <ul className="space-y-3">
                    {insights.recommendations.actionableAdvice.map((advice, idx) => (
                      <li key={idx} className="flex items-start space-x-3">
                        <span className="text-indigo-600 font-bold">→</span>
                        <span className="text-indigo-800">{advice}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              )}

              {!insights.recommendations?.budgetAdjustments?.length &&
                !insights.recommendations?.savingsOpportunities?.length &&
                !insights.recommendations?.actionableAdvice?.length && (
                <div className="bg-gray-50 rounded-lg p-6 text-center">
                  <Lightbulb className="h-12 w-12 text-gray-400 mx-auto mb-3" />
                  <p className="text-gray-600">No recommendations available yet.</p>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function RecommendationCard({
  recommendation,
  isReduction = false,
}: {
  recommendation: BudgetRecommendation;
  isReduction?: boolean;
}) {
  return (
    <div className="bg-white rounded-lg p-4 border border-gray-200 hover:shadow-md transition">
      <div className="flex justify-between items-start mb-3">
        <div>
          <h4 className="font-bold text-gray-900">{recommendation.category}</h4>
          <p className="text-sm text-gray-600">{recommendation.reasoning}</p>
        </div>
        <div className={`text-right ${isReduction ? 'text-green-600' : 'text-blue-600'}`}>
          <p className="text-xs font-semibold uppercase opacity-75">Recommended</p>
          <p className="text-lg font-bold">{recommendation.recommendedBudget}</p>
        </div>
      </div>

      <div className="flex justify-between items-center pt-3 border-t">
        <div className="flex items-center space-x-4">
          <div>
            <p className="text-xs text-gray-500">Current</p>
            <p className="font-semibold text-gray-900">{recommendation.currentBudget}</p>
          </div>
          <span className="text-gray-400">→</span>
          <div>
            <p className="text-xs text-gray-500">Recommended</p>
            <p className="font-semibold text-gray-900">{recommendation.recommendedBudget}</p>
          </div>
        </div>
        <div className="text-right">
          <p className="text-xs text-gray-500">Confidence</p>
          <p className={`font-bold ${recommendation.confidence?.includes('100') || recommendation.confidence?.includes('90') ? 'text-green-600' : recommendation.confidence?.includes('80') ? 'text-blue-600' : 'text-amber-600'}`}>
            {recommendation.confidence}
          </p>
        </div>
      </div>
    </div>
  );
}
