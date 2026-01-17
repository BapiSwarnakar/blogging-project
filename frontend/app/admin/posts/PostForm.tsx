import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { fetchCategories } from "../../store/slices/categoriesSlice";
import { createPost, updatePost } from "../../store/slices/postsSlice";
import type { PostRequest, Post } from "../../store/slices/postsSlice";
import Select from "react-select";
import { toast } from "react-hot-toast";
import { customSelectStyles } from "../../utils/selectStyles";

interface PostFormProps {
  initialData?: Post;
  isEdit?: boolean;
}

export function PostForm({ initialData, isEdit = false }: PostFormProps) {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { categories } = useAppSelector((state) => state.categories);
  const { isLoading } = useAppSelector((state) => state.posts);

  const [formData, setFormData] = useState({
    title: initialData?.title || "",
    excerpt: initialData?.excerpt || "",
    content: initialData?.content || "",
    selectedCategory: initialData?.category ? { value: initialData.category.id, label: initialData.category.name } : null,
    image: initialData?.image || "",
    type: (initialData?.type || "PUBLIC") as "PUBLIC" | "PRIVATE",
  });

  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  const categoryOptions = categories.map((cat) => ({
    value: cat.id,
    label: cat.name,
  }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.selectedCategory) {
      toast.error("Please select a category");
      return;
    }

    const payload: PostRequest = {
      title: formData.title,
      excerpt: formData.excerpt,
      content: formData.content,
      categoryId: formData.selectedCategory.value,
      image: formData.image,
      type: formData.type,
    };

    try {
      if (isEdit && initialData) {
        const resultAction = await dispatch(updatePost({ id: initialData.id, payload }));
        if (updatePost.fulfilled.match(resultAction)) {
          toast.success("Post updated successfully");
          navigate("/admin/posts");
        } else {
          toast.error((resultAction.payload as string) || "Failed to update post");
        }
      } else {
        const resultAction = await dispatch(createPost(payload));
        if (createPost.fulfilled.match(resultAction)) {
          toast.success("Post created successfully");
          navigate("/admin/posts");
        } else {
          toast.error((resultAction.payload as string) || "Failed to create post");
        }
      }
    } catch (err) {
      toast.error("An unexpected error occurred");
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  return (
    <AdminLayout title={isEdit ? "Edit Post" : "Create New Post"}>
      <div className="max-w-5xl mx-auto px-4">
        <div className="flex items-center gap-4 mb-8 pb-6 border-b border-gray-100 dark:border-gray-800">
          <Link
            to="/admin/posts"
            className="group p-2.5 rounded-xl border border-gray-200 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-800 transition-all shadow-sm"
          >
            <svg className="w-5 h-5 text-gray-500 group-hover:text-blue-600 dark:text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
            </svg>
          </Link>
          <div>
            <h1 className="text-3xl font-extrabold text-gray-900 dark:text-white tracking-tight">{isEdit ? "Edit Post" : "Create Post"}</h1>
            <p className="text-gray-500 dark:text-gray-400 mt-1">{isEdit ? "Update your post content and settings" : "Share something new with the community"}</p>
          </div>
        </div>

        <form onSubmit={handleSubmit} className="grid grid-cols-1 lg:grid-cols-3 gap-8 pb-16">
          <div className="lg:col-span-2 space-y-8">
            {/* Main Content Card */}
            <div className="bg-white dark:bg-gray-900 rounded-3xl p-8 border border-gray-200 dark:border-gray-800 shadow-sm">
              <div className="space-y-6">
                <div className="space-y-2">
                  <label htmlFor="title" className="text-sm font-semibold text-gray-700 dark:text-gray-300">Post Title</label>
                  <input
                    type="text"
                    id="title"
                    name="title"
                    required
                    value={formData.title}
                    onChange={handleChange}
                    className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50 text-gray-900 dark:text-white text-lg font-bold focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 outline-none transition-all"
                    placeholder="Enter a catchy title..."
                  />
                </div>

                <div className="space-y-2">
                  <label htmlFor="excerpt" className="text-sm font-semibold text-gray-700 dark:text-gray-300">Excerpt / Short Description</label>
                  <textarea
                    id="excerpt"
                    name="excerpt"
                    rows={3}
                    value={formData.excerpt}
                    onChange={handleChange}
                    className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50 text-gray-900 dark:text-white focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 outline-none transition-all resize-none"
                    placeholder="Briefly describe what this post is about..."
                  />
                </div>

                <div className="space-y-2">
                  <label htmlFor="content" className="text-sm font-semibold text-gray-700 dark:text-gray-300">Main Content</label>
                  <textarea
                    id="content"
                    name="content"
                    required
                    rows={15}
                    value={formData.content}
                    onChange={handleChange}
                    className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50 text-gray-900 dark:text-white focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 outline-none transition-all leading-relaxed"
                    placeholder="Write the full post content here..."
                  />
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-8">
            {/* Settings Card */}
            <div className="bg-white dark:bg-gray-900 rounded-3xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm space-y-6">
              <h2 className="text-lg font-bold text-gray-900 dark:text-white border-b border-gray-100 dark:border-gray-800 pb-4 flex items-center gap-2">
                <svg className="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
                Post Settings
              </h2>

              <div className="space-y-2">
                <label className="text-sm font-semibold text-gray-700 dark:text-gray-300">Category</label>
                <Select
                  options={categoryOptions}
                  value={formData.selectedCategory}
                  onChange={(option: any) => setFormData(prev => ({ ...prev, selectedCategory: option }))}
                  styles={customSelectStyles}
                  placeholder="Select category..."
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-semibold text-gray-700 dark:text-gray-300">Visibility</label>
                <div className="grid grid-cols-2 gap-2">
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, type: "PUBLIC" }))}
                    className={`py-2 px-4 rounded-xl text-sm font-bold border transition-all ${
                      formData.type === "PUBLIC"
                        ? "bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-900/20 dark:text-emerald-400 dark:border-emerald-800/50"
                        : "bg-gray-50 text-gray-600 border-gray-100 dark:bg-gray-800/50 dark:text-gray-400 dark:border-gray-700 hover:bg-gray-100"
                    }`}
                  >
                    Public
                  </button>
                  <button
                    type="button"
                    onClick={() => setFormData(prev => ({ ...prev, type: "PRIVATE" }))}
                    className={`py-2 px-4 rounded-xl text-sm font-bold border transition-all ${
                      formData.type === "PRIVATE"
                        ? "bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-900/20 dark:text-amber-400 dark:border-amber-800/50"
                        : "bg-gray-50 text-gray-600 border-gray-100 dark:bg-gray-800/50 dark:text-gray-400 dark:border-gray-700 hover:bg-gray-100"
                    }`}
                  >
                    Private
                  </button>
                </div>
              </div>

              <div className="space-y-2">
                <label htmlFor="image" className="text-sm font-semibold text-gray-700 dark:text-gray-300">Feature Image URL</label>
                <input
                  type="url"
                  id="image"
                  name="image"
                  value={formData.image}
                  onChange={handleChange}
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50/50 dark:bg-gray-800/50 text-gray-900 dark:text-white text-xs focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 outline-none transition-all"
                  placeholder="https://images.unsplash.com/..."
                />
                {formData.image && (
                  <div className="mt-2 rounded-xl overflow-hidden aspect-video border border-gray-200 dark:border-gray-800">
                    <img src={formData.image} alt="Preview" className="w-full h-full object-cover" />
                  </div>
                )}
              </div>
            </div>

            <div className="bg-white dark:bg-gray-900 rounded-3xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm space-y-4">
              <button
                type="submit"
                disabled={isLoading}
                className="w-full py-4 bg-blue-600 hover:bg-blue-700 text-white rounded-2xl font-bold shadow-lg shadow-blue-500/25 transition-all flex items-center justify-center gap-3 disabled:opacity-50"
              >
                {isLoading ? (
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                ) : (
                  <span>{isEdit ? "Save Changes" : "Publish Post"}</span>
                )}
              </button>
              <Link
                to="/admin/posts"
                className="block w-full py-4 text-center text-gray-600 dark:text-gray-400 font-bold hover:bg-gray-50 dark:hover:bg-gray-800 rounded-2xl transition-all"
              >
                Cancel
              </Link>
            </div>
          </div>
        </form>
      </div>
    </AdminLayout>
  );
}
