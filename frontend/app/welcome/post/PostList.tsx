import React, { useState, useMemo, useEffect } from 'react';
import { PostCard } from './PostCard';
import { useNavigate } from 'react-router';
import { useAppDispatch, useAppSelector } from '~/store/hooks';
import { fetchCategories } from '~/store/slices/categoriesSlice';
import { fetchPosts, type Post as PostFromSlice } from '~/store/slices/postsSlice';





export const PostList: React.FC = () => {
  const dispatch = useAppDispatch();
  const { categories } = useAppSelector((state) => state.categories);
  const { posts, pageInfo, isLoading } = useAppSelector((state) => state.posts);
  const navigate = useNavigate();
  
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [sortBy, setSortBy] = useState<'newest' | 'oldest' | 'votes'>('newest');
  const [currentPage, setCurrentPage] = useState(1);
  const postsPerPage = 5;

  useEffect(() => {
    dispatch(fetchCategories());
  }, [dispatch]);

  useEffect(() => {
    const direction = sortBy === 'oldest' ? 'asc' : 'desc';
    const sortField = sortBy === 'votes' ? 'voteCount' : 'createdAt';
    
    dispatch(fetchPosts({
      page: currentPage - 1,
      size: postsPerPage,
      search: searchQuery,
      sortBy: sortField,
      direction: direction,
      type: 'PUBLIC'
    }));
  }, [dispatch, currentPage, searchQuery, sortBy]);

  // Client-side category filtering since backend might not support it yet in search
  const filteredPosts = useMemo(() => {
    if (selectedCategories.length > 0) {
      return posts.filter(post => selectedCategories.includes(post.category.name));
    }
    return posts;
  }, [posts, selectedCategories]);

  const totalPages = pageInfo.totalPages;
  const currentPosts = filteredPosts;

  // Reset to page 1 when filters change
  useEffect(() => {
    setCurrentPage(1);
  }, [searchQuery, selectedCategories, sortBy]);

  const toggleCategory = (category: string) => {
    setSelectedCategories((prev: string[]) => 
      prev.includes(category) 
        ? prev.filter((c: string) => c !== category) 
        : [...prev, category]
    );
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Search Header - SO Style */}
      <div className="mb-8">
        <div className="relative max-w-2xl mx-auto">
          <input
            type="text"
            placeholder="Search questions, authors or tags..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full px-10 py-3 rounded border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 text-gray-900 dark:text-white focus:ring-2 focus:ring-orange-500/20 focus:border-orange-500 outline-none transition-all shadow-sm shadow-black/5"
          />
          <svg className="absolute left-3 top-3.5 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" /></svg>
        </div>
      </div>

      <div className="flex flex-col lg:flex-row gap-8">
        {/* Main Content (Left) */}
        <div className="flex-1">
          <div className="flex items-center justify-between mb-6 border-b border-gray-200 dark:border-gray-800 pb-4">
            <h2 className="text-xl font-normal text-gray-900 dark:text-white">
              {searchQuery ? `Search Results for "${searchQuery}"` : 'All Questions'}
            </h2>
            <div className="flex bg-white dark:bg-gray-900 border border-gray-300 dark:border-gray-700 rounded overflow-hidden">
              <button 
                onClick={() => setSortBy('newest')}
                className={`px-3 py-1.5 text-[11px] font-medium border-r border-gray-300 dark:border-gray-700 ${sortBy === 'newest' ? 'bg-gray-100 dark:bg-gray-800' : 'hover:bg-gray-50 dark:hover:bg-gray-800'}`}
              >
                Newest
              </button>
              <button 
                onClick={() => setSortBy('votes')}
                className={`px-3 py-1.5 text-[11px] font-medium border-r border-gray-300 dark:border-gray-700 ${sortBy === 'votes' ? 'bg-gray-100 dark:bg-gray-800' : 'hover:bg-gray-50 dark:hover:bg-gray-800'}`}
              >
                Votes
              </button>
              <button 
                onClick={() => setSortBy('oldest')}
                className={`px-3 py-1.5 text-[11px] font-medium ${sortBy === 'oldest' ? 'bg-gray-100 dark:bg-gray-800' : 'hover:bg-gray-50 dark:hover:bg-gray-800'}`}
              >
                Oldest
              </button>
            </div>
          </div>

          <div className="bg-white dark:bg-gray-950 rounded border border-gray-200 dark:border-gray-800 divide-y divide-gray-200 dark:divide-gray-800 shadow-sm min-h-[400px]">
            {isLoading ? (
              <div className="flex items-center justify-center h-64">
                <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-orange-600"></div>
              </div>
            ) : (
              <>
                {currentPosts.map((post: PostFromSlice) => (
                  <PostCard 
                    key={post.id} 
                    post={{
                      id: post.id.toString(),
                      title: post.title,
                      excerpt: post.excerpt,
                      content: post.content,
                      author: post.authorName || 'Anonymous', 
                      authorId: post.authorId,
                      date: new Date(post.createdAt).toLocaleDateString(),
                      category: post.category?.name || 'General',
                      image: post.image || 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&q=80&w=2072',
                      type: (post.type?.toLowerCase() === 'private' ? 'private' : 'public') as 'public' | 'private',
                      voteCount: post.voteCount,
                      viewCount: post.viewCount,
                      commentCount: post.commentCount
                    }} 
                    onClick={() => navigate(`/posts/${post.id}`)} 
                  />
                ))}
              </>
            )}
            {!isLoading && currentPosts.length === 0 && (
              <div className="p-12 text-center">
                <p className="text-gray-500 mb-2">We couldn&apos;t find anything matching your search.</p>
                <button onClick={() => setSearchQuery('')} className="text-blue-600 hover:underline text-sm">Clear search</button>
              </div>
            )}
          </div>

          {/* Pagination Controls - SO Style */}
          {totalPages > 1 && (
            <div className="mt-8 flex items-center gap-1">
              <button
                disabled={currentPage === 1}
                onClick={() => setCurrentPage((prev: number) => Math.max(prev - 1, 1))}
                className={`px-2 py-1 rounded border text-xs font-normal transition-colors ${currentPage === 1 ? 'text-gray-300 border-gray-200 cursor-not-allowed' : 'text-gray-700 border-gray-300 hover:bg-gray-100'}`}
              >
                Prev
              </button>
              
              {[...Array(totalPages)].map((_, i) => (
                <button
                  key={i + 1}
                  onClick={() => setCurrentPage(i + 1)}
                  className={`px-3 py-1 rounded border text-xs font-normal transition-colors ${currentPage === i + 1 ? 'bg-orange-600 border-orange-600 text-white' : 'text-gray-700 border-gray-300 hover:bg-gray-100'}`}
                >
                  {i + 1}
                </button>
              ))}

              <button
                disabled={currentPage === totalPages}
                onClick={() => setCurrentPage((prev: number) => Math.min(prev + 1, totalPages))}
                className={`px-2 py-1 rounded border text-xs font-normal transition-colors ${currentPage === totalPages ? 'text-gray-300 border-gray-200 cursor-not-allowed' : 'text-gray-700 border-gray-300 hover:bg-gray-100'}`}
              >
                Next
              </button>
            </div>
          )}
        </div>

        {/* Sidebar (Right) */}
        <aside className="w-full lg:w-72 flex flex-col gap-6">
          {/* Categories Filter */}
          <div className="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded p-4 shadow-sm">
            <h3 className="text-xs font-bold text-gray-900 dark:text-white mb-4 uppercase tracking-wider border-b border-gray-100 dark:border-gray-800 pb-2">
              Filter by Category
            </h3>
            <div className="space-y-3">
              {categories.map(category => (
                <label key={category.id} className="flex items-center gap-3 cursor-pointer group">
                  <div className="relative flex items-center">
                    <input 
                      type="checkbox" 
                      className="peer h-4 w-4 rounded border-gray-300 text-orange-600 focus:ring-orange-500 transition-all cursor-pointer accent-orange-500"
                      checked={selectedCategories.includes(category.name)}
                      onChange={() => toggleCategory(category.name)}
                    />
                  </div>
                  <span className="text-xs text-gray-600 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white transition-colors">
                    {category.name}
                  </span>
                </label>
              ))}
              {categories.length === 0 && (
                <p className="text-[10px] text-gray-400 italic">No categories available</p>
              )}
            </div>
          </div>

          {/* Professional Stats Widget */}
          <div className="bg-amber-50 dark:bg-amber-900/10 border border-amber-200 dark:border-amber-900/30 rounded p-4">
            <h3 className="text-xs font-bold text-amber-800 dark:text-amber-400 mb-2 font-mono">
              COMMUNITY_METRICS
            </h3>
            <div className="space-y-2 text-[11px] text-amber-900 dark:text-amber-300 opacity-80">
              <div className="flex justify-between">
                <span>Total Posts:</span>
                <span className="font-bold">{pageInfo.totalElements}</span>
              </div>
              <div className="flex justify-between">
                <span>Public visibility:</span>
                <span className="font-bold">100%</span>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
};
