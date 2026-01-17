import React, { useState, useMemo, useEffect } from 'react';
import { PostCard } from './PostCard';
import type { Post } from './PostCard';
import { useNavigate } from 'react-router';

export const MOCK_POSTS: Post[] = [
  {
    id: '1',
    title: 'How to implement sub-millisecond response times in Edge Computing?',
    excerpt: 'I am trying to optimize my frontend architecture for 2026 standards. Currently facing latency with AI agent orchestration.',
    content: 'The landscape of web development is evolving at an unprecedented pace. In 2026, we are seeing the convergence of artificial intelligence and traditional frontend frameworks...\n\nEdge computing has become the standard, allowing for sub-millisecond response times. Developers are now orchestrating sophisticated AI agents that handle everything from state management to performance optimization.\n\nDesign systems are becoming truly fluid, adapting in real-time to user preferences and accessibility needs without manual intervention.',
    author: 'Sam Altman',
    date: 'Jan 15, 2026',
    category: 'Technology',
    image: 'https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&q=80&w=2072',
    type: 'public'
  },
  {
    id: '2',
    title: 'Best practices for creating "conscious" minimalist UI designs?',
    excerpt: 'Looking for principles of conscious minimalism that combine modern aesthetics with warmth. Any advice on textures?',
    content: 'Minimalism is often misunderstood as living in empty, sterile spaces. However, the new era of minimalism focuses on "conscious living"...\n\nTextures are replacing colors as the primary way to create depth. Natural materials like reclaimed wood, linen, and hand-beaten copper are leading the trend.\n\nSmart homes are being integrated invisibly, ensuring technology serves the aesthetic rather than detracting from it.',
    author: 'Elena Rossi',
    date: 'Jan 12, 2026',
    category: 'Lifestyle',
    image: 'https://images.unsplash.com/photo-1484154218962-a197022b5858?auto=format&fit=crop&q=80&w=2074',
    type: 'public'
  },
  {
    id: '4',
    title: 'What is the psychological impact of teals and indigos in professional UI?',
    excerpt: 'I know blue invokes trust, but what about the intermediate hues? How do they affect the "vibe" of a professional app?',
    content: 'Colors are the silent language of the web. Understanding their psychological impact is crucial for any designer...\n\nBlue invokes trust, which is why it dominates fintech and healthcare. Red creates urgency, perfect for flash sales and critical alerts.\n\nBut the real magic happens in the intermediate hues—the teals, the indigos, and the warm grays that define the "vibe" of a professional application.',
    author: 'Sarah Jenkins',
    date: 'Jan 08, 2026',
    category: 'Design',
    image: 'https://images.unsplash.com/photo-1550684848-fac1c5b4e853?auto=format&fit=crop&q=80&w=2070',
    type: 'public'
  },
  {
    id: '5',
    title: 'Are vertical forests the future of sustainable urban architecture?',
    excerpt: 'Exploring case studies of skyscrapers that act as air filters and oxygen producers. How effective are they?',
    content: 'Cities are often seen as the antithesis of nature. Vertical forests are changing that narrative...\n\nBy integrating thousands of plants into the facade of skyscrapers, architects are reducing local temperatures by up to 3 degrees Celsius.\n\nThese buildings act as giant air filters, trapping particulate matter and producing hundreds of kilograms of oxygen daily.',
    author: 'Marcus Thorne',
    date: 'Jan 05, 2026',
    category: 'Environment',
    image: 'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?auto=format&fit=crop&q=80&w=2070',
    type: 'public'
  }
];

const CATEGORIES = ['Technology', 'Lifestyle', 'Design', 'Environment', 'Architecture'];

export const PostList: React.FC = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [sortBy, setSortBy] = useState<'newest' | 'oldest' | 'votes'>('newest');
  const [currentPage, setCurrentPage] = useState(1);
  const postsPerPage = 3;

  const filteredPosts = useMemo(() => {
    let result = MOCK_POSTS.filter(post => post.type !== 'private');

    if (searchQuery.trim()) {
      const query = searchQuery.toLowerCase();
      result = result.filter(post => 
        post.title.toLowerCase().includes(query) || 
        post.excerpt.toLowerCase().includes(query) ||
        post.author.toLowerCase().includes(query)
      );
    }

    if (selectedCategories.length > 0) {
      result = result.filter(post => selectedCategories.includes(post.category));
    }

    if (sortBy === 'newest') {
      result = [...result].sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());
    } else if (sortBy === 'oldest') {
      result = [...result].sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
    }
    
    return result;
  }, [selectedCategories, sortBy, searchQuery]);

  const totalPages = Math.ceil(filteredPosts.length / postsPerPage);
  const indexOfLastPost = currentPage * postsPerPage;
  const indexOfFirstPost = indexOfLastPost - postsPerPage;
  const currentPosts = filteredPosts.slice(indexOfFirstPost, indexOfLastPost);

  // Reset to page 1 when filters change
  useEffect(() => {
    setCurrentPage(1);
  }, [searchQuery, selectedCategories, sortBy]);

  const toggleCategory = (category: string) => {
    setSelectedCategories(prev => 
      prev.includes(category) 
        ? prev.filter(c => c !== category) 
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

          <div className="bg-white dark:bg-gray-950 rounded border border-gray-200 dark:border-gray-800 divide-y divide-gray-200 dark:divide-gray-800 shadow-sm">
            {currentPosts.map((post) => (
              <PostCard 
                key={post.id} 
                post={post} 
                onClick={(p) => navigate(`/posts/${p.id}`)} 
              />
            ))}
            {currentPosts.length === 0 && (
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
                onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
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
                onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
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
              {CATEGORIES.map(category => (
                <label key={category} className="flex items-center gap-3 cursor-pointer group">
                  <div className="relative flex items-center">
                    <input 
                      type="checkbox" 
                      className="peer h-4 w-4 rounded border-gray-300 text-orange-600 focus:ring-orange-500 transition-all cursor-pointer accent-orange-500"
                      checked={selectedCategories.includes(category)}
                      onChange={() => toggleCategory(category)}
                    />
                  </div>
                  <span className="text-xs text-gray-600 dark:text-gray-400 group-hover:text-gray-900 dark:group-hover:text-white transition-colors">
                    {category}
                  </span>
                </label>
              ))}
            </div>
          </div>

          {/* Professional Stats Widget */}
          <div className="bg-amber-50 dark:bg-amber-900/10 border border-amber-200 dark:border-amber-900/30 rounded p-4">
            <h3 className="text-xs font-bold text-amber-800 dark:text-amber-400 mb-2 font-mono">
              COMMUNITY_METRICS
            </h3>
            <div className="space-y-2 text-[11px] text-amber-900 dark:text-amber-300 opacity-80">
              <div className="flex justify-between">
                <span>Posts analyzed:</span>
                <span className="font-bold">{MOCK_POSTS.length}</span>
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
