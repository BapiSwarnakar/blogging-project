import { useState } from "react";
import { useNavigate } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";

export function CreateRolePage() {
  const navigate = useNavigate();
  const [roleName, setRoleName] = useState("");
  const [description, setDescription] = useState("");
  
  // Mock permissions list for selection
  const availablePermissions = [
    { code: "users.view", label: "View Users" },
    { code: "users.create", label: "Create Users" },
    { code: "users.edit", label: "Edit Users" },
    { code: "users.delete", label: "Delete Users" },
    { code: "posts.view", label: "View Posts" },
    { code: "posts.create", label: "Create Posts" },
  ];

  const [selectedPermissions, setSelectedPermissions] = useState<string[]>([]);

  const handlePermissionToggle = (code: string) => {
    setSelectedPermissions(prev => 
      prev.includes(code) 
        ? prev.filter(c => c !== code) 
        : [...prev, code]
    );
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log("Creating role:", { roleName, description, selectedPermissions });
    // In a real app, you'd call your API here
    navigate("/admin/roles");
  };

  return (
    <AdminLayout title="Create New Role">
      <form onSubmit={handleSubmit} className="max-w-4xl mx-auto space-y-8">
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
          <div className="p-6 border-b border-gray-100 dark:border-gray-700">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">Role Information</h3>
            <p className="text-sm text-gray-500">Define the basic details of the role.</p>
          </div>
          <div className="p-6 space-y-6">
            <div className="grid grid-cols-1 gap-6">
              <div>
                <label htmlFor="roleName" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Role Name
                </label>
                <input
                  id="roleName"
                  type="text"
                  required
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  value={roleName}
                  onChange={(e) => setRoleName(e.target.value)}
                  placeholder="e.g. Content Manager"
                />
              </div>
              <div>
                <label htmlFor="description" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Description
                </label>
                <textarea
                  id="description"
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all min-h-[100px]"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="What can this role do?"
                />
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
          <div className="p-6 border-b border-gray-100 dark:border-gray-700">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">Permissions</h3>
            <p className="text-sm text-gray-500">Assign specific permissions to this role.</p>
          </div>
          <div className="p-6">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {availablePermissions.map((permission) => (
                <label
                  key={permission.code}
                  className={`flex items-center p-4 rounded-xl border-2 cursor-pointer transition-all ${
                    selectedPermissions.includes(permission.code)
                      ? "border-blue-500 bg-blue-50 dark:bg-blue-900/20"
                      : "border-gray-100 dark:border-gray-700 hover:border-blue-200 dark:hover:border-gray-600"
                  }`}
                >
                  <input
                    type="checkbox"
                    className="hidden"
                    checked={selectedPermissions.includes(permission.code)}
                    onChange={() => handlePermissionToggle(permission.code)}
                  />
                  <div className="flex-1">
                    <span className={`block font-semibold ${
                      selectedPermissions.includes(permission.code) ? "text-blue-700 dark:text-blue-400" : "text-gray-900 dark:text-white"
                    }`}>
                      {permission.label}
                    </span>
                    <span className="text-xs text-gray-500 font-mono">
                      {permission.code}
                    </span>
                  </div>
                  {selectedPermissions.includes(permission.code) && (
                    <svg className="w-5 h-5 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                  )}
                </label>
              ))}
            </div>
          </div>
        </div>

        <div className="flex justify-end space-x-4 pt-4">
          <button
            type="button"
            onClick={() => navigate("/admin/roles")}
            className="px-6 py-2 border border-gray-300 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all"
          >
            Create Role
          </button>
        </div>
      </form>
    </AdminLayout>
  );
}
