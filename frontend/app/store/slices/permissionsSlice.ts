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

interface PermissionsState {
  permissions: Permission[];
  isLoading: boolean;
  error: string | null;
}

const initialState: PermissionsState = {
  permissions: [],
  isLoading: false,
  error: null,
};

export const fetchPermissions = createAsyncThunk(
  "permissions/fetchPermissions",
  async (_, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get("/auth/permissions");
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch permissions");
    }
  }
);

const permissionsSlice = createSlice({
  name: "permissions",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchPermissions.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchPermissions.fulfilled, (state, action) => {
        state.isLoading = false;
        state.permissions = action.payload.data;
      })
      .addCase(fetchPermissions.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });
  },
});

export default permissionsSlice.reducer;
