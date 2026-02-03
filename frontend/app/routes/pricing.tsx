import { Pricing } from "../welcome/Pricing";

export function meta() {
  return [
    { title: "Pricing - Blogging Platform" },
    { name: "description", content: "Choose the best plan for your blogging needs" },
  ];
}

export default function PricingPage() {
  return <Pricing />;
}


