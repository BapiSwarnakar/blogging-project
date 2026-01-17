import { CreateCategory } from "../../../admin/categories/CreateCategory";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Create Category - Admin Dashboard" },
  ];
}

export default function AdminCreateCategoryPage() {
  return (
    <PrivateRoute>
      <CreateCategory />
    </PrivateRoute>
  );
}
