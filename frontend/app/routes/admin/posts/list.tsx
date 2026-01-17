import { PostsPage } from "../../../admin/posts/PostsList";
import { PrivateRoute } from "../../../components/PrivateRoute";

export function meta() {
  return [
    { title: "Posts - Admin Dashboard" },
    { name: "description", content: "Manage blog posts" },
  ];
}

export default function AdminPostsPage() {
  return (
    <PrivateRoute requiredPermission="POST_READ">
      <PostsPage />
    </PrivateRoute>
  );
}
