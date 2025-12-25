import { useState, useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router";
import { useAuth } from "../../contexts/AuthContext";

interface SidebarProps {
  readonly isOpen: boolean;
  readonly onClose: () => void;
}

interface MenuItem {
  path: string;
  label: string;
  icon: React.ReactNode;
  permission?: string;
}

export function Sidebar({ isOpen, onClose }: SidebarProps) {
  const location = useLocation();
  const navigate = useNavigate();
  const { hasPermission, hasAnyPermission, logout, user } = useAuth();
  const [usersDropdownOpen, setUsersDropdownOpen] = useState(false);
  const [rolesDropdownOpen, setRolesDropdownOpen] = useState(false);
  const [permissionsDropdownOpen, setPermissionsDropdownOpen] = useState(false);

  // Auto-expand dropdowns when a child route is active
  useEffect(() => {
    if (location.pathname.startsWith("/admin/users")) setUsersDropdownOpen(true);
    if (location.pathname.startsWith("/admin/roles")) setRolesDropdownOpen(true);
    if (location.pathname.startsWith("/admin/permissions")) setPermissionsDropdownOpen(true);
  }, [location.pathname]);
  
  const isActive = (path: string) => {
    if (path === "/admin") {
      return location.pathname === "/admin";
    }
    return location.pathname.startsWith(path);
  };

  const isUsersActive = location.pathname.startsWith("/admin/users");
  const isRolesActive = location.pathname.startsWith("/admin/roles");
  const isPermissionsActive = location.pathname.startsWith("/admin/permissions");


  const handleLogout = async () => {
    await logout(user?.refreshToken);
    navigate("/login");
  };

  return (
    <aside
      className={`fixed top-0 left-0 z-40 h-screen transition-transform duration-300 ${isOpen ? "translate-x-0" : "-translate-x-full"
        } bg-white dark:bg-gray-900 border-r border-gray-200 dark:border-gray-800 w-64 flex flex-col`}
    >
      <div className="px-3 py-4 flex flex-col h-full">
        <div className="flex items-center justify-between mb-8 px-3">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
            Admin Panel
          </h2>
          <button
            onClick={onClose}
            className="lg:hidden p-2 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800"
          >
            <svg className="w-6 h-6 text-gray-600 dark:text-gray-400" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
            </svg>
          </button>
        </div>

        <ul className="space-y-2 font-medium flex-1 overflow-y-auto">
          <li key={"/admin"}>
            <Link
              to={"/admin"}
              className={`flex items-center p-3 rounded-lg transition-colors ${isActive("/admin")
                  ? "text-white bg-blue-600 hover:bg-blue-700"
                  : "text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800"
                }`}
            >
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z" clipRule="evenodd" />
              </svg>
              <span className="ml-3">Dashboard</span>
            </Link>
          </li>

          {/* Users Dropdown Menu */}
          {hasAnyPermission(["USER_READ", "USER_CREATE", "USER_UPDATE", "USER_DELETE"]) && (
            <li>
              <button
                onClick={() => setUsersDropdownOpen(!usersDropdownOpen)}
                className={`w-full flex items-center justify-between p-3 rounded-lg transition-colors ${isUsersActive
                    ? "text-white bg-blue-600 hover:bg-blue-700"
                    : "text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800"
                  }`}
              >
                <div className="flex items-center">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z" />
                  </svg>
                  <span className="ml-3">Users</span>
                </div>
                <svg
                  className={`w-4 h-4 transition-transform ${usersDropdownOpen ? "rotate-180" : ""}`}
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
                </svg>
              </button>

              {usersDropdownOpen && (
                <ul className="mt-2 space-y-1 pl-4">
                  {hasPermission("USER_READ") && (
                    <li>
                      <Link
                        to="/admin/users"
                        className={`flex items-center p-2 pl-4 rounded-lg transition-colors ${location.pathname === "/admin/users"
                            ? "text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20"
                            : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"
                          }`}
                      >
                        <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                          <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                          <path fillRule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clipRule="evenodd" />
                        </svg>
                        View Users
                      </Link>
                    </li>
                  )}
                  {hasPermission("USER_CREATE") && (
                    <li>
                      <Link
                        to="/admin/users/create"
                        className={`flex items-center p-2 pl-4 rounded-lg transition-colors ${location.pathname === "/admin/users/create"
                            ? "text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20"
                            : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"
                          }`}
                      >
                        <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd" />
                        </svg>
                        Create User
                      </Link>
                    </li>
                  )}
                </ul>
              )}
            </li>
          )}
          {/* Roles Dropdown Menu */}
          {hasAnyPermission(["ROLE_READ", "ROLE_CREATE", "ROLE_UPDATE", "ROLE_DELETE"]) && (
            <li>
              <button
                onClick={() => setRolesDropdownOpen(!rolesDropdownOpen)}
                className={`w-full flex items-center justify-between p-3 rounded-lg transition-colors ${isRolesActive
                    ? "text-white bg-blue-600 hover:bg-blue-700"
                    : "text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800"
                  }`}
              >
                <div className="flex items-center">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M6.267 3.455a3.066 3.066 0 001.745-.723 3.066 3.066 0 013.976 0 3.066 3.066 0 001.745.723 3.066 3.066 0 012.812 2.812c.051.643.304 1.254.723 1.745a3.066 3.066 0 010 3.976 3.066 3.066 0 00-.723 1.745 3.066 3.066 0 01-2.812 2.812 3.066 3.066 0 00-1.745.723 3.066 3.066 0 01-3.976 0 3.066 3.066 0 00-1.745-.723 3.066 3.066 0 01-2.812-2.812 3.066 3.066 0 00-.723-1.745 3.066 3.066 0 010-3.976 3.066 3.066 0 00.723-1.745 3.066 3.066 0 012.812-2.812zm7.44 5.252a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                  <span className="ml-3">Roles</span>
                </div>
                <svg
                  className={`w-4 h-4 transition-transform ${rolesDropdownOpen ? "rotate-180" : ""}`}
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
                </svg>
              </button>

              {rolesDropdownOpen && (
                <ul className="mt-2 space-y-1 pl-4">
                  {hasPermission("ROLE_READ") && (
                    <li>
                      <Link
                        to="/admin/roles"
                        className={`flex items-center p-2 pl-4 rounded-lg transition-colors ${location.pathname === "/admin/roles"
                            ? "text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20"
                            : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"
                          }`}
                      >
                        <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                          <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                          <path fillRule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clipRule="evenodd" />
                        </svg>
                        View Roles
                      </Link>
                    </li>
                  )}
                  {hasPermission("ROLE_CREATE") && (
                    <li>
                      <Link
                        to="/admin/roles/create"
                        className={`flex items-center p-2 pl-4 rounded-lg transition-colors ${location.pathname === "/admin/roles/create"
                            ? "text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20"
                            : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"
                          }`}
                      >
                        <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd" />
                        </svg>
                        Create Role
                      </Link>
                    </li>
                  )}
                </ul>
              )}
            </li>
          )}
          {/* Permissions Dropdown Menu */}
          {hasAnyPermission(["PERMISSION_READ", "PERMISSION_CREATE", "PERMISSION_UPDATE", "PERMISSION_DELETE"]) && (
            <li>
              <button
                onClick={() => setPermissionsDropdownOpen(!permissionsDropdownOpen)}
                className={`w-full flex items-center justify-between p-3 rounded-lg transition-colors ${isPermissionsActive
                    ? "text-white bg-blue-600 hover:bg-blue-700"
                    : "text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800"
                  }`}
              >
                <div className="flex items-center">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M18 8a6 6 0 11-12 0 6 6 0 0112 0zm-11.5 1.335A4 4 0 1011 4.586a4 4 0 00-4.5 4.749zm2.41 1.701L1.293 18.707a1 1 0 001.414 1.414L4 18.828V18a1 1 0 011-1h1.5l1.5-1.5V14a1 1 0 011-1H11v-1.172l-2.09-2.09z" clipRule="evenodd" />
                  </svg>
                  <span className="ml-3">Permissions</span>
                </div>
                <svg
                  className={`w-4 h-4 transition-transform ${permissionsDropdownOpen ? "rotate-180" : ""}`}
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
                </svg>
              </button>

              {permissionsDropdownOpen && (
                <ul className="mt-2 space-y-1 pl-4">
                  {hasPermission("PERMISSION_READ") && (
                    <li>
                      <Link
                        to="/admin/permissions"
                        className={`flex items-center p-2 pl-4 rounded-lg transition-colors ${location.pathname === "/admin/permissions"
                            ? "text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20"
                            : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"
                          }`}
                      >
                        <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                          <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                          <path fillRule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clipRule="evenodd" />
                        </svg>
                        View Permissions
                      </Link>
                    </li>
                  )}
                  {hasPermission("PERMISSION_CREATE") && (
                    <li>
                      <Link
                        to="/admin/permissions/create"
                        className={`flex items-center p-2 pl-4 rounded-lg transition-colors ${location.pathname === "/admin/permissions/create"
                            ? "text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-900/20"
                            : "text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800"
                          }`}
                      >
                        <svg className="w-4 h-4 mr-2" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z" clipRule="evenodd" />
                        </svg>
                        Create Permission
                      </Link>
                    </li>
                  )}
                </ul>
              )}
            </li>
          )}
        </ul>

        {/* Footer actions */}
        <div className="pt-4 mt-4 border-t border-gray-200 dark:border-gray-800 space-y-2">
          <Link
            to="/"
            className="flex items-center p-3 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
          >
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M3 3a1 1 0 00-1 1v12a1 1 0 102 0V4a1 1 0 00-1-1zm10.293 9.293a1 1 0 001.414 1.414l3-3a1 1 0 000-1.414l-3-3a1 1 0 10-1.414 1.414L14.586 9H7a1 1 0 100 2h7.586l-1.293 1.293z" clipRule="evenodd" />
            </svg>
            <span className="ml-3">Back to Site</span>
          </Link>
          <button
            onClick={handleLogout}
            className="w-full flex items-center p-3 text-red-600 dark:text-red-400 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            <span className="ml-3 font-medium">Logout</span>
          </button>
        </div>
      </div>
    </aside>
  );
}
