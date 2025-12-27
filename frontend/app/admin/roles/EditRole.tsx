import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router";
import { AdminLayout } from "../layout/AdminLayout";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { fetchAllPermissions } from "../../store/slices/permissionsSlice";
import { fetchRole, updateRole, clearCurrentRole } from "../../store/slices/rolesSlice";
import { toast } from "react-hot-toast";

export function EditRolePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { permissions, isLoading: isLoadingPermissions } = useAppSelector((state) => state.permissions);
  const { currentRole, isLoading: isLoadingRole, error: roleError } = useAppSelector((state) => state.roles);
  
  const [roleName, setRoleName] = useState("");
  const [description, setDescription] = useState("");
  const [isFullAccess, setIsFullAccess] = useState(false);
  const [selectedPermissions, setSelectedPermissions] = useState<number[]>([]);

  useEffect(() => {
    dispatch(fetchAllPermissions());
    if (id) {
      dispatch(fetchRole(Number(id)));
    }
    return () => {
      dispatch(clearCurrentRole());
    };
  }, [dispatch, id]);

  useEffect(() => {
    if (currentRole) {
      setRoleName(currentRole.name);
      setDescription(currentRole.description);
      setIsFullAccess(currentRole.fullAccess); // Note: Role interface uses fullAccess, payload uses isFullAccess
      
      // Map existing permissions to IDs
      if (currentRole.permissions) {
        setSelectedPermissions(currentRole.permissions.map((p: any) => p.id));
      }
    }
  }, [currentRole]);

  useEffect(() => {
    if (roleError) {
      toast.error(roleError);
      navigate("/admin/roles");
    }
  }, [roleError, navigate]);

  const handlePermissionToggle = (id: number) => {
    setSelectedPermissions(prev => 
      prev.includes(id) 
        ? prev.filter(c => c !== id) 
        : [...prev, id]
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!roleName.trim()) {
      toast.error("Role name is required");
      return;
    }

    if (!id) return;

    try {
      await dispatch(updateRole({
        id: Number(id),
        payload: {
          name: roleName,
          description,
          permissionId: isFullAccess ? [] : selectedPermissions,
          isActive: currentRole?.active ?? true, // Preserve active status or default to true
          isFullAccess
        }
      })).unwrap();
      
      toast.success("Role updated successfully");
      navigate("/admin/roles");
    } catch (error: any) {
      toast.error(error || "Failed to update role");
    }
  };

  if (isLoadingRole) {
    return (
      <AdminLayout title="Edit Role">
        <div className="flex justify-center items-center h-64">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        </div>
      </AdminLayout>
    );
  }

  return (
    <AdminLayout title="Edit Role">
      <form onSubmit={handleSubmit} className="max-w-4xl mx-auto space-y-8">
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden">
          <div className="p-6 border-b border-gray-100 dark:border-gray-700">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">Role Information</h3>
            <p className="text-sm text-gray-500">Update the details of the role.</p>
          </div>
          <div className="p-6 space-y-6">
            <div className="grid grid-cols-1 gap-6">
              <div>
                <label htmlFor="roleName" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Role Name
                </label>
                <input
                  id="roleName"
                  type="text"
                  required
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all"
                  value={roleName}
                  onChange={(e) => setRoleName(e.target.value)}
                  placeholder="e.g. Content Manager"
                />
              </div>
              <div>
                <label htmlFor="description" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  Description
                </label>
                <textarea
                  id="description"
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 outline-none transition-all min-h-[100px]"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="What can this role do?"
                />
              </div>
              <div className="flex items-center space-x-3">
                <input
                  id="isFullAccess"
                  type="checkbox"
                  className="w-5 h-5 text-blue-600 border-gray-300 rounded focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600"
                  checked={isFullAccess}
                  onChange={(e) => setIsFullAccess(e.target.checked)}
                />
                <label htmlFor="isFullAccess" className="text-sm font-medium text-gray-700 dark:text-gray-300">
                  Full Access (Administrator)
                </label>
              </div>
              {isFullAccess && (
                 <p className="text-sm text-yellow-600 dark:text-yellow-400">
                   Note: Full access roles have unrestricted access to all resources. Permission selection is disabled.
                 </p>
              )}
            </div>
          </div>
        </div>

        <div className={`bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 overflow-hidden ${isFullAccess ? 'opacity-50 pointer-events-none' : ''}`}>
          <div className="p-6 border-b border-gray-100 dark:border-gray-700">
            <h3 className="text-lg font-bold text-gray-900 dark:text-white">Permissions</h3>
            <p className="text-sm text-gray-500">Assign specific permissions to this role.</p>
          </div>
          <div className="p-6">
            {isLoadingPermissions ? (
               <div className="flex justify-center p-4">
                 <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
               </div>
            ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {permissions.map((permission) => (
                <label
                  key={permission.id}
                  className={`flex items-center p-4 rounded-xl border-2 cursor-pointer transition-all ${
                    selectedPermissions.includes(permission.id)
                      ? "border-blue-500 bg-blue-50 dark:bg-blue-900/20"
                      : "border-gray-100 dark:border-gray-700 hover:border-blue-200 dark:hover:border-gray-600"
                  }`}
                >
                  <input
                    type="checkbox"
                    className="hidden"
                    checked={selectedPermissions.includes(permission.id)}
                    onChange={() => handlePermissionToggle(permission.id)}
                  />
                  <div className="flex-1">
                    <span className={`block font-semibold ${
                      selectedPermissions.includes(permission.id) ? "text-blue-700 dark:text-blue-400" : "text-gray-900 dark:text-white"
                    }`}>
                      {permission.name}
                    </span>
                    <span className="text-xs text-gray-500 font-medium">
                      {permission.category}
                    </span>
                  </div>
                  {selectedPermissions.includes(permission.id) && (
                    <svg className="w-5 h-5 text-blue-600" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                    </svg>
                  )}
                </label>
              ))}
            </div>
            )}
          </div>
        </div>

        <div className="flex justify-end space-x-4 pt-4">
          <button
            type="button"
            onClick={() => navigate("/admin/roles")}
            className="px-6 py-2 border border-gray-300 dark:border-gray-700 rounded-lg text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 shadow-md shadow-blue-500/20 transition-all"
          >
            Update Role
          </button>
        </div>
      </form>
    </AdminLayout>
  );
}
