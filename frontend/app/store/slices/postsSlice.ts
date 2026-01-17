import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { privateAxios } from "../../api/axiosInstance";
import type { Category } from "./categoriesSlice";

export type PostType = "PUBLIC" | "PRIVATE";

export interface Post {
  id: number;
  title: string;
  excerpt: string;
  content: string;
  authorId: number;
  category: Category;
  image: string;
  type: PostType;
  viewCount: number;
  voteCount: number;
  answerCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface PostRequest {
  title: string;
  excerpt: string;
  content: string;
  categoryId: number;
  image?: string;
  type: PostType;
}

interface PageInfo {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

interface PostsState {
  posts: Post[];
  currentPost: Post | null;
  isLoading: boolean;
  error: string | null;
  pageInfo: PageInfo;
}

const initialState: PostsState = {
  posts: [],
  currentPost: null,
  isLoading: false,
  error: null,
  pageInfo: {
    size: 10,
    number: 0,
    totalElements: 0,
    totalPages: 0,
  },
};

interface FetchPostsArgs {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: string;
  search?: string;
  type?: PostType;
}

export const fetchPosts = createAsyncThunk(
  "posts/fetchPosts",
  async (
    { page = 0, size = 10, sortBy = "createdAt", direction = "desc", search = "", type }: FetchPostsArgs = {},
    { rejectWithValue }
  ) => {
    try {
      const response = await privateAxios.get(`/user/blog/posts`, {
        params: { page, size, sortBy, direction, search, type },
      });
      // Page response structure from Spring Data JPA
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch posts");
    }
  }
);

export const fetchPostById = createAsyncThunk(
  "posts/fetchPostById",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get(`/user/blog/posts/${id}`);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch post");
    }
  }
);

export const createPost = createAsyncThunk(
  "posts/createPost",
  async (payload: PostRequest, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post("/user/blog/posts", payload);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to create post");
    }
  }
);

export const updatePost = createAsyncThunk(
  "posts/updatePost",
  async ({ id, payload }: { id: number; payload: PostRequest }, { rejectWithValue }) => {
    try {
      const response = await privateAxios.put(`/user/blog/posts/${id}`, payload);
      return response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to update post");
    }
  }
);

export const deletePost = createAsyncThunk(
  "posts/deletePost",
  async (id: number, { rejectWithValue }) => {
    try {
      await privateAxios.delete(`/user/blog/posts/${id}`);
      return id;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to delete post");
    }
  }
);

const postsSlice = createSlice({
  name: "posts",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearCurrentPost: (state) => {
      state.currentPost = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchPosts.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchPosts.fulfilled, (state, action) => {
        state.isLoading = false;
        state.posts = action.payload.content;
        state.pageInfo = {
          size: action.payload.size,
          number: action.payload.number,
          totalElements: action.payload.totalElements,
          totalPages: action.payload.totalPages,
        };
      })
      .addCase(fetchPosts.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(fetchPostById.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchPostById.fulfilled, (state, action) => {
        state.isLoading = false;
        state.currentPost = action.payload.data ? action.payload.data : action.payload;
      })
      .addCase(fetchPostById.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(createPost.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(createPost.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(createPost.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(updatePost.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(updatePost.fulfilled, (state) => {
        state.isLoading = false;
      })
      .addCase(updatePost.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(deletePost.fulfilled, (state, action) => {
        state.posts = state.posts.filter((post) => post.id !== action.payload);
        state.pageInfo.totalElements -= 1;
      });
  },
});

export const { clearError, clearCurrentPost } = postsSlice.actions;
export default postsSlice.reducer;
