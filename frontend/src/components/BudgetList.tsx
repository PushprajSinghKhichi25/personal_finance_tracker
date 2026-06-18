import { Budget } from '@/types';
import { Trash2, Edit2 } from 'lucide-react';

interface BudgetListProps {
  budgets: Budget[];
  budgetSpent: Record<number, number>;
  onEdit: (budget: Budget) => void;
  onDelete: (id: number) => Promise<void>;
  isLoading?: boolean;
}

export default function BudgetList({ budgets, budgetSpent, onEdit, onDelete, isLoading = false }: BudgetListProps) {
  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this budget?')) {
      await onDelete(id);
    }
  };

  const getProgressColor = (spent: number, limit: number) => {
    const percentage = (spent / limit) * 100;
    if (percentage > 100) return 'bg-red-500';
    if (percentage > 80) return 'bg-yellow-500';
    return 'bg-green-500';
  };

  const getStatusText = (spent: number, limit: number) => {
    const remaining = limit - spent;
    if (remaining < 0) return `Over by ₹${Math.abs(remaining).toFixed(2)}`;
    return `₹${remaining.toFixed(2)} remaining`;
  };

  if (budgets.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-6 text-center text-gray-500">
        No budgets found. Add one to get started!
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {budgets.map(budget => {
        const spent = budgetSpent[budget.id] || 0;
        const percentage = Math.min((spent / budget.monthlyLimit) * 100, 100);

        return (
          <div key={budget.id} className="bg-white rounded-lg shadow p-6">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{budget.category.name}</h3>
                <p className="text-sm text-gray-600">Month: {budget.currentMonth}</p>
              </div>
              <div className="flex gap-2">
                <button
                  onClick={() => onEdit(budget)}
                  className="text-blue-600 hover:text-blue-800 transition"
                  title="Edit"
                >
                  <Edit2 size={18} />
                </button>
                <button
                  onClick={() => handleDelete(budget.id)}
                  disabled={isLoading}
                  className="text-red-600 hover:text-red-800 transition disabled:opacity-50"
                  title="Delete"
                >
                  <Trash2 size={18} />
                </button>
              </div>
            </div>

            <div className="mb-2">
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div
                  className={`h-2 rounded-full transition-all ${getProgressColor(spent, budget.monthlyLimit)}`}
                  style={{ width: `${percentage}%` }}
                ></div>
              </div>
            </div>

            <div className="flex justify-between items-center text-sm">
              <div>
                <span className="font-semibold text-gray-900">
                  ₹{spent.toFixed(2)} / ${budget.monthlyLimit.toFixed(2)}
                </span>
              </div>
              <div className={`text-sm font-medium ${spent > budget.monthlyLimit ? 'text-red-600' : 'text-green-600'}`}>
                {getStatusText(spent, budget.monthlyLimit)}
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}