import { useState } from "react";
import { Link } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";

interface Role {
  id: string;
  name: string;
  description: string;
  permissionsCount: number;
  usersCount: number;
  createdAt: string;
}

const mockRoles: Role[] = [
  {
    id: "1",
    name: "Administrator",
    description: "Full access to all system features and settings.",
    permissionsCount: 45,
    usersCount: 2,
    createdAt: "2023-01-01",
  },
  {
    id: "2",
    name: "Editor",
    description: "Can create, edit, and publish posts and manage comments.",
    permissionsCount: 12,
    usersCount: 5,
    createdAt: "2023-02-15",
  },
  {
    id: "3",
    name: "Author",
    description: "Can create and manage their own posts.",
    permissionsCount: 5,
    usersCount: 12,
    createdAt: "2023-03-10",
  },
  {
    id: "4",
    name: "Subscriber",
    description: "Can read posts and leave comments.",
    permissionsCount: 2,
    usersCount: 156,
    createdAt: "2023-04-20",
  },
];

export function RolesList() {
  const [searchTerm, setSearchTerm] = useState("");

  const filteredRoles = mockRoles.filter((role) =>
    role.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    role.description.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <AdminLayout title="Roles Management">
      <div className="space-y-6">
        {/* Header Actions */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="relative flex-1 max-w-md">
            <span className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg className="h-5 w-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </span>
            <input
              type="text"
              placeholder="Search roles..."
              className="block w-full pl-10 pr-3 py-2 border border-gray-300 dark:border-gray-700 rounded-lg leading-5 bg-white dark:bg-gray-800 text-gray-900 dark:text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <Link
            to="/admin/roles/create"
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-lg shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
          >
            <svg className="-ml-1 mr-2 h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            Create Role
          </Link>
        </div>

        {/* Roles Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredRoles.map((role) => (
            <div
              key={role.id}
              className="bg-white dark:bg-gray-800 rounded-xl border border-gray-200 dark:border-gray-700 p-6 hover:shadow-lg transition-shadow"
            >
              <div className="flex justify-between items-start mb-4">
                <div className="p-2 bg-blue-50 dark:bg-blue-900/20 rounded-lg text-blue-600 dark:text-blue-400">
                  <svg className="w-6 h-6" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M6.267 3.455a3.066 3.066 0 001.745-.723 3.066 3.066 0 013.976 0 3.066 3.066 0 001.745.723 3.066 3.066 0 012.812 2.812c.051.643.304 1.254.723 1.745a3.066 3.066 0 010 3.976 3.066 3.066 0 00-.723 1.745 3.066 3.066 0 01-2.812 2.812 3.066 3.066 0 00-1.745.723 3.066 3.066 0 01-3.976 0 3.066 3.066 0 00-1.745-.723 3.066 3.066 0 01-2.812-2.812 3.066 3.066 0 00-.723-1.745 3.066 3.066 0 010-3.976 3.066 3.066 0 00.723-1.745 3.066 3.066 0 012.812-2.812zm7.44 5.252a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                </div>
                <div className="flex space-x-2">
                  <Link
                    to={`/admin/roles/${role.id}/edit`}
                    className="p-1 text-gray-400 hover:text-blue-600 transition-colors"
                  >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                    </svg>
                  </Link>
                  <button className="p-1 text-gray-400 hover:text-red-600 transition-colors">
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
              <h3 className="text-lg font-bold text-gray-900 dark:text-white mb-2">{role.name}</h3>
              <p className="text-gray-600 dark:text-gray-400 text-sm mb-6 line-clamp-2">
                {role.description}
              </p>
              <div className="flex items-center justify-between pt-4 border-t border-gray-100 dark:border-gray-700">
                <div className="flex flex-col">
                  <span className="text-xl font-bold text-gray-900 dark:text-white">{role.permissionsCount}</span>
                  <span className="text-xs text-gray-500 uppercase tracking-tight">Permissions</span>
                </div>
                <div className="flex flex-col text-right">
                  <span className="text-xl font-bold text-gray-900 dark:text-white">{role.usersCount}</span>
                  <span className="text-xs text-gray-500 uppercase tracking-tight">Users</span>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </AdminLayout>
  );
}
