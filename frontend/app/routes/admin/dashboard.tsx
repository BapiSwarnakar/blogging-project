import { AdminDashboard } from "../../admin/dashboard/Dashboard";
import { PrivateRoute } from "../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Admin Dashboard - Blogging Platform" },
    { name: "description", content: "Manage your blogging platform" },
  ];
}

export default function AdminDashboardPage() {
  return (
    <PrivateRoute>
      <AdminDashboard />
    </PrivateRoute>
  );
}
