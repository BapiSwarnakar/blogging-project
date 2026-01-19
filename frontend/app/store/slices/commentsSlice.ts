import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { privateAxios } from "../../api/axiosInstance";

export interface Comment {
  id: number;
  content: string;
  authorId: number;
  authorName: string;
  postId: number;
  parentId: number | null;
  replies: Comment[];
  createdAt: string;
  updatedAt: string;
}

export interface CommentRequest {
  content: string;
  postId: number;
  parentId?: number | null;
  authorName: string;
}

interface CommentsState {
  comments: Comment[];
  isLoading: boolean;
  error: string | null;
}

const initialState: CommentsState = {
  comments: [],
  isLoading: false,
  error: null,
};

export const fetchCommentsByPostId = createAsyncThunk(
  "comments/fetchByPostId",
  async (postId: number, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get(`/user/public/posts/${postId}/comments`);
      return response.data.data || response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to fetch comments");
    }
  }
);

export const createComment = createAsyncThunk(
  "comments/create",
  async (payload: CommentRequest, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post("/user/comments", payload);
      return response.data.data || response.data;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to post comment");
    }
  }
);

export const deleteComment = createAsyncThunk(
  "comments/delete",
  async (id: number, { rejectWithValue }) => {
    try {
      await privateAxios.delete(`/user/comments/${id}`);
      return id;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.message || "Failed to delete comment");
    }
  }
);

const commentsSlice = createSlice({
  name: "comments",
  initialState,
  reducers: {
    clearComments: (state) => {
      state.comments = [];
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCommentsByPostId.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchCommentsByPostId.fulfilled, (state, action) => {
        state.isLoading = false;
        state.comments = action.payload;
      })
      .addCase(fetchCommentsByPostId.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      })
      .addCase(createComment.fulfilled, (state, action) => {
        const newComment = action.payload;
        if (newComment.parentId) {
            // Find parent and add to replies
            const addReply = (comments: Comment[]): boolean => {
                for (let comment of comments) {
                    if (comment.id === newComment.parentId) {
                        comment.replies = [newComment, ...comment.replies];
                        return true;
                    }
                    if (comment.replies && addReply(comment.replies)) return true;
                }
                return false;
            };
            addReply(state.comments);
        } else {
            state.comments = [newComment, ...state.comments];
        }
      })
      .addCase(deleteComment.fulfilled, (state, action) => {
        const commentId = action.payload;
        // Recursive removal
        const removeComment = (comments: Comment[]): Comment[] => {
            return comments
                .filter(c => c.id !== commentId)
                .map(c => ({
                    ...c,
                    replies: removeComment(c.replies || [])
                }));
        };
        state.comments = removeComment(state.comments);
      });
  },
});

export const { clearComments } = commentsSlice.actions;
export default commentsSlice.reducer;
