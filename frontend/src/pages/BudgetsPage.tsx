import { useState, useEffect } from 'react';
import { Budget, Category } from '@/types';
import BudgetForm from '@/components/BudgetForm';
import BudgetList from '@/components/BudgetList';
import { budgetService } from '@/services/budgetService';
import { expenseService } from '@/services/expenseService';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function BudgetsPage() {
  const navigate = useNavigate();
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [budgetSpent, setBudgetSpent] = useState<Record<number, number>>({});
  const [selectedBudget, setSelectedBudget] = useState<Budget | undefined>();
  const [showForm, setShowForm] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadBudgets();
    loadCategories();
  }, []);

  const loadBudgets = async () => {
    try {
      setIsLoading(true);
      const data = await budgetService.getBudgets();
      setBudgets(data.content);

      // Load spent amounts for each budget
      const spentMap: Record<number, number> = {};
      for (const budget of data.content) {
        try {
          const status = await budgetService.getBudgetStatus(budget.id);
          spentMap[budget.id] = status.spent || 0;
        } catch {
          spentMap[budget.id] = 0;
        }
      }
      setBudgetSpent(spentMap);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load budgets');
    } finally {
      setIsLoading(false);
    }
  };

  const loadCategories = async () => {
    try {
      const data = await expenseService.getCategories();
      setCategories(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load categories');
    }
  };

  const handleSubmit = async (formData: any) => {
    try {
      setIsLoading(true);
      if (selectedBudget) {
        await budgetService.updateBudget(selectedBudget.id, formData);
      } else {
        await budgetService.createBudget(formData);
      }
      setShowForm(false);
      setSelectedBudget(undefined);
      await loadBudgets();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save budget');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      setIsLoading(true);
      await budgetService.deleteBudget(id);
      await loadBudgets();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete budget');
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (budget: Budget) => {
    setSelectedBudget(budget);
    setShowForm(true);
  };

  const handleCancel = () => {
    setShowForm(false);
    setSelectedBudget(undefined);
  };

  return (
    <div className="min-h-screen bg-gray-50 pb-8">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="flex items-center space-x-4">
              <button
                onClick={() => navigate('/dashboard')}
                className="text-gray-600 hover:text-gray-900 transition"
                title="Back to Dashboard"
              >
                <ArrowLeft size={24} />
              </button>
              <h1 className="text-2xl font-bold text-gray-900">Budgets</h1>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
            {error}
          </div>
        )}

        {!showForm ? (
          <button
            onClick={() => setShowForm(true)}
            className="mb-6 bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-lg transition"
          >
            + Add New Budget
          </button>
        ) : null}

        {showForm && (
          <BudgetForm
            budget={selectedBudget}
            categories={categories}
            onSubmit={handleSubmit}
            onCancel={handleCancel}
            isLoading={isLoading}
          />
        )}

        <BudgetList
          budgets={budgets}
          budgetSpent={budgetSpent}
          onEdit={handleEdit}
          onDelete={handleDelete}
          isLoading={isLoading}
        />
      </main>
    </div>
  );
}