import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { privateAxios } from "../../api/axiosInstance";

export interface Permission {
  id: number;
  name: string;
  category: string;
  slug: string;
  apiUrl: string;
  apiMethod: string;
  description: string;
}

interface PageInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

interface PermissionsState {
  permissions: Permission[];
  currentPermission: Permission | null;
  pageInfo: PageInfo | null;
  isLoading: boolean;
  error: string | null;
}

const initialState: PermissionsState = {
  permissions: [],
  currentPermission: null,
  pageInfo: null,
  isLoading: false,
  error: null,
};

export interface FetchPermissionsParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
  search?: string;
}

export const fetchPermissions = createAsyncThunk(
  "permissions/fetchPermissions",
  async (params: FetchPermissionsParams | undefined, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get("/auth/permissions", {
        params: {
          page: params?.page ?? 0,
          size: params?.size ?? 10,
          sortBy: params?.sortBy ?? "id",
          sortDir: params?.sortDir ?? "asc",
          search: params?.search ?? "",
        },
      });
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch permissions");
    }
  }
);

export const fetchAllPermissions = createAsyncThunk(
  "permissions/fetchAllPermissions",
  async (_, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get("/auth/permissions", {
        params: {
          page: 0,
          size: 1000,
          sortBy: "name",
          sortDir: "asc",
        },
      });
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch all permissions");
    }
  }
);

export const fetchPermission = createAsyncThunk(
  "permissions/fetchPermission",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get(`/auth/permissions/${id}`);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch permission");
    }
  }
);

export const createPermission = createAsyncThunk(
  "permissions/createPermission",
  async (payload: any, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post("/auth/permissions", payload);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to create permission");
    }
  }
);

export const updatePermission = createAsyncThunk(
  "permissions/updatePermission",
  async ({ id, payload }: { id: number; payload: any }, { rejectWithValue }) => {
    try {
      const response = await privateAxios.put(`/auth/permissions/${id}`, payload);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to update permission");
    }
  }
);

export const deletePermission = createAsyncThunk(
  "permissions/deletePermission",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.delete(`/auth/permissions/${id}`);
      return { id, message: response.data.message };
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to delete permission");
    }
  }
);

const permissionsSlice = createSlice({
  name: "permissions",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentPermission: (state) => {
      state.currentPermission = null;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchPermissions.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchPermissions.fulfilled, (state, action) => {
        state.isLoading = false;
        state.permissions = action.payload.data;
        state.pageInfo = action.payload.pageInfo;
      })
      .addCase(fetchPermissions.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Fetch All
      .addCase(fetchAllPermissions.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchAllPermissions.fulfilled, (state, action) => {
        state.isLoading = false;
        state.permissions = action.payload.data;
      })
      .addCase(fetchAllPermissions.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Fetch Single
      .addCase(fetchPermission.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchPermission.fulfilled, (state, action) => {
        state.isLoading = false;
        state.currentPermission = action.payload.data;
      })
      .addCase(fetchPermission.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Create
      .addCase(createPermission.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(createPermission.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(createPermission.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Update
      .addCase(updatePermission.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(updatePermission.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(updatePermission.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      // Delete
      .addCase(deletePermission.fulfilled, (state, action) => {
        state.permissions = state.permissions.filter(p => p.id !== action.payload.id);
      });
  },
});

export const { clearError, clearCurrentPermission } = permissionsSlice.actions;
export default permissionsSlice.reducer;
