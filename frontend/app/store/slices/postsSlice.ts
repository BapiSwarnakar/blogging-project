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
  authorName: string;
  category: Category;
  image: string;
  type: PostType;
  viewCount: number;
  voteCount: number;
  commentCount: number;
  isBookmarked: boolean;
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
      const response = await privateAxios.get(`/user/public/posts`, {
        params: { page, size, sortBy, direction, search, type },
      });
      // Extract data from GlobalApiResponse
      return response.data.data || response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch posts");
    }
  }
);

export const fetchPostById = createAsyncThunk(
  "posts/fetchPostById",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get(`/user/public/posts/${id}`);
      return response.data.data || response.data;
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
      return response.data.data || response.data;
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
      return response.data.data || response.data;
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

export const votePost = createAsyncThunk(
  "posts/votePost",
  async ({ id, type }: { id: number; type: number }, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post(`/user/public/posts/${id}/vote?type=${type}`);
      return response.data.data || response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to vote");
    }
  }
);

export const incrementPostView = createAsyncThunk(
  "posts/incrementPostView",
  async (id: number, { rejectWithValue }) => {
    try {
      await privateAxios.post(`/user/public/posts/${id}/view`);
      return id;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to increment view");
    }
  }
);

export const bookmarkPost = createAsyncThunk(
  "posts/bookmarkPost",
  async (id: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post(`/user/blog/posts/${id}/bookmark`);
      return response.data.data || response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to bookmark");
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
        state.currentPost = action.payload;
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
      })
      .addCase(votePost.fulfilled, (state, action) => {
        const updatedPost = action.payload;
        if (state.currentPost && state.currentPost.id === updatedPost.id) {
          state.currentPost = updatedPost;
        }
        const index = state.posts.findIndex(p => p.id === updatedPost.id);
        if (index !== -1) {
          state.posts[index] = updatedPost;
        }
      })
      .addCase(bookmarkPost.fulfilled, (state, action) => {
        const updatedPost = action.payload;
        if (state.currentPost && state.currentPost.id === updatedPost.id) {
          state.currentPost = updatedPost;
        }
        const index = state.posts.findIndex(p => p.id === updatedPost.id);
        if (index !== -1) {
          state.posts[index] = updatedPost;
        }
      });
  },
});

export const { clearError, clearCurrentPost } = postsSlice.actions;
export default postsSlice.reducer;
