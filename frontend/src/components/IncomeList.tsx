import { Income } from '@/types';
import { Trash2, Edit2 } from 'lucide-react';

interface IncomeListProps {
  incomeList: Income[];
  onEdit: (income: Income) => void;
  onDelete: (id: number) => Promise<void>;
  isLoading?: boolean;
}

export default function IncomeList({ incomeList, onEdit, onDelete, isLoading = false }: IncomeListProps) {
  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this income?')) {
      await onDelete(id);
    }
  };

  if (incomeList.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-6 text-center text-gray-500">
        No income entries found. Add one to get started!
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
            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Source</th>
            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Amount</th>
            <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y">
          {incomeList.map(item => (
            <tr key={item.id} className="hover:bg-gray-50">
              <td className="px-6 py-4 text-sm text-gray-900">
                {new Date(item.date).toLocaleDateString()}
              </td>
              <td className="px-6 py-4 text-sm text-gray-900">{item.description}</td>
              <td className="px-6 py-4 text-sm text-gray-600">{item.source}</td>
              <td className="px-6 py-4 text-sm font-semibold text-green-600">
                +${item.amount.toFixed(2)}
              </td>
              <td className="px-6 py-4 text-sm">
                <div className="flex gap-2">
                  <button
                    onClick={() => onEdit(item)}
                    className="text-blue-600 hover:text-blue-800 transition"
                    title="Edit"
                  >
                    <Edit2 size={18} />
                  </button>
                  <button
                    onClick={() => handleDelete(item.id)}
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