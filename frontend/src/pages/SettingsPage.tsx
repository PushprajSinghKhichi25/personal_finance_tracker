import { useState, useEffect } from 'react';
import { useAuthStore } from '@/store/authStore';
import Navbar from '@/components/Navbar';
import { Settings, Save, AlertCircle } from 'lucide-react';

interface AIPreferences {
  enableAIInsights: boolean;
  insightFrequency: 'weekly' | 'monthly';
  autoApplyRecommendations: boolean;
  emailDigest: boolean;
  emailFrequency: 'weekly' | 'monthly';
}

export default function SettingsPage() {
  const user = useAuthStore((state) => state.user);
  const [preferences, setPreferences] = useState<AIPreferences>({
    enableAIInsights: true,
    insightFrequency: 'monthly',
    autoApplyRecommendations: false,
    emailDigest: false,
    emailFrequency: 'monthly',
  });
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  useEffect(() => {
    loadPreferences();
  }, []);

  const loadPreferences = async () => {
    try {
      const stored = localStorage.getItem(`ai-preferences-${user?.id}`);
      if (stored) {
        setPreferences(JSON.parse(stored));
      }
    } catch (err) {
      console.error('Failed to load preferences:', err);
    }
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      localStorage.setItem(`ai-preferences-${user?.id}`, JSON.stringify(preferences));
      setMessage({ type: 'success', text: 'Settings saved successfully!' });
      setTimeout(() => setMessage(null), 3000);
    } catch (err) {
      console.error('Failed to save preferences:', err);
      setMessage({ type: 'error', text: 'Failed to save settings. Please try again.' });
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <main className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        //{ Header }
        <div className="mb-8">
          <div className="flex items-center space-x-3 mb-2">
            <Settings className="h-8 w-8 text-gray-900" />
            <h1 className="text-3xl font-bold text-gray-900">Settings</h1>
          </div>
          <p className="text-gray-600">Manage your AI insights and preferences</p>
        </div>

        //{ Success/Error Message }
        {message && (
          <div className={`mb-6 p-4 rounded-lg flex items-center space-x-3 ${
            message.type === 'success'
              ? 'bg-green-50 border border-green-200'
              : 'bg-red-50 border border-red-200'
          }`}>
            <AlertCircle className={`h-5 w-5 ${
              message.type === 'success' ? 'text-green-600' : 'text-red-600'
            }`} />
            <p className={message.type === 'success' ? 'text-green-800' : 'text-red-800'}>
              {message.text}
            </p>
          </div>
        )}

       // { Settings Sections }
        <div className="space-y-6">
          //{ AI Insights Section }
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-4">AI Insights</h2>

            <div className="space-y-4">
             // { Enable AI Insights }
              <div className="flex items-center justify-between py-3 border-b">
                <div>
                  <p className="font-semibold text-gray-900">Enable AI Insights</p>
                  <p className="text-sm text-gray-600 mt-1">Get AI-powered spending insights and budget recommendations</p>
                </div>
                <label className="flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={preferences.enableAIInsights}
                    onChange={(e) => setPreferences({
                      ...preferences,
                      enableAIInsights: e.target.checked
                    })}
                    className="w-5 h-5 text-blue-600 rounded cursor-pointer"
                  />
                </label>
              </div>

              //{ Insight Frequency }
              {preferences.enableAIInsights && (
                <div className="py-3 border-b">
                  <p className="font-semibold text-gray-900 mb-3">Insight Frequency</p>
                  <div className="space-y-2">
                    <label className="flex items-center space-x-3 cursor-pointer">
                      <input
                        type="radio"
                        name="frequency"
                        value="weekly"
                        checked={preferences.insightFrequency === 'weekly'}
                        onChange={(e) => setPreferences({
                          ...preferences,
                          insightFrequency: e.target.value as 'weekly' | 'monthly'
                        })}
                        className="w-4 h-4 text-blue-600"
                      />
                      <span className="text-gray-700">Weekly - Get insights every Monday</span>
                    </label>
                    <label className="flex items-center space-x-3 cursor-pointer">
                      <input
                        type="radio"
                        name="frequency"
                        value="monthly"
                        checked={preferences.insightFrequency === 'monthly'}
                        onChange={(e) => setPreferences({
                          ...preferences,
                          insightFrequency: e.target.value as 'weekly' | 'monthly'
                        })}
                        className="w-4 h-4 text-blue-600"
                      />
                      <span className="text-gray-700">Monthly - Get insights at the end of each month</span>
                    </label>
                  </div>
                </div>
              )}
            </div>
          </div>

          //{ Recommendations Section }
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Budget Recommendations</h2>

            <div className="space-y-4">
             // { Auto-Apply Recommendations }
              <div className="flex items-center justify-between py-3 border-b">
                <div>
                  <p className="font-semibold text-gray-900">Auto-Apply Recommendations</p>
                  <p className="text-sm text-gray-600 mt-1">Automatically apply recommended budget changes</p>
                </div>
                <label className="flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={preferences.autoApplyRecommendations}
                    onChange={(e) => setPreferences({
                      ...preferences,
                      autoApplyRecommendations: e.target.checked
                    })}
                    className="w-5 h-5 text-blue-600 rounded cursor-pointer"
                  />
                </label>
              </div>
            </div>
          </div>

          //{ Email Notifications Section }
          <div className="bg-white rounded-lg shadow p-6">
            <h2 className="text-xl font-bold text-gray-900 mb-4">Email Notifications</h2>

            <div className="space-y-4">
              //{ Email Digest }
              <div className="flex items-center justify-between py-3 border-b">
                <div>
                  <p className="font-semibold text-gray-900">Email Digest</p>
                  <p className="text-sm text-gray-600 mt-1">Receive insights and recommendations via email</p>
                </div>
                <label className="flex items-center cursor-pointer">
                  <input
                    type="checkbox"
                    checked={preferences.emailDigest}
                    onChange={(e) => setPreferences({
                      ...preferences,
                      emailDigest: e.target.checked
                    })}
                    className="w-5 h-5 text-blue-600 rounded cursor-pointer"
                  />
                </label>
              </div>

              //{ Email Frequency }
              {preferences.emailDigest && (
                <div className="py-3 border-b">
                  <p className="font-semibold text-gray-900 mb-3">Email Frequency</p>
                  <div className="space-y-2">
                    <label className="flex items-center space-x-3 cursor-pointer">
                      <input
                        type="radio"
                        name="email-frequency"
                        value="weekly"
                        checked={preferences.emailFrequency === 'weekly'}
                        onChange={(e) => setPreferences({
                          ...preferences,
                          emailFrequency: e.target.value as 'weekly' | 'monthly'
                        })}
                        className="w-4 h-4 text-blue-600"
                      />
                      <span className="text-gray-700">Weekly - Every Monday</span>
                    </label>
                    <label className="flex items-center space-x-3 cursor-pointer">
                      <input
                        type="radio"
                        name="email-frequency"
                        value="monthly"
                        checked={preferences.emailFrequency === 'monthly'}
                        onChange={(e) => setPreferences({
                          ...preferences,
                          emailFrequency: e.target.value as 'weekly' | 'monthly'
                        })}
                        className="w-4 h-4 text-blue-600"
                      />
                      <span className="text-gray-700">Monthly - End of month</span>
                    </label>
                  </div>
                </div>
              )}
            </div>
          </div>

          //{ Save Button }
          <div className="bg-white rounded-lg shadow p-6 flex justify-end space-x-4">
            <button
              onClick={() => loadPreferences()}
              className="px-6 py-2 border border-gray-300 text-gray-700 font-semibold rounded-lg hover:bg-gray-50 transition"
            >
              Reset
            </button>
            <button
              onClick={handleSave}
              disabled={saving}
              className="flex items-center space-x-2 px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition disabled:bg-gray-400"
            >
              <Save size={20} />
              <span>{saving ? 'Saving...' : 'Save Settings'}</span>
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}