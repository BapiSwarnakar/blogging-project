import { useEffect } from "react";
import { useNavigate } from "react-router";
import { useAuth } from "../contexts/AuthContext";

interface PrivateRouteProps {
  readonly children: React.ReactNode;
  readonly requiredPermission?: string;
  readonly requiredPermissions?: string[];
  readonly requireAll?: boolean; // If true, user must have ALL permissions; if false, ANY permission
}

export function PrivateRoute({
  children,
  requiredPermission,
  requiredPermissions,
  requireAll = false,
}: PrivateRouteProps) {
  const { isAuthenticated, isLoading, hasPermission, hasAnyPermission } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      // Redirect to login if not authenticated
      navigate("/login", { replace: true });
    }
  }, [isAuthenticated, isLoading, navigate]);

  // Show loading state
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-950">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600 dark:text-gray-400">Loading...</p>
        </div>
      </div>
    );
  }

  // Not authenticated
  if (!isAuthenticated) {
    return null;
  }

  // Check permissions if required
  if (requiredPermission && !hasPermission(requiredPermission)) {
    return <PermissionDenied />;
  }

  if (requiredPermissions && requiredPermissions.length > 0) {
    if (requireAll) {
      // User must have ALL permissions
      const hasAllPermissions = requiredPermissions.every((perm) => hasPermission(perm));
      if (!hasAllPermissions) {
        return <PermissionDenied />;
      }
    } else if (!hasAnyPermission(requiredPermissions)) {
      // User must have ANY permission
      return <PermissionDenied />;
    }
  }

  // User is authenticated and has required permissions
  return <>{children}</>;
}

function PermissionDenied() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-950">
      <div className="text-center max-w-md px-4">
        <div className="mb-8">
          <svg
            className="w-24 h-24 mx-auto text-red-500"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
            />
          </svg>
        </div>
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-4">
          Access Denied
        </h1>
        <p className="text-lg text-gray-600 dark:text-gray-400 mb-8">
          You don't have permission to access this page. Please contact your administrator if you
          believe this is an error.
        </p>
        <div className="flex gap-4 justify-center">
          <button
            onClick={() => navigate("/")}
            className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            Go Home
          </button>
        </div>
      </div>
    </div>
  );
}
