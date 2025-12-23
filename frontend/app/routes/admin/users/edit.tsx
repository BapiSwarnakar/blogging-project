import { EditUserPage } from "../../../admin/users/EditUser";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Edit User - Admin Dashboard" },
    { name: "description", content: "Edit user information" },
  ];
}

export default function AdminEditUserPage() {
  return (
    <PrivateRoute requiredPermission="USER_UPDATE">
      <EditUserPage />
    </PrivateRoute>
  );
}
