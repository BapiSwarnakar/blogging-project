import { useState } from "react";
import { useNavigate } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";

export function CreatePermissionPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    code: "",
    module: "",
    description: ""
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log("Creating permission:", formData);
    navigate("/admin/permissions");
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <AdminLayout title="Create Permission">
      <div className="max-w-3xl mx-auto">
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
          <div className="p-6 border-b border-gray-100 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-900/50">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">New Permission</h3>
            <p className="text-sm text-gray-500">Add a new granular permission code to the system.</p>
          </div>
          <form onSubmit={handleSubmit} className="p-6 space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div>
                <label htmlFor="permName" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Permission Name
                </label>
                <input
                  id="permName"
                  type="text"
                  name="name"
                  required
                  placeholder="e.g. Export Users"
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  value={formData.name}
                  onChange={handleChange}
                />
              </div>
              <div>
                <label htmlFor="permCode" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Permission Code
                </label>
                <input
                  id="permCode"
                  type="text"
                  name="code"
                  required
                  placeholder="e.g. users.export"
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 font-mono text-sm outline-none transition-all"
                  value={formData.code}
                  onChange={handleChange}
                />
              </div>
              <div>
                <label htmlFor="permModule" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Module
                </label>
                <select
                  id="permModule"
                  name="module"
                  required
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  value={formData.module}
                  onChange={handleChange}
                >
                  <option value="">Select a module</option>
                  <option value="Users">Users</option>
                  <option value="Posts">Posts</option>
                  <option value="Comments">Comments</option>
                  <option value="Settings">Settings</option>
                  <option value="Analytics">Analytics</option>
                </select>
              </div>
            </div>

            <div>
              <label htmlFor="permDesc" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Description
              </label>
              <textarea
                id="permDesc"
                name="description"
                placeholder="What exactly does this permission allow?"
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all min-h-[120px]"
                value={formData.description}
                onChange={handleChange}
              />
            </div>

            <div className="flex justify-end space-x-4 pt-4 border-t border-gray-100 dark:border-gray-700">
              <button
                type="button"
                onClick={() => navigate("/admin/permissions")}
                className="px-6 py-2 border border-gray-300 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all font-medium"
              >
                Save Permission
              </button>
            </div>
          </form>
        </div>
      </div>
    </AdminLayout>
  );
}
