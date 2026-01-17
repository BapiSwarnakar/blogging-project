import { useState } from "react";
import { useNavigate } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { createCategory } from "../../store/slices/categoriesSlice";
import { toast } from "react-hot-toast";

export function CreateCategory() {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { isLoading, error } = useAppSelector((state) => state.categories);
  
  const [formData, setFormData] = useState({
    name: "",
    description: "",
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await dispatch(createCategory(formData)).unwrap();
      toast.success("Category created successfully");
      navigate("/admin/categories");
    } catch (err: any) {
      toast.error(err || "Failed to create category");
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <AdminLayout title="Create Blog Category">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
          <div className="p-6 border-b border-gray-100 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-900/50">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">New Category</h3>
            <p className="text-sm text-gray-500">Add a new category for the blog posts.</p>
          </div>
          <form onSubmit={handleSubmit} className="p-6 space-y-6">
            {error && (
              <div className="p-3 bg-red-50 border border-red-200 text-red-600 rounded-lg text-sm">
                {error}
              </div>
            )}
            
            <div>
              <label htmlFor="catName" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                 Category Name
              </label>
              <input
                id="catName"
                type="text"
                name="name"
                required
                placeholder="e.g. Technology"
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                value={formData.name}
                onChange={handleChange}
              />
            </div>

            <div>
              <label htmlFor="catDesc" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Description
              </label>
              <textarea
                id="catDesc"
                name="description"
                placeholder="Briefly describe what this category covers..."
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all min-h-[120px]"
                value={formData.description}
                onChange={handleChange}
              />
            </div>

            <div className="flex justify-end space-x-4 pt-4 border-t border-gray-100 dark:border-gray-700">
              <button
                type="button"
                onClick={() => navigate("/admin/categories")}
                className="px-6 py-2 border border-gray-300 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isLoading}
                className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all font-medium disabled:opacity-50"
              >
                {isLoading ? "Saving..." : "Save Category"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AdminLayout>
  );
}
