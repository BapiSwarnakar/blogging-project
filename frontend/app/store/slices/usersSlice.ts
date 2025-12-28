import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { privateAxios } from "../../api/axiosInstance";

export interface Role {
  id: number;
  name: string;
  description: string;
}

export interface User {
  id: number;
  firstName: string;
  middleName?: string;
  lastName: string;
  name: string;
  email: string;
  gender: "MALE" | "FEMALE" | "OTHER";
  phone: string;
  dateOfBirth: string;
  roles: Role[];
  permissions: string[];
  active: boolean;
  userStatus: "PENDING" | "APPROVED" | "REJECTED";
  createdAt: string;
  updatedAt: string;
}

export interface UserRequest {
  firstName: string;
  middleName?: string;
  lastName: string;
  email: string;
  password?: string;
  gender: "MALE" | "FEMALE" | "OTHER";
  phone: string;
  dateOfBirth: string;
  roles: string[];
  directPermissions?: string[];
  active: boolean;
  userStatus: "PENDING" | "APPROVED" | "REJECTED";
}

interface PageInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

interface UsersState {
  users: User[];
  currentUser: User | null;
  isLoading: boolean;
  error: string | null;
  pageInfo: PageInfo;
}

const initialState: UsersState = {
  users: [],
  currentUser: null,
  isLoading: false,
  error: null,
  pageInfo: {
    size: 10,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  },
};

interface FetchUsersArgs {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
  search?: string;
}

export const fetchUsers = createAsyncThunk(
  "users/fetchUsers",
  async ({ page = 0, size = 10, sortBy = "id", sortDir = "asc", search = "" }: FetchUsersArgs = {}, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get(`/auth/users`, {
        params: { page, size, sortBy, sortDir, search },
      });
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch users");
    }
  }
);

export const createUser = createAsyncThunk(
  "users/createUser",
  async (payload: UserRequest, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post("/auth/users", payload);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to create user");
    }
  }
);

export const updateUser = createAsyncThunk(
  "users/updateUser",
  async ({ id, payload }: { id: number; payload: UserRequest }, { rejectWithValue }) => {
    try {
      const response = await privateAxios.put(`/auth/users/${id}`, payload);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to update user");
    }
  }
);

export const fetchUserById = createAsyncThunk(
  "users/fetchUserById",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get(`/auth/users/${id}`);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch user");
    }
  }
);

export const deleteUser = createAsyncThunk(
  "users/deleteUser",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.delete(`/auth/users/${id}`);
      return { id, message: response.data.message };
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to delete user");
    }
  }
);

const usersSlice = createSlice({
  name: "users",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentUser: (state) => {
      state.currentUser = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchUsers.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchUsers.fulfilled, (state, action) => {
        state.isLoading = false;
        state.users = action.payload.data;
        state.pageInfo = action.payload.pageInfo;
      })
      .addCase(fetchUsers.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(createUser.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(createUser.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(createUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(fetchUserById.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchUserById.fulfilled, (state, action) => {
        state.isLoading = false;
        state.currentUser = action.payload.data;
      })
      .addCase(fetchUserById.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(deleteUser.fulfilled, (state, action) => {
        state.users = state.users.filter((user) => user.id !== action.payload.id);
        state.pageInfo.totalElements -= 1;
      });
  },
});

export const { clearError, clearCurrentUser } = usersSlice.actions;
export default usersSlice.reducer;
