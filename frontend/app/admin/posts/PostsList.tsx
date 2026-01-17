import { useState, useEffect } from "react";
import { Link } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { fetchPosts, deletePost } from "../../store/slices/postsSlice";
import { showConfirmDialog } from "../../utils/sweetAlert";
import { toast } from "react-hot-toast";

export function PostsPage() {
  const dispatch = useAppDispatch();
  const { posts, isLoading, pageInfo } = useAppSelector((state) => state.posts);

  const [search, setSearch] = useState("");
  const [debouncedSearch, setDebouncedSearch] = useState("");
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [sortBy] = useState("createdAt");
  const [direction] = useState("desc");

  // Debounce search effect
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
    }, 500);

    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    dispatch(fetchPosts({ page, size, sortBy, direction, search: debouncedSearch }));
  }, [dispatch, page, size, sortBy, direction, debouncedSearch]);

  const handleDelete = async (id: number, title: string) => {
    const result = await showConfirmDialog({
      title: "Delete Post?",
      text: `Are you sure you want to delete post "${title}"? This action cannot be undone.`,
      confirmButtonText: "Yes, delete",
    });

    if (result.isConfirmed) {
      try {
        await dispatch(deletePost(id)).unwrap();
        toast.success("Post deleted successfully");
      } catch (error: any) {
        toast.error(error || "Failed to delete post");
      }
    }
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearch(e.target.value);
    setPage(0);
  };

  return (
    <AdminLayout title="Post Management">
      <div className="space-y-6">
        {/* Header with Actions */}
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white">Blog Posts</h2>
            <p className="text-gray-600 dark:text-gray-400 mt-1">Manage and moderate your community's content</p>
          </div>
          <Link
            to="/admin/posts/create"
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors font-medium shadow-sm"
          >
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
            </svg>
            Create Post
          </Link>
        </div>


        {/* Search and Filter */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl p-4 border border-gray-200 dark:border-gray-800 shadow-sm">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1 relative">
              <span className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
              </span>
              <input
                type="text"
                placeholder="Search by title or excerpt..."
                value={search}
                onChange={handleSearchChange}
                className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50 text-gray-900 dark:text-white focus:ring-4 focus:ring-blue-500/10 focus:border-blue-500 outline-none transition-all"
              />
            </div>
          </div>
        </div>

        {/* Posts Table */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl border border-gray-200 dark:border-gray-800 shadow-sm overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 dark:bg-gray-800/50 border-b border-gray-200 dark:border-gray-700">
                <tr>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Post Info</th>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Category</th>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Type</th>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Stats</th>
                  <th className="px-6 py-4 text-left text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Date</th>
                  <th className="px-6 py-4 text-right text-xs font-bold text-gray-500 dark:text-gray-400 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                {isLoading ? (
                  <tr>
                    <td colSpan={6} className="px-6 py-12 text-center">
                      <div className="flex flex-col items-center gap-3">
                        <div className="w-10 h-10 border-4 border-blue-600/30 border-t-blue-600 rounded-full animate-spin"></div>
                        <span className="text-gray-500 dark:text-gray-400 font-medium">Loading posts...</span>
                      </div>
                    </td>
                  </tr>
                ) : posts.length === 0 ? (
                  <tr>
                    <td colSpan={6} className="px-6 py-12 text-center text-gray-500 dark:text-gray-400 font-medium">
                      No posts found.
                    </td>
                  </tr>
                ) : (
                  posts.map((post) => (
                    <tr key={post.id} className="group hover:bg-blue-50/30 dark:hover:bg-blue-900/5 transition-all">
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          <div className="w-11 h-11 bg-gray-100 dark:bg-gray-800 rounded-lg flex-shrink-0 overflow-hidden">
                            {post.image ? (
                              <img src={post.image} alt={post.title} className="w-full h-full object-cover" />
                            ) : (
                              <div className="w-full h-full flex items-center justify-center text-gray-400">
                                <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                              </div>
                            )}
                          </div>
                          <div className="ml-4">
                            <div className="text-sm font-bold text-gray-900 dark:text-white group-hover:text-blue-600 dark:group-hover:text-blue-400 transition-colors max-w-xs truncate">
                              {post.title}
                            </div>
                            <div className="text-xs text-gray-500 dark:text-gray-400 max-w-xs truncate">{post.excerpt}</div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className="px-2.5 py-0.5 text-[10px] font-bold uppercase rounded-md bg-purple-50 text-purple-700 dark:bg-purple-900/20 dark:text-purple-400 border border-purple-100 dark:border-purple-800/50">
                          {post.category?.name || "Uncategorized"}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-bold ${
                          post.type === "PUBLIC"
                            ? "bg-emerald-100 text-emerald-800 dark:bg-emerald-900/20 dark:text-emerald-400"
                            : "bg-amber-100 text-amber-800 dark:bg-amber-900/20 dark:text-amber-400"
                        }`}>
                          {post.type}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center gap-3 text-xs text-gray-600 dark:text-gray-400">
                          <span className="flex items-center gap-1">
                            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                            </svg>
                            {post.viewCount}
                          </span>
                          <span className="flex items-center gap-1">
                            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M14 10h4.757a2 2 0 011.708 3.033l-3.344 5.573A2 2 0 0115.415 21H8.585a2 2 0 01-1.708-3.033l3.344-5.573A2 2 0 0111.93 10H8.5a2 2 0 00-2 2v8a2 2 0 002 2h4.5a2 2 0 002-2v-8z" />
                            </svg>
                            {post.voteCount}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="text-xs text-gray-900 dark:text-white font-medium">
                          {new Date(post.createdAt).toLocaleDateString()}
                        </div>
                        <div className="text-[10px] text-gray-500 dark:text-gray-400">
                          {new Date(post.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                        <div className="flex items-center justify-end gap-3">
                          <Link
                            to={`/admin/posts/${post.id}/edit`}
                            className="p-2 rounded-lg bg-indigo-50 dark:bg-indigo-900/20 text-indigo-600 dark:text-indigo-400 hover:bg-indigo-600 hover:text-white dark:hover:bg-indigo-600 transition-all"
                            title="Edit Post"
                          >
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z" />
                            </svg>
                          </Link>
                          <button
                            onClick={() => handleDelete(post.id, post.title)}
                            className="p-2 rounded-lg bg-rose-50 dark:bg-rose-900/20 text-rose-600 dark:text-rose-400 hover:bg-rose-600 hover:text-white dark:hover:bg-rose-600 transition-all"
                            title="Delete Post"
                          >
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                          </button>
                        </div>
                      </td>
                    </tr>
                  )))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Pagination */}
        <div className="flex items-center justify-between py-2">
          <p className="text-sm text-gray-500 dark:text-gray-400 font-medium">
            Showing <span className="text-gray-900 dark:text-white font-bold">{pageInfo.number * pageInfo.size + (posts.length > 0 ? 1 : 0)}</span> to{" "}
            <span className="text-gray-900 dark:text-white font-bold">{pageInfo.number * pageInfo.size + posts.length}</span> of{" "}
            <span className="text-gray-900 dark:text-white font-bold">{pageInfo.totalElements}</span> posts
          </p>
          <div className="flex gap-3">
            <button
              onClick={() => setPage(page - 1)}
              disabled={page === 0 || isLoading}
              className="flex items-center gap-2 px-6 py-2 border border-gray-200 dark:border-gray-700 rounded-xl text-sm font-bold text-gray-600 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 19l-7-7 7-7" />
              </svg>
              Previous
            </button>
            <button
              onClick={() => setPage(page + 1)}
              disabled={page >= pageInfo.totalPages - 1 || isLoading}
              className="flex items-center gap-2 px-6 py-2 bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-xl text-sm font-bold text-gray-600 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800 disabled:opacity-40 disabled:cursor-not-allowed transition-all"
            >
              Next
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 5l7 7-7 7" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </AdminLayout>
  );
}
