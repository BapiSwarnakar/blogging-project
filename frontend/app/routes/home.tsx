import type { Route } from "./+types/home";
import { Welcome } from "~/welcome/welcome";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Technical Blog | Insights for the Modern Creator" },
    { name: "description", content: "Explore the latest insights in technology, design, and lifestyle on our community-driven blog platform." },
    { property: "og:title", content: "Technical Blog | Insights for the Modern Creator" },
    { property: "og:description", content: "Explore the latest insights in technology, design, and lifestyle on our community-driven blog platform." },
  ];
}

export default function Home() {
  return <Welcome />;
}
