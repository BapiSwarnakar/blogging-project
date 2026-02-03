import React, { useEffect } from 'react';
import { useParams } from 'react-router';
import { useAppDispatch, useAppSelector } from '~/store/hooks';
import { fetchPostById, clearCurrentPost, votePost, incrementPostView, bookmarkPost } from '~/store/slices/postsSlice';
import { SocialIcons } from './SocialIcons';
import { CommentSection } from './CommentSection';
import { Navbar } from '../Navbar';

interface PostDetailProps {
  onBack: () => void;
}

import { Footer } from '../Footer';

export const PostDetail: React.FC<PostDetailProps> = ({ onBack }) => {
  const { id } = useParams<{ id: string }>();
  const dispatch = useAppDispatch();
  const { currentPost: post, isLoading, error } = useAppSelector((state) => state.posts);

  useEffect(() => {
    window.scrollTo(0, 0);
    if (id) {
      const postId = Number(id);
      dispatch(fetchPostById(postId));
      // Increment view when post is loaded
      dispatch(incrementPostView(postId));
    }
    return () => {
      dispatch(clearCurrentPost());
    };
  }, [id, dispatch]);

  const handleVote = (type: number) => {
    if (id) {
      dispatch(votePost({ id: Number(id), type }));
    }
  };

  const handleBookmark = () => {
    if (id) {
      dispatch(bookmarkPost(Number(id)));
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-[#fafafa] dark:bg-gray-950 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error || !post) {
    return (
      <div className="min-h-screen bg-[#fafafa] dark:bg-gray-950 flex items-center justify-center flex-col gap-4">
        <p className="text-gray-600 dark:text-gray-400">{error || 'Post not found'}</p>
        <button onClick={onBack} className="text-blue-600 hover:underline">Go Back</button>
      </div>
    );
  }

  // Adaptive mapping for backend data structure
  const askedDate = new Date(post.createdAt).toLocaleDateString();
  const viewedCount = post.viewCount ?? 0;
  const activeDays = Math.floor((new Date().getTime() - new Date(post.createdAt).getTime()) / (1000 * 3600 * 24)) || 0;

  return (
    <div className="min-h-screen bg-[#fafafa] dark:bg-gray-950 transition-colors duration-500">
      <Navbar />
      <div className="max-w-7xl mx-auto px-4 py-6">
      {/* Header section - SO Style */}
      <div className="flex flex-col mb-4 border-b border-gray-200 dark:border-gray-800 pb-4">
        <div className="flex justify-between items-start gap-4 mb-4">
          <h1 className="text-2xl md:text-3xl text-gray-800 dark:text-gray-100 font-normal leading-tight flex-1">
            {post.title}
          </h1>
          <button 
            onClick={onBack}
            className="flex-shrink-0 px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded shadow-sm transition-colors"
          >
            Back to List
          </button>
        </div>

        <div className="flex flex-wrap gap-4 text-xs text-gray-600 dark:text-gray-400">
          <div className="flex gap-1">
            <span className="text-gray-500">Asked</span>
            <span className="text-gray-800 dark:text-gray-200">{askedDate}</span>
          </div>
          <div className="flex gap-1">
            <span className="text-gray-500">Active</span>
            <span className="text-gray-800 dark:text-gray-200">{activeDays} days ago</span>
          </div>
          <div className="flex gap-1">
            <span className="text-gray-500">Viewed</span>
            <span className="text-gray-800 dark:text-gray-200">{viewedCount} times</span>
          </div>
        </div>
      </div>

      <div className="flex flex-col lg:flex-row gap-6">
        {/* Main Content (Left) */}
        <div className="flex-1 min-w-0">
          <div className="flex gap-4">
            {/* Voting Column */}
            <div className="flex flex-col items-center gap-4 w-12 flex-shrink-0">
              <button 
                onClick={() => handleVote(1)}
                className="p-2 rounded-full border border-gray-300 dark:border-gray-700 hover:bg-orange-100 dark:hover:bg-orange-900/30 text-gray-400 hover:text-orange-600 transition-all active:scale-95"
                title="Upvote"
              >
                <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 15l7-7 7 7" /></svg>
              </button>
              <span className="text-xl font-bold text-gray-700 dark:text-gray-300">
                {post.voteCount ?? 0}
              </span>
              <button 
                onClick={() => handleVote(-1)}
                className="p-2 rounded-full border border-gray-300 dark:border-gray-700 hover:bg-orange-100 dark:hover:bg-orange-900/30 text-gray-400 hover:text-orange-600 transition-all active:scale-95"
                title="Downvote"
              >
                <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7" /></svg>
              </button>
              <button 
                onClick={handleBookmark}
                className={`mt-2 transition-colors ${post.isBookmarked ? 'text-orange-600' : 'text-gray-400 hover:text-orange-600'}`}
                title={post.isBookmarked ? "Remove Bookmark" : "Add Bookmark"}
              >
                <svg className="w-6 h-6" fill={post.isBookmarked ? "currentColor" : "none"} stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />
                </svg>
              </button>
            </div>

            {/* Content Column */}
            <div className="flex-1">
              {/* Optional Post Image - kept smaller for SO feel */}
              {post.image && (
                <div className="mb-6 rounded overflow-hidden max-h-80 border border-gray-200 dark:border-gray-800">
                  <img src={post.image} alt={post.title} className="w-full h-full object-cover" />
                </div>
              )}

              <article className="prose prose-sm md:prose-base dark:prose-invert max-w-none text-gray-800 dark:text-gray-200 leading-relaxed">
                <p className="font-medium text-lg text-gray-600 dark:text-gray-400 mb-6 italic border-l-2 border-orange-500 pl-4 bg-orange-50/30 dark:bg-orange-900/10 py-2">
                  {post.excerpt}
                </p>
                
                <div className="space-y-6">
                  {post.content.split('\n\n').map((paragraph, idx) => (
                    <p key={idx}>{paragraph}</p>
                  ))}
                </div>
              </article>

              {/* Tags and Metadata container */}
              <div className="mt-8 flex flex-wrap items-start justify-between gap-6">
                <div className="flex gap-2">
                  <span className="px-2 py-1 bg-blue-50 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 text-xs rounded hover:bg-blue-100 dark:hover:bg-blue-900/50 cursor-pointer">
                    {post.category?.name.toLowerCase()}
                  </span>
                </div>
                
                <div className="flex flex-col items-end gap-2">
                  <div className="p-3 bg-blue-50/50 dark:bg-blue-900/10 border border-blue-100 dark:border-blue-900/30 rounded w-48">
                    <span className="text-[11px] text-gray-500 mb-1 block">asked {askedDate}</span>
                    <div className="flex items-center gap-2">
                      <div className="w-8 h-8 rounded bg-gradient-to-tr from-orange-400 to-red-500 flex items-center justify-center font-bold text-white shadow-sm uppercase">
                        {post.authorName?.charAt(0) || 'A'}
                      </div>
                      <div className="flex flex-col">
                        <span className="text-xs text-blue-600 dark:text-blue-400 font-medium">{post.authorName || 'Anonymous'}</span>
                        <div className="flex items-center gap-1">
                          <span className="text-[10px] font-bold text-gray-600">3,450</span>
                          <span className="text-[10px] text-gray-400">reputation</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <SocialIcons />
                </div>
              </div>

              <CommentSection postId={post.id.toString()} />
            </div>
          </div>
        </div>

        {/* Sidebar (Right) */}
        <aside className="w-full lg:w-72 flex flex-col gap-6 flex-shrink-0">
          {/* Category Navigation (Filter) */}
          <div className="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded shadow-sm">
            <div className="bg-gray-50 dark:bg-gray-800/50 px-4 py-3 border-b border-gray-200 dark:border-gray-800 font-bold text-sm text-gray-700 dark:text-gray-300">
              Browse Categories
            </div>
            <div className="p-4 flex flex-wrap gap-2">
              {['Technology', 'Design', 'Lifestyle', 'Environment'].map((cat) => (
                <button 
                  key={cat}
                  onClick={onBack}
                  className={`px-2 py-1 text-[11px] rounded transition-colors ${post.category?.name === cat ? 'bg-orange-500 text-white' : 'bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 hover:bg-gray-200'}`}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          {/* Post Filter & Interaction Widget */}
          <div className="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded shadow-sm overflow-hidden">
            <div className="bg-gray-50 dark:bg-gray-800/50 px-4 py-3 border-b border-gray-200 dark:border-gray-800 font-bold text-sm text-gray-700 dark:text-gray-300">
              Related Questions
            </div>
            <div className="p-4 space-y-4">
              {[1, 2, 3].map((i) => (
                <div key={i} className="flex gap-2 group cursor-pointer">
                  <span className="text-blue-600 dark:text-blue-400 text-xs font-bold bg-blue-50 dark:bg-blue-900/20 px-2 py-0.5 rounded h-fit">
                    {Math.floor(Math.random() * 50)}
                  </span>
                  <p className="text-xs text-blue-600 dark:text-blue-400 hover:text-blue-500 leading-snug line-clamp-2">
                    How to solve complex state management in React 19?
                  </p>
                </div>
              ))}
            </div>
          </div>

          {/* Ad/Highlight Widget */}
          <div className="bg-amber-50 dark:bg-amber-900/10 border border-amber-200 dark:border-amber-900/30 rounded p-4">
            <h3 className="text-sm font-bold text-amber-800 dark:text-amber-400 mb-2">
              Featured on Meta
            </h3>
            <ul className="text-xs space-y-3 text-gray-700 dark:text-gray-300">
              <li className="flex gap-2">
                 <svg className="w-4 h-4 flex-shrink-0 text-amber-600" fill="currentColor" viewBox="0 0 20 20"><path d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" /></svg>
                 <span>Improvements to the Blog Engine coming in Q3</span>
              </li>
              <li className="flex gap-2">
                 <svg className="w-4 h-4 flex-shrink-0 text-amber-600" fill="currentColor" viewBox="0 0 20 20"><path d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" /></svg>
                 <span>New Community Moderator selection process</span>
              </li>
            </ul>
          </div>

          <div className="sticky top-24">
             <div className="p-4 bg-gray-50 dark:bg-gray-800/20 border border-gray-200 dark:border-gray-800 rounded">
                <h4 className="text-xs font-bold uppercase tracking-widest text-gray-500 mb-4">Sharing is Caring</h4>
                <div className="flex justify-center">
                  <SocialIcons />
                </div>
             </div>
          </div>
        </aside>
      </div>
      </div>
      <Footer />
    </div>
  );
};
