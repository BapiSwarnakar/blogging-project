import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { fetchPermission, updatePermission, clearCurrentPermission } from "../../store/slices/permissionsSlice";
import { toast } from "react-hot-toast";

export function EditPermissionPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { currentPermission, isLoading, error } = useAppSelector((state) => state.permissions);
  
  const [formData, setFormData] = useState({
    name: "",
    slug: "",
    category: "",
    description: "",
    apiUrl: "",
    apiMethod: "GET"
  });

  useEffect(() => {
    if (id) {
      dispatch(fetchPermission(Number(id)));
    }
    return () => {
      dispatch(clearCurrentPermission());
    };
  }, [dispatch, id]);

  useEffect(() => {
    if (currentPermission) {
      setFormData({
        name: currentPermission.name,
        slug: currentPermission.slug,
        category: currentPermission.category,
        description: currentPermission.description,
        apiUrl: currentPermission.apiUrl,
        apiMethod: currentPermission.apiMethod
      });
    }
  }, [currentPermission]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    try {
      await dispatch(updatePermission({ id: Number(id), payload: formData })).unwrap();
      toast.success("Permission updated successfully");
      navigate("/admin/permissions");
    } catch (err: any) {
      toast.error(err || "Failed to update permission");
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  if (isLoading && !currentPermission) {
    return (
      <AdminLayout title="Edit Permission">
        <div className="flex justify-center p-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout title="Edit Permission">
      <div className="max-w-3xl mx-auto">
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
          <div className="p-6 border-b border-gray-100 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-900/50">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">Edit Permission</h3>
            <p className="text-sm text-gray-500">Update granular permission details.</p>
          </div>
          <form onSubmit={handleSubmit} className="p-6 space-y-6">
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-600 rounded-lg text-sm">
                {error}
              </div>
            )}
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
                <label htmlFor="permSlug" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                   Permission Slug (Code)
                </label>
                <input
                  id="permSlug"
                  type="text"
                  name="slug"
                  required
                  placeholder="e.g. USER_EXPORT"
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 font-mono text-sm outline-none transition-all"
                  value={formData.slug}
                  onChange={handleChange}
                />
              </div>
              <div>
                <label htmlFor="permCategory" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Category
                </label>
                <select
                  id="permCategory"
                  name="category"
                  required
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  value={formData.category}
                  onChange={handleChange}
                >
                  <option value="">Select a category</option>
                  <option value="Users">Users</option>
                  <option value="Roles">Roles</option>
                  <option value="Permissions">Permissions</option>
                  <option value="Posts">Posts</option>
                  <option value="Comments">Comments</option>
                  <option value="Settings">Settings</option>
                </select>
              </div>
              <div>
                <label htmlFor="apiMethod" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  API Method
                </label>
                <select
                  id="apiMethod"
                  name="apiMethod"
                  required
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  value={formData.apiMethod}
                  onChange={handleChange}
                >
                  <option value="GET">GET</option>
                  <option value="POST">POST</option>
                  <option value="PUT">PUT</option>
                  <option value="DELETE">DELETE</option>
                  <option value="PATCH">PATCH</option>
                </select>
              </div>
              <div className="md:col-span-2">
                <label htmlFor="apiUrl" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  API URL
                </label>
                <input
                  id="apiUrl"
                  type="text"
                  name="apiUrl"
                  required
                  placeholder="e.g. /api/v1/auth/users/export"
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  value={formData.apiUrl}
                  onChange={handleChange}
                />
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
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all min-h-[100px]"
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
                disabled={isLoading}
                className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all font-medium disabled:opacity-50"
              >
                {isLoading ? "Saving..." : "Update Permission"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AdminLayout>
  );
}
