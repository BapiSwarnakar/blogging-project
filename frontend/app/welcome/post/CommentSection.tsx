import React, { useState, useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '~/store/hooks';
import { Link } from 'react-router';
import { fetchCommentsByPostId, createComment, deleteComment, type Comment } from '~/store/slices/commentsSlice';

interface CommentSectionProps {
  postId: string;
}

const CommentItem: React.FC<{ 
    comment: Comment, 
    onReply: (parentId: number) => void,
    onDelete: (id: number) => void,
    currentUserId?: number,
    isAuthenticated: boolean,
    isReply?: boolean 
}> = ({ comment, onReply, onDelete, currentUserId, isAuthenticated, isReply = false }) => {
  const isAuthor = currentUserId === comment.authorId;
  return (
    <div className={`group ${isReply ? 'ml-12 mt-4' : 'mt-6'}`}>
      <div className="flex gap-4 p-4 rounded-xl hover:bg-gray-50 dark:hover:bg-gray-800/30 transition-colors">
        <div className="flex-shrink-0">
          <div className="w-10 h-10 rounded-full bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-white font-bold text-sm">
            {comment.authorName.charAt(0)}
          </div>
        </div>
        <div className="flex-1">
          <div className="flex items-center justify-between mb-1">
            <h4 className="font-semibold text-gray-900 dark:text-white">
              {comment.authorName}
            </h4>
            <span className="text-xs text-gray-500 dark:text-gray-500">
              {new Date(comment.createdAt).toLocaleDateString()}
            </span>
          </div>
          <p className="text-gray-700 dark:text-gray-300 leading-relaxed">
            {comment.content}
          </p>
          <div className="mt-2 flex gap-4">
            {isAuthenticated ? (
              <button 
                onClick={() => onReply(comment.id)}
                className="text-xs font-medium text-blue-600 hover:text-blue-700 dark:text-blue-400 dark:hover:text-blue-300 transition-colors"
              >
                Reply
              </button>
            ) : (
              <Link 
                to="/login"
                className="text-xs font-medium text-gray-500 hover:text-blue-600 dark:text-gray-500 dark:hover:text-blue-400 transition-colors"
              >
                Login to reply
              </Link>
            )}
            {isAuthor && (
              <button 
                onClick={() => {
                  if (window.confirm('Are you sure you want to delete this comment?')) {
                    onDelete(comment.id);
                  }
                }}
                className="text-xs font-medium text-red-600 hover:text-red-700 dark:text-red-400 dark:hover:text-red-300 transition-colors"
              >
                Delete
              </button>
            )}
          </div>
        </div>
      </div>
      
      {comment.replies && comment.replies.length > 0 && (
        <div className="border-l-2 border-gray-100 dark:border-gray-800 ml-4">
          {comment.replies.map(reply => (
            <CommentItem 
                key={reply.id} 
                comment={reply} 
                onReply={onReply} 
                onDelete={onDelete}
                currentUserId={currentUserId}
                isAuthenticated={isAuthenticated}
                isReply={true} 
            />
          ))}
        </div>
      )}
    </div>
  );
};

export const CommentSection: React.FC<CommentSectionProps> = ({ postId }) => {
  const dispatch = useAppDispatch();
  const { user, isAuthenticated } = useAppSelector((state) => state.auth);
  const { comments, isLoading } = useAppSelector((state) => state.comments);
  
  const [newComment, setNewComment] = useState('');
  const [replyTo, setReplyTo] = useState<number | null>(null);
  const [replyContent, setReplyContent] = useState('');

  useEffect(() => {
    dispatch(fetchCommentsByPostId(Number(postId)));
  }, [dispatch, postId]);

  const handleSubmit = async (e: React.FormEvent, parentId?: number) => {
    e.preventDefault();
    const content = parentId ? replyContent : newComment;
    if (!content.trim()) return;

    await dispatch(createComment({
      content,
      postId: Number(postId),
      parentId: parentId || null,
      authorName: user?.name || 'Anonymous'
    }));

    if (parentId) {
      setReplyTo(null);
      setReplyContent('');
    } else {
      setNewComment('');
    }
  };

  return (
    <div className="mt-12 pt-8 border-t border-gray-200 dark:border-gray-800">
      <div className="flex items-center justify-between mb-8">
        <h3 className="text-2xl font-bold text-gray-900 dark:text-white">
          Comments ({comments.length})
        </h3>
      </div>

      {isAuthenticated ? (
        <form onSubmit={(e) => handleSubmit(e)} className="mb-10">
          <div className="relative group">
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Join the discussion..."
              className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none resize-none h-32 shadow-sm"
            />
            <div className="mt-3 flex justify-end">
              <button
                type="submit"
                disabled={!newComment.trim()}
                className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed text-white font-medium rounded-lg transition-all shadow-lg shadow-blue-500/20 active:scale-95"
              >
                Post Comment
              </button>
            </div>
          </div>
        </form>
      ) : (
        <div className="mb-10 p-8 rounded-2xl bg-gray-50 dark:bg-gray-800/30 border border-dashed border-gray-300 dark:border-gray-700 text-center">
          <p className="text-gray-600 dark:text-gray-400 mb-6 font-medium">
            Join the conversation. Please log in to leave a comment.
          </p>
          <Link
            to="/login"
            className="inline-flex items-center gap-2 px-8 py-3 bg-gray-900 dark:bg-white text-white dark:text-gray-900 font-bold rounded-xl hover:bg-gray-800 dark:hover:bg-gray-100 transition-all shadow-xl active:scale-95"
          >
            Log In to Comment
          </Link>
        </div>
      )}

      {/* Reply Modal/Input Field overlay if replyTo is set */}
      {replyTo && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="w-full max-w-xl bg-white dark:bg-gray-900 rounded-2xl shadow-2xl p-6 border border-gray-100 dark:border-gray-800 animate-in fade-in zoom-in duration-200">
            <h4 className="text-lg font-bold mb-4 dark:text-white">Reply to Comment</h4>
            <form onSubmit={(e) => handleSubmit(e, replyTo)}>
              <textarea
                autoFocus
                value={replyContent}
                onChange={(e) => setReplyContent(e.target.value)}
                placeholder="Write your reply..."
                className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all outline-none resize-none h-32"
              />
              <div className="mt-4 flex justify-end gap-3">
                <button
                  type="button"
                  onClick={() => setReplyTo(null)}
                  className="px-4 py-2 text-gray-600 dark:text-gray-400 font-medium hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={!replyContent.trim()}
                  className="px-6 py-2 bg-blue-600 hover:bg-blue-700 disabled:opacity-50 text-white font-medium rounded-lg transition-all shadow-lg shadow-blue-500/20 active:scale-95"
                >
                  Reply
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {isLoading && comments.length === 0 ? (
        <div className="flex justify-center py-12">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      ) : (
        <div className="space-y-2">
            {comments.map((comment) => (
            <CommentItem 
                key={comment.id} 
                comment={comment} 
                onReply={(id) => setReplyTo(id)} 
                onDelete={(id) => dispatch(deleteComment(id))}
                currentUserId={user?.id}
                isAuthenticated={isAuthenticated}
            />
            ))}
            {comments.length === 0 && !isLoading && (
                <div className="text-center py-12 text-gray-500 dark:text-gray-400">
                    No comments yet. Be the first to share your thoughts!
                </div>
            )}
        </div>
      )}
    </div>
  );
};
