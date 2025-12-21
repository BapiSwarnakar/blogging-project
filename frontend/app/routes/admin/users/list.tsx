import { UsersPage } from "../../../admin/users/UsersList";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Users - Admin Dashboard" },
    { name: "description", content: "Manage users" },
  ];
}

export default function AdminUsersPage() {
  return (
    <PrivateRoute requiredPermission="users.view">
      <UsersPage />
    </PrivateRoute>
  );
}
