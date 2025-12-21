import { PermissionsList } from "../../../admin/permissions/PermissionsList";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Permissions Management - Admin Dashboard" },
  ];
}

export default function AdminPermissionsListPage() {
  return (
    <PrivateRoute requiredPermission="permissions.view">
      <PermissionsList />
    </PrivateRoute>
  );
}
