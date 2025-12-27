import { EditPermissionPage } from "../../../admin/permissions/EditPermission";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Edit Permission - Admin Dashboard" },
  ];
}

export default function AdminEditPermissionPage() {
  return (
    <PrivateRoute requiredPermission="PERMISSION_UPDATE">
      <EditPermissionPage />
    </PrivateRoute>
  );
}
