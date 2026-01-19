import type { Config } from "@react-router/dev/config";

export default {
  // Config options...
  // Server-side render by default, to enable SPA mode set this to `false`
  // SSR is disabled to support localStorage-based authentication in SPA mode
  ssr: false,
} satisfies Config;
