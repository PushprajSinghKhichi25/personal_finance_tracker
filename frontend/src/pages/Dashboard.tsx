import { useState, useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import { useNavigate } from 'react-router-dom';
import Navbar from '@/components/Navbar';
import AIInsightsCard from '@/components/AIInsightsCard';
import { expenseService } from '@/services/expenseService';
import { incomeService } from '@/services/incomeService';

export default function Dashboard() {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const navigate = useNavigate();
  const [totalIncome, setTotalIncome] = useState(0);
  const [totalExpenses, setTotalExpenses] = useState(0);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    loadSummary();
  }, []);

  const loadSummary = async () => {
    try {
      setIsLoading(true);
      const currentMonth = new Date().toISOString().slice(0, 7);

      // Load all expenses for this month
      const expensesData = await expenseService.getExpenses(0, 100);
      const monthExpenses = expensesData.content
        .filter(e => e.date.startsWith(currentMonth))
        .reduce((sum, e) => sum + e.amount, 0);
      setTotalExpenses(monthExpenses);

      // Load all income for this month
      const incomeData = await incomeService.getIncome(0, 100);
      const monthIncome = incomeData.content
        .filter(i => i.date.startsWith(currentMonth))
        .reduce((sum, i) => sum + i.amount, 0);
      setTotalIncome(monthIncome);
    } catch (err) {
      console.error('Failed to load summary:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const balance = totalIncome - totalExpenses;

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-gray-500 text-sm font-semibold uppercase">Total Balance</h2>
            <p className={`text-3xl font-bold mt-2 ${balance >= 0 ? 'text-green-600' : 'text-red-600'}`}>
              ₹{balance.toFixed(2)}
            </p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-gray-500 text-sm font-semibold uppercase">This Month Income</h2>
            <p className="text-3xl font-bold text-green-600 mt-2">₹{totalIncome.toFixed(2)}</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-gray-500 text-sm font-semibold uppercase">This Month Expenses</h2>
            <p className="text-3xl font-bold text-red-600 mt-2">₹{totalExpenses.toFixed(2)}</p>
          </div>
        </div>

        <div className="mb-8">
          <AIInsightsCard onViewFull={() => navigate('/ai-insights')} />
        </div>

        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-xl font-bold text-gray-900 mb-6">Quick Actions</h2>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <button
              onClick={() => navigate('/expenses')}
              className="bg-blue-600 hover:bg-blue-700 text-white px-6 py-3 rounded-lg transition font-semibold"
            >
              Manage Expenses
            </button>
            <button
              onClick={() => navigate('/income')}
              className="bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-lg transition font-semibold"
            >
              Manage Income
            </button>
            <button
              onClick={() => navigate('/budgets')}
              className="bg-purple-600 hover:bg-purple-700 text-white px-6 py-3 rounded-lg transition font-semibold"
            >
              Manage Budgets
            </button>
            <button
              onClick={() => navigate('/categories')}
              className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-lg transition font-semibold"
            >
              Manage Categories
            </button>
          </div>
          <div className="mt-4 pt-4 border-t">
            <button
              onClick={() => navigate('/ai-insights')}
              className="w-full bg-gradient-to-r from-cyan-500 to-blue-600 hover:from-cyan-600 hover:to-blue-700 text-white px-6 py-3 rounded-lg transition font-semibold"
            >
              View AI Insights & Recommendations
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}