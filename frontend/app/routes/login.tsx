import { Login } from "../welcome/login";
import type { Route } from "./+types/login";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Login - Blogging Platform" },
    { name: "description", content: "Login to your account" },
  ];
}

export default function LoginPage() {
  return <Login />;
}
