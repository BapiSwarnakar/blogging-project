import { CreateRolePage } from "../../../admin/roles/CreateRole";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Create Role - Admin Dashboard" },
  ];
}

export default function AdminCreateRolePage() {
  return (
    <PrivateRoute requiredPermission="roles.create">
      <CreateRolePage />
    </PrivateRoute>
  );
}
