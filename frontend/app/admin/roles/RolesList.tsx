import { useState, useEffect } from "react";
import { Link } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { fetchRoles, deleteRole } from "../../store/slices/rolesSlice";
import { toast } from "react-hot-toast";
import { showConfirmDialog } from "../../utils/sweetAlert";

export function RolesList() {
  const [searchTerm, setSearchTerm] = useState("");
  const dispatch = useAppDispatch();
  const { roles, isLoading, error, pageInfo } = useAppSelector((state) => state.roles);

  useEffect(() => {
    const handler = setTimeout(() => {
      dispatch(fetchRoles({ page: 0, size: 9, search: searchTerm }));
    }, 500);

    return () => {
      clearTimeout(handler);
    };
  }, [dispatch, searchTerm]);

  const handlePageChange = (newPage: number) => {
    if (newPage >= 0 && newPage < pageInfo.totalPages) {
      dispatch(fetchRoles({ page: newPage, size: pageInfo.size, search: searchTerm }));
    }
  };

  const handleDelete = async (id: number) => {
    const result = await showConfirmDialog({
      title: "Are you sure?",
      text: "You won't be able to revert this!",
      confirmButtonText: "Yes, delete it!",
    });

    if (result.isConfirmed) {
      try {
        const actionResult = await dispatch(deleteRole(id)).unwrap();
        toast.success(actionResult.message || "Role deleted successfully");
      } catch (err: any) {
        toast.error(err || "Failed to delete role");
      }
    }
  };


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
        {isLoading ? (
          <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {roles.map((role) => (
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
                      <button 
                        onClick={() => handleDelete(role.id)}
                        className="p-1 text-gray-400 hover:text-red-600 transition-colors"
                      >
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
                      <span className="text-xl font-bold text-gray-900 dark:text-white">{role.permissions?.length || 0}</span>
                      <span className="text-xs text-gray-500 uppercase tracking-tight">Permissions</span>
                    </div>
                    <div className="flex flex-col text-right">
                      <span className="text-xl font-bold text-gray-900 dark:text-white">{role.userIds?.length || role.users?.length || 0}</span>
                      <span className="text-xs text-gray-500 uppercase tracking-tight">Users</span>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination Controls */}
            {pageInfo.totalPages > 1 && (
              <div className="flex justify-center items-center mt-6 space-x-2">
                <button
                  onClick={() => handlePageChange(pageInfo.number - 1)}
                  disabled={pageInfo.number === 0}
                  className="px-3 py-1 rounded bg-gray-200 dark:bg-gray-700 disabled:opacity-50"
                >
                  Previous
                </button>
                <span className="text-gray-600 dark:text-gray-400">
                  Page {pageInfo.number + 1} of {pageInfo.totalPages}
                </span>
                <button
                  onClick={() => handlePageChange(pageInfo.number + 1)}
                  disabled={pageInfo.number === pageInfo.totalPages - 1}
                  className="px-3 py-1 rounded bg-gray-200 dark:bg-gray-700 disabled:opacity-50"
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </AdminLayout>
  );
}
