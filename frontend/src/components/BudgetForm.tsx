import { useState, useEffect } from 'react';
import { Budget, Category } from '@/types';

interface BudgetFormProps {
  budget?: Budget;
  categories: Category[];
  onSubmit: (data: any) => Promise<void>;
  onCancel: () => void;
  isLoading?: boolean;
}

export default function BudgetForm({ budget, categories, onSubmit, onCancel, isLoading = false }: BudgetFormProps) {
  const [formData, setFormData] = useState({
    categoryId: 0,
    monthlyLimit: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    if (budget) {
      setFormData({
        categoryId: budget.category.id,
        monthlyLimit: budget.monthlyLimit.toString(),
      });
    }
  }, [budget]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (!formData.categoryId || !formData.monthlyLimit) {
      setError('Please fill in all fields');
      return;
    }

    try {
      await onSubmit({
        ...formData,
        monthlyLimit: parseFloat(formData.monthlyLimit),
      });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save budget');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow p-6 mb-6">
      <h2 className="text-xl font-bold text-gray-900 mb-4">
        {budget ? 'Edit Budget' : 'Add New Budget'}
      </h2>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Category
          </label>
          <select
            value={formData.categoryId}
            onChange={(e) => setFormData(prev => ({ ...prev, categoryId: parseInt(e.target.value) }))}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          >
            <option value={0}>Select a category</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>
                {cat.name}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Monthly Limit
          </label>
          <input
            type="number"
            step="0.01"
            value={formData.monthlyLimit}
            onChange={(e) => setFormData(prev => ({ ...prev, monthlyLimit: e.target.value }))}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            placeholder="0.00"
          />
        </div>
      </div>

      <div className="flex gap-4">
        <button
          type="submit"
          disabled={isLoading}
          className="bg-purple-600 hover:bg-purple-700 text-white px-4 py-2 rounded-lg transition disabled:opacity-50"
        >
          {isLoading ? 'Saving...' : budget ? 'Update' : 'Add'} Budget
        </button>
        <button
          type="button"
          onClick={onCancel}
          className="bg-gray-300 hover:bg-gray-400 text-gray-800 px-4 py-2 rounded-lg transition"
        >
          Cancel
        </button>
      </div>
    </form>
  );
}