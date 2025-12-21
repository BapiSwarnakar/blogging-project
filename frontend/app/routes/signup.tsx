import { Signup } from "../welcome/signup";

export function meta() {
  return [
    { title: "Sign Up - Blogging Platform" },
    { name: "description", content: "Create your account and start blogging" },
  ];
}

export default function SignupPage() {
  return <Signup />;
}
