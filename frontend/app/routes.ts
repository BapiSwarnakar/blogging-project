import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
    index("routes/home.tsx"),
    route("login", "routes/login.tsx"),
    route("signup", "routes/signup.tsx"),
    route("admin", "routes/admin/dashboard.tsx"),
    route("admin/users", "routes/admin/users/list.tsx"),
    route("admin/users/create", "routes/admin/users/create.tsx"),
    route("admin/users/:id/edit", "routes/admin/users/edit.tsx"),
    route("admin/roles", "routes/admin/roles/list.tsx"),
    route("admin/roles/create", "routes/admin/roles/create.tsx"),
    route("admin/roles/:id/edit", "routes/admin/roles/edit.tsx"),
    route("admin/permissions", "routes/admin/permissions/list.tsx"),
    route("admin/permissions/create", "routes/admin/permissions/create.tsx"),
    route("admin/permissions/:id/edit", "routes/admin/permissions/edit.tsx"),
    route("posts/:id", "routes/post.tsx"),
    route("*", "routes/$.tsx"), // Catch-all route for 404
] satisfies RouteConfig;
