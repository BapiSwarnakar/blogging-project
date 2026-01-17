import { EditPostPage } from "../../../admin/posts/EditPost";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Edit Post - Admin Dashboard" },
    { name: "description", content: "Edit an existing blog post" },
  ];
}

export default function AdminEditPostPage() {
  return (
    <PrivateRoute requiredPermission="POST_UPDATE">
      <EditPostPage />
    </PrivateRoute>
  );
}
