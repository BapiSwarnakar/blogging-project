import React from 'react';

export interface Post {
  id: string;
  title: string;
  excerpt: string;
  content: string;
  author: string;
  date: string;
  category: string;
  image: string;
  type: 'public' | 'private';
}

interface PostCardProps {
  post: Post;
  onClick: (post: Post) => void;
}

export const PostCard: React.FC<PostCardProps> = ({ post, onClick }) => {
  // Mocking some stats for StackOverflow look
  const votes = Math.floor(Math.random() * 50);
  const answers = Math.floor(Math.random() * 10);
  const views = Math.floor(Math.random() * 500);

  return (
    <div 
      onClick={() => onClick(post)}
      className="flex flex-col sm:flex-row gap-4 p-4 border-b border-gray-200 dark:border-gray-800 hover:bg-gray-50 dark:hover:bg-gray-900/50 transition-colors cursor-pointer group"
    >
      {/* Stats container (SO style) */}
      <div className="flex flex-row sm:flex-col items-end sm:items-end gap-3 sm:gap-1.5 min-w-[100px] text-sm pt-1">
        <div className="flex flex-row sm:flex-col items-center gap-1 sm:gap-0 text-gray-600 dark:text-gray-400">
          <span className="font-medium">{votes}</span>
          <span className="text-[11px] sm:text-xs">votes</span>
        </div>
        <div className={`flex flex-row sm:flex-col items-center gap-1 sm:gap-0 px-2 py-1 rounded border ${answers > 0 ? 'border-green-600 text-green-600 dark:border-green-500 dark:text-green-500' : 'text-gray-600 dark:text-gray-400 border-transparent'}`}>
          <span className="font-medium">{answers}</span>
          <span className="text-[11px] sm:text-xs">answers</span>
        </div>
        <div className="flex flex-row sm:flex-col items-center gap-1 sm:gap-0 text-amber-700 dark:text-amber-500">
          <span className="font-medium">{views}</span>
          <span className="text-[11px] sm:text-xs">views</span>
        </div>
      </div>

      {/* Content area */}
      <div className="flex-1 min-w-0">
        <h3 className="text-lg text-blue-600 dark:text-blue-400 hover:text-blue-500 transition-colors mb-1 font-normal line-clamp-2">
          {post.title}
        </h3>
        
        <p className="text-gray-700 dark:text-gray-300 text-sm mb-3 line-clamp-2 leading-relaxed">
          {post.excerpt}
        </p>
        
        <div className="flex flex-wrap items-center justify-between gap-3">
          {/* Tags */}
          <div className="flex flex-wrap gap-2">
            <span className="px-2 py-1 bg-blue-50 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 text-xs rounded hover:bg-blue-100 dark:hover:bg-blue-900/50 transition-colors">
              {post.category.toLowerCase()}
            </span>
            {/* Mocking extra tags for SO look */}
            <span className="px-2 py-1 bg-blue-50 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 text-xs rounded hover:bg-blue-100 dark:hover:bg-blue-900/50 transition-colors">
              frontend
            </span>
          </div>

          {/* User Info Card (SO style) */}
          <div className="flex items-center gap-2 text-xs ml-auto">
            <div className="w-8 h-8 rounded bg-gray-200 dark:bg-gray-700 flex items-center justify-center font-bold text-gray-400">
              {post.author.charAt(0)}
            </div>
            <div className="flex flex-col">
              <span className="text-blue-600 dark:text-blue-400 hover:underline">{post.author}</span>
              <span className="text-gray-500">asked {post.date}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
