import { Link } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";

export function UsersPage() {
  const users = [
    { id: 1, name: "John Doe", email: "john@example.com", role: "Admin", status: "Active", posts: 45, joined: "Jan 15, 2024" },
    { id: 2, name: "Jane Smith", email: "jane@example.com", role: "Author", status: "Active", posts: 38, joined: "Feb 20, 2024" },
    { id: 3, name: "Bob Johnson", email: "bob@example.com", role: "Author", status: "Active", posts: 32, joined: "Mar 10, 2024" },
    { id: 4, name: "Alice Brown", email: "alice@example.com", role: "Editor", status: "Inactive", posts: 28, joined: "Apr 5, 2024" },
    { id: 5, name: "Charlie Wilson", email: "charlie@example.com", role: "Author", status: "Active", posts: 15, joined: "May 12, 2024" },
  ];

  return (
    <AdminLayout title="User Management">
      <div className="space-y-6">
        {/* Header with Actions */}
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white">All Users</h2>
            <p className="text-gray-600 dark:text-gray-400 mt-1">Manage your blog users and their permissions</p>
          </div>
          <Link
            to="/admin/users/create"
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors font-medium"
          >
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
            </svg>
            Create User
          </Link>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="bg-white dark:bg-gray-900 rounded-xl p-4 border border-gray-200 dark:border-gray-800">
            <p className="text-sm text-gray-600 dark:text-gray-400">Total Users</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-white mt-1">5,678</p>
          </div>
          <div className="bg-white dark:bg-gray-900 rounded-xl p-4 border border-gray-200 dark:border-gray-800">
            <p className="text-sm text-gray-600 dark:text-gray-400">Active Users</p>
            <p className="text-2xl font-bold text-green-600 dark:text-green-400 mt-1">4,892</p>
          </div>
          <div className="bg-white dark:bg-gray-900 rounded-xl p-4 border border-gray-200 dark:border-gray-800">
            <p className="text-sm text-gray-600 dark:text-gray-400">Authors</p>
            <p className="text-2xl font-bold text-blue-600 dark:text-blue-400 mt-1">234</p>
          </div>
          <div className="bg-white dark:bg-gray-900 rounded-xl p-4 border border-gray-200 dark:border-gray-800">
            <p className="text-sm text-gray-600 dark:text-gray-400">New This Month</p>
            <p className="text-2xl font-bold text-purple-600 dark:text-purple-400 mt-1">127</p>
          </div>
        </div>

        {/* Search and Filter */}
        <div className="bg-white dark:bg-gray-900 rounded-xl p-4 border border-gray-200 dark:border-gray-800">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <input
                type="text"
                placeholder="Search users by name or email..."
                className="w-full px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
              />
            </div>
            <select className="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none">
              <option>All Roles</option>
              <option>Admin</option>
              <option>Editor</option>
              <option>Author</option>
            </select>
            <select className="px-4 py-2 rounded-lg border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none">
              <option>All Status</option>
              <option>Active</option>
              <option>Inactive</option>
            </select>
          </div>
        </div>

        {/* Users Table */}
        <div className="bg-white dark:bg-gray-900 rounded-xl border border-gray-200 dark:border-gray-800 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">User</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">Role</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">Status</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">Posts</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">Joined</th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                {users.map((user) => (
                  <tr key={user.id} className="hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white font-semibold">
                          {user.name.charAt(0)}
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900 dark:text-white">{user.name}</div>
                          <div className="text-sm text-gray-500 dark:text-gray-400">{user.email}</div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-3 py-1 text-xs font-medium rounded-full ${
                        user.role === "Admin" 
                          ? "bg-purple-100 text-purple-800 dark:bg-purple-900/30 dark:text-purple-400"
                          : user.role === "Editor"
                          ? "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-400"
                          : "bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-400"
                      }`}>
                        {user.role}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-3 py-1 text-xs font-medium rounded-full ${
                        user.status === "Active"
                          ? "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-400"
                          : "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-400"
                      }`}>
                        {user.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                      {user.posts}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                      {user.joined}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <div className="flex items-center justify-end gap-2">
                        <Link
                          to={`/admin/users/${user.id}/edit`}
                          className="text-blue-600 dark:text-blue-400 hover:text-blue-900 dark:hover:text-blue-300"
                        >
                          Edit
                        </Link>
                        <button className="text-red-600 dark:text-red-400 hover:text-red-900 dark:hover:text-red-300">
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Pagination */}
        <div className="flex items-center justify-between">
          <p className="text-sm text-gray-600 dark:text-gray-400">
            Showing <span className="font-medium">1</span> to <span className="font-medium">5</span> of{" "}
            <span className="font-medium">5,678</span> results
          </p>
          <div className="flex gap-2">
            <button className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800 disabled:opacity-50" disabled>
              Previous
            </button>
            <button className="px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-lg text-sm font-medium text-gray-700 dark:text-gray-300 hover:bg-gray-50 dark:hover:bg-gray-800">
              Next
            </button>
          </div>
        </div>
      </div>
    </AdminLayout>
  );
}
