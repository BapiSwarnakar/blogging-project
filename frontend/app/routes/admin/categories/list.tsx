import { CategoriesList } from "../../../admin/categories/CategoriesList";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Category Management - Admin Dashboard" },
  ];
}

export default function AdminCategoriesListPage() {
  return (
    <PrivateRoute>
      <CategoriesList />
    </PrivateRoute>
  );
}
