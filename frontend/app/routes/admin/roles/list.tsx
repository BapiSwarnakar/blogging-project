import { RolesList } from "../../../admin/roles/RolesList";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Roles Management - Admin Dashboard" },
  ];
}

export default function AdminRolesListPage() {
  return (
    <PrivateRoute requiredPermission="ROLE_READ">
      <RolesList />
    </PrivateRoute>
  );
}
