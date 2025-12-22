import { createContext, useContext, useMemo, type ReactNode } from "react";
import { useAppDispatch, useAppSelector } from "../store/hooks";
import { loginUser, logout as logoutAction } from "../store/slices/authSlice";

interface AuthContextType {
  user: any;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  hasPermission: (permission: string) => boolean;
  hasAnyPermission: (permissions: string[]) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { readonly children: ReactNode }) {
  const { user, isAuthenticated, isLoading } = useAppSelector((state) => state.auth);
  const dispatch = useAppDispatch();

  const login = async (email: string, password: string) => {
    await dispatch(loginUser({ email, password }));
  };

  const logout = () => {
    dispatch(logoutAction());
  };

  const hasPermission = (permission: string): boolean => {
    if (!user) return false;
    // Check roles for full access or permissions array
    if (user.roles?.includes("ADMIN")) return true;
    return user.permissions?.includes(permission);
  };

  const hasAnyPermission = (permissions: string[]): boolean => {
    if (!user) return false;
    if (user.roles?.includes("ADMIN")) return true;
    return permissions.some((permission) => user.permissions?.includes(permission));
  };

  const value = useMemo(() => ({
    user,
    isAuthenticated,
    isLoading,
    login,
    logout,
    hasPermission,
    hasAnyPermission,
  }), [user, isAuthenticated, isLoading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
