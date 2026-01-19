import type { Route } from "./+types/post";
import { PostDetail } from "~/welcome/post/PostDetail";
import { useNavigate } from "react-router";
import { privateAxios } from "~/api/axiosInstance";

export function meta({ data }: Route.MetaArgs) {
  return [
    { title: "Blog Post | Blog Project" },
    { name: "description", content: "Read this interesting blog post" },
  ];
}

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  // We return a simple object to satisfy Route types if needed, 
  // but the real work happens in PostDetail client-side
  return { id: params.id };
}

export default function PostRoute() {
  const navigate = useNavigate();

  return <PostDetail onBack={async () => navigate("/")} />;
}
