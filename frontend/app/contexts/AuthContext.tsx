import { createContext, useContext, useState, useEffect, useMemo, type ReactNode } from "react";

interface User {
  id: string;
  name: string;
  email: string;
  role: string;
  permissions: string[];
}

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  hasPermission: (permission: string) => boolean;
  hasAnyPermission: (permissions: string[]) => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  readonly children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in (from localStorage or API)
    const checkAuth = async () => {
      try {
        const storedUser = localStorage.getItem("user");
        if (storedUser) {
          setUser(JSON.parse(storedUser));
        }
      } catch (error) {
        console.error("Error checking auth:", error);
      } finally {
        setIsLoading(false);
      }
    };

    checkAuth();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      // Mock login implementation
      const mockUser: User = {
        id: "1",
        name: "Admin User",
        email: email,
        role: "admin",
        permissions: ["users.create", "users.edit", "users.delete", "posts.view", "posts.create"],
      };

      setUser(mockUser);
      localStorage.setItem("user", JSON.stringify(mockUser));
    } catch (error) {
      console.error("Login error:", error);
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem("user");
  };

  const hasPermission = (permission: string): boolean => {
    if (!user) return false;
    // Admin role has all permissions
    if (user.role === "admin") return true;
    return user.permissions.includes(permission);
  };

  const hasAnyPermission = (permissions: string[]): boolean => {
    if (!user) return false;
    if (user.role === "admin") return true;
    return permissions.some((permission) => user.permissions.includes(permission));
  };

  const value = useMemo(() => ({
    user,
    isAuthenticated: !!user,
    isLoading,
    login,
    logout,
    hasPermission,
    hasAnyPermission,
  }), [user, isLoading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
