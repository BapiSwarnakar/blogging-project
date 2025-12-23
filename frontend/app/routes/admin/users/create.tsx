import { CreateUserPage } from "../../../admin/users/CreateUser";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Create User - Admin Dashboard" },
    { name: "description", content: "Create a new user" },
  ];
}

export default function AdminCreateUserPage() {
  return (
    <PrivateRoute requiredPermission="USER_CREATE">
      <CreateUserPage />
    </PrivateRoute>
  );
}
