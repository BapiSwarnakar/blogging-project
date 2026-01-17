import { CreatePostPage } from "../../../admin/posts/CreatePost";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Create Post - Admin Dashboard" },
    { name: "description", content: "Create a new blog post" },
  ];
}

export default function AdminCreatePostPage() {
  return (
    <PrivateRoute requiredPermission="POST_WRITE">
      <CreatePostPage />
    </PrivateRoute>
  );
}
