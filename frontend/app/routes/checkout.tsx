import { Checkout } from "../welcome/Checkout";

export function meta() {
  return [
    { title: "Checkout - Blogging Platform" },
    { name: "description", content: "Complete your subscription payment" },
  ];
}

export default function CheckoutPage() {
  return <Checkout />;
}
