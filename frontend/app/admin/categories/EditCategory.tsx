import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { fetchCategory, updateCategory, clearCurrentCategory } from "../../store/slices/categoriesSlice";
import { toast } from "react-hot-toast";

export function EditCategory() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { currentCategory, isLoading, error } = useAppSelector((state) => state.categories);
  
  const [formData, setFormData] = useState({
    name: "",
    description: "",
  });

  useEffect(() => {
    if (id) {
      dispatch(fetchCategory(Number(id)));
    }
    return () => {
      dispatch(clearCurrentCategory());
    };
  }, [dispatch, id]);

  useEffect(() => {
    if (currentCategory) {
      setFormData({
        name: currentCategory.name,
        description: currentCategory.description,
      });
    }
  }, [currentCategory]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    try {
      await dispatch(updateCategory({ id: Number(id), payload: formData })).unwrap();
      toast.success("Category updated successfully");
      navigate("/admin/categories");
    } catch (err: any) {
      toast.error(err || "Failed to update category");
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  if (isLoading && !currentCategory) {
    return (
      <AdminLayout title="Edit Category">
        <div className="flex justify-center p-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout title="Edit Blog Category">
      <div className="max-w-2xl mx-auto">
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
          <div className="p-6 border-b border-gray-100 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-900/50">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">Edit Category</h3>
            <p className="text-sm text-gray-500">Update the category details.</p>
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
                {isLoading ? "Saving..." : "Update Category"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </AdminLayout>
  );
}
