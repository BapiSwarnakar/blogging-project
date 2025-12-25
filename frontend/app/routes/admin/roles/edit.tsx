import { EditRolePage } from "../../../admin/roles/EditRole";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Edit Role - Admin Dashboard" },
  ];
}

export default function AdminEditRolePage() {
  return (
    <PrivateRoute requiredPermission="ROLE_UPDATE">
      <EditRolePage />
    </PrivateRoute>
  );
}
