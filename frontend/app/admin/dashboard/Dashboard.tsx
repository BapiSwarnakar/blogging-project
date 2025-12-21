import { AdminLayout } from "../layout/AdminLayout";

export function AdminDashboard() {
  return (
    <AdminLayout title="Dashboard Overview">
      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
        <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">Total Posts</p>
              <p className="text-3xl font-bold text-gray-900 dark:text-white mt-2">1,234</p>
              <p className="text-sm text-green-600 dark:text-green-400 mt-2">
                <span className="font-medium">+12%</span> from last month
              </p>
            </div>
            <div className="w-12 h-12 bg-blue-100 dark:bg-blue-900/30 rounded-xl flex items-center justify-center">
              <svg className="w-6 h-6 text-blue-600 dark:text-blue-400" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 2a1 1 0 000 2h2a1 1 0 100-2H9z" />
                <path fillRule="evenodd" d="M4 5a2 2 0 012-2 3 3 0 003 3h2a3 3 0 003-3 2 2 0 012 2v11a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 000 2h.01a1 1 0 100-2H7zm3 0a1 1 0 000 2h3a1 1 0 100-2h-3zm-3 4a1 1 0 100 2h.01a1 1 0 100-2H7zm3 0a1 1 0 100 2h3a1 1 0 100-2h-3z" clipRule="evenodd" />
              </svg>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">Total Users</p>
              <p className="text-3xl font-bold text-gray-900 dark:text-white mt-2">5,678</p>
              <p className="text-sm text-green-600 dark:text-green-400 mt-2">
                <span className="font-medium">+8%</span> from last month
              </p>
            </div>
            <div className="w-12 h-12 bg-purple-100 dark:bg-purple-900/30 rounded-xl flex items-center justify-center">
              <svg className="w-6 h-6 text-purple-600 dark:text-purple-400" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
              </svg>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">Comments</p>
              <p className="text-3xl font-bold text-gray-900 dark:text-white mt-2">8,456</p>
              <p className="text-sm text-green-600 dark:text-green-400 mt-2">
                <span className="font-medium">+23%</span> from last month
              </p>
            </div>
            <div className="w-12 h-12 bg-green-100 dark:bg-green-900/30 rounded-xl flex items-center justify-center">
              <svg className="w-6 h-6 text-green-600 dark:text-green-400" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zM9 9h2v2H9V9z" clipRule="evenodd" />
              </svg>
            </div>
          </div>
        </div>

        <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm font-medium text-gray-600 dark:text-gray-400">Page Views</p>
              <p className="text-3xl font-bold text-gray-900 dark:text-white mt-2">45.2K</p>
              <p className="text-sm text-red-600 dark:text-red-400 mt-2">
                <span className="font-medium">-3%</span> from last month
              </p>
            </div>
            <div className="w-12 h-12 bg-orange-100 dark:bg-orange-900/30 rounded-xl flex items-center justify-center">
              <svg className="w-6 h-6 text-orange-600 dark:text-orange-400" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                <path fillRule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clipRule="evenodd" />
              </svg>
            </div>
          </div>
        </div>
      </div>

      {/* Charts and Tables */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        {/* Recent Posts */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Recent Posts</h3>
          <div className="space-y-4">
            {[
              { title: "Getting Started with React", author: "John Doe", date: "2 hours ago", status: "Published" },
              { title: "Advanced TypeScript Tips", author: "Jane Smith", date: "5 hours ago", status: "Published" },
              { title: "CSS Grid Layout Guide", author: "Bob Johnson", date: "1 day ago", status: "Draft" },
              { title: "Node.js Best Practices", author: "Alice Brown", date: "2 days ago", status: "Published" },
            ].map((post, index) => (
              <div key={`post-${index}`} className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                <div className="flex-1">
                  <h4 className="text-sm font-medium text-gray-900 dark:text-white">{post.title}</h4>
                  <p className="text-xs text-gray-500 dark:text-gray-400 mt-1">
                    by {post.author} • {post.date}
                  </p>
                </div>
                <span className={`px-3 py-1 text-xs font-medium rounded-full ${
                  post.status === "Published"
                    ? "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400"
                    : "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-400"
                }`}>
                  {post.status}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Top Authors */}
        <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Top Authors</h3>
          <div className="space-y-4">
            {[
              { name: "John Doe", posts: 45, avatar: "JD", color: "from-blue-500 to-cyan-500" },
              { name: "Jane Smith", posts: 38, avatar: "JS", color: "from-purple-500 to-pink-500" },
              { name: "Bob Johnson", posts: 32, avatar: "BJ", color: "from-green-500 to-teal-500" },
              { name: "Alice Brown", posts: 28, avatar: "AB", color: "from-orange-500 to-red-500" },
            ].map((author, index) => (
              <div key={`author-${index}`} className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 bg-gradient-to-br ${author.color} rounded-full flex items-center justify-center text-white font-semibold text-sm`}>
                    {author.avatar}
                  </div>
                  <div>
                    <h4 className="text-sm font-medium text-gray-900 dark:text-white">{author.name}</h4>
                    <p className="text-xs text-gray-500 dark:text-gray-400">{author.posts} posts</p>
                  </div>
                </div>
                <button className="text-blue-600 dark:text-blue-400 hover:underline text-sm font-medium">
                  View Profile
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Activity Chart Placeholder */}
      <div className="bg-white dark:bg-gray-900 rounded-2xl p-6 border border-gray-200 dark:border-gray-800 shadow-sm">
        <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Activity Overview</h3>
        <div className="h-64 flex items-center justify-center bg-gradient-to-br from-blue-50 to-purple-50 dark:from-blue-900/10 dark:to-purple-900/10 rounded-xl">
          <div className="text-center">
            <svg className="w-16 h-16 mx-auto text-gray-400 dark:text-gray-600 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
            <p className="text-gray-600 dark:text-gray-400">Chart visualization would go here</p>
          </div>
        </div>
      </div>
    </AdminLayout>
  );
}
