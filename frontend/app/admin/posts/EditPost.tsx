import { useEffect } from "react";
import { useParams } from "react-router";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { fetchPostById, clearCurrentPost } from "../../store/slices/postsSlice";
import { PostForm } from "./PostForm";
import { AdminLayout } from "../layout/AdminLayout";

export function EditPostPage() {
  const { id } = useParams();
  const dispatch = useAppDispatch();
  const { currentPost, isLoading, error } = useAppSelector((state) => state.posts);

  useEffect(() => {
    if (id) {
      dispatch(fetchPostById(Number(id)));
    }
    return () => {
      dispatch(clearCurrentPost());
    };
  }, [dispatch, id]);

  if (isLoading) {
    return (
      <AdminLayout title="Loading Post...">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="w-12 h-12 border-4 border-blue-600/30 border-t-blue-600 rounded-full animate-spin"></div>
        </div>
      </AdminLayout>
    );
  }

  if (error || !currentPost) {
    return (
      <AdminLayout title="Post Not Found">
        <div className="text-center py-20">
          <div className="inline-flex items-center justify-center w-20 h-20 rounded-full bg-rose-50 dark:bg-rose-900/20 text-rose-600 mb-6">
            <svg className="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">Post Not Found</h2>
          <p className="text-gray-500 dark:text-gray-400">The post you are looking for does not exist or has been deleted.</p>
        </div>
      </AdminLayout>
    );
  }

  return <PostForm initialData={currentPost} isEdit />;
}
