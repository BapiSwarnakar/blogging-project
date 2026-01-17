import { EditCategory } from "../../../admin/categories/EditCategory";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Edit Category - Admin Dashboard" },
  ];
}

export default function AdminEditCategoryPage() {
  return (
    <PrivateRoute>
      <EditCategory />
    </PrivateRoute>
  );
}
