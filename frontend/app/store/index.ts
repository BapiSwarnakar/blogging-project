import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./slices/authSlice";
import rolesReducer from "./slices/rolesSlice";
import permissionsReducer from "./slices/permissionsSlice";
import usersReducer from "./slices/usersSlice";
import categoriesReducer from "./slices/categoriesSlice";
import postsReducer from "./slices/postsSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    roles: rolesReducer,
    permissions: permissionsReducer,
    users: usersReducer,
    categories: categoriesReducer,
    posts: postsReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export type AppStore = typeof store;
