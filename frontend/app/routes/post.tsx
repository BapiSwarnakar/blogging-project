import type { Route } from "./+types/post";
import { PostDetail } from "~/welcome/post/PostDetail";
import { MOCK_POSTS } from "~/welcome/post/PostList";
import { useParams, useNavigate } from "react-router";

export function meta({ data }: Route.MetaArgs) {
  const post = data?.post;
  if (!post) {
    return [{ title: "Post Not Found" }];
  }
  return [
    { title: `${post.title} | Blog Project` },
    { name: "description", content: post.excerpt },
    { property: "og:title", content: post.title },
    { property: "og:description", content: post.excerpt },
    { property: "og:image", content: post.image },
    { name: "twitter:card", content: "summary_large_image" },
  ];
}

export async function loader({ params }: Route.LoaderArgs) {
  const post = MOCK_POSTS.find((p) => p.id === params.id);
  if (!post) {
    throw new Response("Not Found", { status: 404 });
  }
  return { post };
}

export default function PostRoute({ loaderData }: Route.ComponentProps) {
  const navigate = useNavigate();
  const { post } = loaderData;

  return <PostDetail post={post} onBack={() => navigate("/")} />;
}
