import { useAuthStore } from '@/store/authStore';
import { useNavigate, useLocation } from 'react-router-dom';
import { LogOut, BarChart3, Settings } from 'lucide-react';

export default function Navbar() {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path: string) => location.pathname === path;

  const navLinks = [
    { path: '/dashboard', label: 'Dashboard', icon: '📊' },
    { path: '/expenses', label: 'Expenses', icon: '💸' },
    { path: '/income', label: 'Income', icon: '💰' },
    { path: '/budgets', label: 'Budgets', icon: '📋' },
    { path: '/categories', label: 'Categories', icon: '🏷️' },
    { path: '/ai-insights', label: 'AI Insights', icon: '🤖' },
    { path: '/settings', label: 'Settings', icon: '⚙️' },
  ];

  return (
    <nav className="bg-white shadow">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16">

          <div className="flex items-center space-x-2">
            <BarChart3 className="h-8 w-8 text-blue-600" />
            <h1
              onClick={() => navigate('/dashboard')}
              className="text-2xl font-bold text-gray-900 cursor-pointer hover:text-blue-600 transition"
            >
              Finance Tracker
            </h1>
          </div>

          //{ Navigation Links }
          <div className="hidden md:flex items-center space-x-1">
            {navLinks.map((link) => (
              <button
                key={link.path}
                onClick={() => navigate(link.path)}
                className={`px-3 py-2 rounded-md text-sm font-medium transition ${
                  isActive(link.path)
                    ? 'bg-blue-600 text-white'
                    : 'text-gray-700 hover:bg-gray-100'
                }`}
              >
                <span className="mr-1">{link.icon}</span>
                {link.label}
              </button>
            ))}
          </div>

          //{ User Menu }
          <div className="flex items-center space-x-4">
            <span className="text-gray-700 text-sm font-medium">{user?.username}</span>
            <button
              onClick={handleLogout}
              className="flex items-center space-x-2 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg transition font-semibold"
            >
              <LogOut size={20} />
              <span className="hidden sm:inline">Logout</span>
            </button>
          </div>
        </div>

        //{ Mobile Navigation }
        <div className="md:hidden flex overflow-x-auto pb-2 space-x-2">
          {navLinks.map((link) => (
            <button
              key={link.path}
              onClick={() => navigate(link.path)}
              className={`px-3 py-2 rounded-md text-xs font-medium whitespace-nowrap transition ${
                isActive(link.path)
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              {link.icon} {link.label}
            </button>
          ))}
        </div>
      </div>
    </nav>
  );
}