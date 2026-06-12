import { useState, useEffect } from 'react';
import { Income } from '@/types';
import IncomeForm from '@/components/IncomeForm';
import IncomeList from '@/components/IncomeList';
import { incomeService } from '@/services/incomeService';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function IncomePage() {
  const navigate = useNavigate();
  const [incomeList, setIncomeList] = useState<Income[]>([]);
  const [selectedIncome, setSelectedIncome] = useState<Income | undefined>();
  const [showForm, setShowForm] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadIncome();
  }, []);

  const loadIncome = async () => {
    try {
      setIsLoading(true);
      const data = await incomeService.getIncome();
      setIncomeList(data.content);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load income');
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (formData: any) => {
    try {
      setIsLoading(true);
      if (selectedIncome) {
        await incomeService.updateIncome(selectedIncome.id, formData);
      } else {
        await incomeService.createIncome(formData);
      }
      setShowForm(false);
      setSelectedIncome(undefined);
      await loadIncome();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to save income');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      setIsLoading(true);
      await incomeService.deleteIncome(id);
      await loadIncome();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete income');
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (income: Income) => {
    setSelectedIncome(income);
    setShowForm(true);
  };

  const handleCancel = () => {
    setShowForm(false);
    setSelectedIncome(undefined);
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
              <h1 className="text-2xl font-bold text-gray-900">Income</h1>
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
            className="mb-6 bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg transition"
          >
            + Add New Income
          </button>
        ) : null}

        {showForm && (
          <IncomeForm
            income={selectedIncome}
            onSubmit={handleSubmit}
            onCancel={handleCancel}
            isLoading={isLoading}
          />
        )}

        <IncomeList
          incomeList={incomeList}
          onEdit={handleEdit}
          onDelete={handleDelete}
          isLoading={isLoading}
        />
      </main>
    </div>
  );
}
