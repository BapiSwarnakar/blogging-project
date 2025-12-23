import { CreatePermissionPage } from "../../../admin/permissions/CreatePermission";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Create Permission - Admin Dashboard" },
  ];
}

export default function AdminCreatePermissionPage() {
  return (
    <PrivateRoute requiredPermission="PERMISSION_CREATE">
      <CreatePermissionPage />
    </PrivateRoute>
  );
}
