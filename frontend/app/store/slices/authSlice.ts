import { createSlice, createAsyncThunk, type PayloadAction } from "@reduxjs/toolkit";
import { publicAxios } from "../../api/axiosInstance";

export interface UserData {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  id: number;
  name: string;
  email: string;
  permissions: string[];
  roles: string[];
}

export interface AuthResponse {
  timestamp: string;
  status: string;
  data: UserData;
  message: string;
  errors: string[];
}

interface AuthState {
  user: UserData | null;
  isLoading: boolean;
  error: string | null;
  isAuthenticated: boolean;
}

const getInitialAuthData = (): UserData | null => {
  if (typeof window === "undefined") return null;
  const data = localStorage.getItem("auth_data");
  if (!data) return null;
  try {
    return JSON.parse(data);
  } catch (e) {
    return null;
  }
};

const initialState: AuthState = {
  user: getInitialAuthData(),
  isLoading: false,
  error: null,
  isAuthenticated: !!getInitialAuthData(),
};

export const loginUser = createAsyncThunk<
  UserData,
  Record<string, string>,
  { rejectValue: string }
>(
  "auth/login",
  async (credentials, { rejectWithValue }) => {
    try {
      const response = await publicAxios.post<AuthResponse>("/auth/login", credentials);
      // Backend returns status: "SUCCESS" according to user documentation
      if (response.data.status === "SUCCESS") {
        localStorage.setItem("auth_data", JSON.stringify(response.data.data));
        return response.data.data;
      } else {
        return rejectWithValue(response.data.message || "Login failed");
      }
    } catch (error: any) {
      const errorMessage = 
        error.response?.data?.message || 
        error.response?.data?.errors?.[0] || 
        error.message || 
        "Something went wrong";
      return rejectWithValue(errorMessage);
    }
  }
);

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setCredentials: (state, action: PayloadAction<UserData>) => {
      state.user = action.payload;
      state.isAuthenticated = true;
      localStorage.setItem("auth_data", JSON.stringify(action.payload));
    },
    logout: (state) => {
      state.user = null;
      state.isAuthenticated = false;
      state.error = null;
      localStorage.removeItem("auth_data");
    },
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload;
        state.isAuthenticated = true;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload ?? "Login failed";
      });
  },
});

export const { logout, clearError, setCredentials } = authSlice.actions;
export default authSlice.reducer;
