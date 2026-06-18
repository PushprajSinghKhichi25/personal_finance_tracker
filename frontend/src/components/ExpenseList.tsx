import { Expense } from '@/types';
import { Trash2, Edit2 } from 'lucide-react';

interface ExpenseListProps {
  expenses: Expense[];
  onEdit: (expense: Expense) => void;
  onDelete: (id: number) => Promise<void>;
  isLoading?: boolean;
}

export default function ExpenseList({ expenses, onEdit, onDelete, isLoading = false }: ExpenseListProps) {
  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this expense?')) {
      await onDelete(id);
    }
  };

  if (expenses.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-6 text-center text-gray-500">
        No expenses found. Add one to get started!
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow overflow-x-auto">
      <table className="w-full">
        <thead className="bg-gray-50 border-b">
          <tr>
            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Date</th>
            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Description</th>
            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Categories</th>
            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Amount</th>
            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y">
          {expenses.map(expense => (
            <tr key={expense.id} className="hover:bg-gray-50">
              <td className="px-6 py-4 text-sm text-gray-900">
                {new Date(expense.date).toLocaleDateString()}
              </td>
              <td className="px-6 py-4 text-sm text-gray-900">{expense.description}</td>
              <td className="px-6 py-4 text-sm">
                <div className="flex flex-wrap gap-1">
                  {expense.categories.map(cat => (
                    <span key={cat.id} className="inline-block bg-blue-100 text-blue-800 px-2 py-1 rounded text-xs">
                      {cat.name}
                    </span>
                  ))}
                </div>
              </td>
              <td className="px-6 py-4 text-sm font-semibold text-gray-900">
                ₹{expense.amount.toFixed(2)}
              </td>
              <td className="px-6 py-4 text-sm">
                <div className="flex gap-2">
                  <button
                    onClick={() => onEdit(expense)}
                    className="text-blue-600 hover:text-blue-800 transition"
                    title="Edit"
                  >
                    <Edit2 size={18} />
                  </button>
                  <button
                    onClick={() => handleDelete(expense.id)}
                    disabled={isLoading}
                    className="text-red-600 hover:text-red-800 transition disabled:opacity-50"
                    title="Delete"
                  >
                    <Trash2 size={18} />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}