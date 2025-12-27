import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { privateAxios } from "../../api/axiosInstance";

export interface Role {
  id: number;
  name: string;
  description: string;
  active: boolean;
  fullAccess: boolean;
  createdAt: string;
  updatedAt: string;
  permissions: any[];
  users: any[] | null;
  userIds?: number[];
}

interface PageInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

interface RolesState {
  roles: Role[];
  currentRole: Role | null;
  isLoading: boolean;
  error: string | null;
  pageInfo: PageInfo;
}

const initialState: RolesState = {
  roles: [],
  currentRole: null,
  isLoading: false,
  error: null,
  pageInfo: {
    size: 10,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  },
};

interface FetchRolesArgs {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
  search?: string;
}

export const fetchRoles = createAsyncThunk(
  "roles/fetchRoles",
  async ({ page = 0, size = 10, sortBy = "id", sortDir = "asc", search = "" }: FetchRolesArgs = {}, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get(`/auth/roles`, {
        params: { page, size, sortBy, sortDir, search },
      });
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch roles");
    }
  }
);

export const fetchRole = createAsyncThunk(
  "roles/fetchRole",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get(`/auth/roles/${id}`);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch role");
    }
  }
);

export const deleteRole = createAsyncThunk(
  "roles/deleteRole",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.delete(`/auth/roles/${id}`);
      return { id, message: response.data.message };
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to delete role");
    }
  }
);

interface RolePayload {
  name: string;
  description: string;
  permissionId: number[];
  isActive: boolean;
  isFullAccess: boolean;
}

export const createRole = createAsyncThunk(
  "roles/createRole",
  async (payload: RolePayload, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post("/auth/roles", payload);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to create role");
    }
  }
);

export const updateRole = createAsyncThunk(
  "roles/updateRole",
  async ({ id, payload }: { id: number; payload: RolePayload }, { rejectWithValue }) => {
    try {
      const response = await privateAxios.put(`/auth/roles/${id}`, payload);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to update role");
    }
  }
);

const rolesSlice = createSlice({
  name: "roles",
  initialState,
  reducers: {
    clearCurrentRole: (state) => {
      state.currentRole = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchRoles.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchRoles.fulfilled, (state, action) => {
        state.isLoading = false;
        state.roles = action.payload.data;
        state.pageInfo = action.payload.pageInfo;
      })
      .addCase(fetchRoles.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(fetchRole.pending, (state) => {
        state.isLoading = true;
        state.error = null;
        state.currentRole = null;
      })
      .addCase(fetchRole.fulfilled, (state, action) => {
        state.isLoading = false;
        // The API returns { status, data: Role, ... } so we need action.payload.data
        state.currentRole = action.payload.data;
      })
      .addCase(fetchRole.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(deleteRole.fulfilled, (state, action) => {
        state.roles = state.roles.filter((role) => role.id !== action.payload.id);
        state.pageInfo.totalElements -= 1;
      })
      .addCase(deleteRole.rejected, (state, action) => {
        state.error = action.payload as string;
      });
  },
});

export const { clearCurrentRole } = rolesSlice.actions;

export default rolesSlice.reducer;
