import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./slices/authSlice";
import rolesReducer from "./slices/rolesSlice";
import permissionsReducer from "./slices/permissionsSlice";

export const store = configureStore({
  reducer: {
    auth: authReducer,
    roles: rolesReducer,
    permissions: permissionsReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export type AppStore = typeof store;
