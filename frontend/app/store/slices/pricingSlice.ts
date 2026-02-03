import { createSlice, createAsyncThunk, type PayloadAction } from "@reduxjs/toolkit";
import { publicAxios, privateAxios } from "../../api/axiosInstance";

export interface PricingPlan {
  id: number;
  name: string;
  price: number;
  postLimit: number;
  durationDays: number;
  description: string;
}

export interface UserSubscription {
  id: number;
  userId: number;
  plan: PricingPlan;
  startDate: string;
  endDate: string;
  status: "ACTIVE" | "EXPIRED" | "CANCELLED" | "UPGRADED";
  remainingPosts?: number;
}

export interface PaymentOrder {
  orderId: string;
  razorpayKey: string;
  amount: number;
  currency: string;
  status: string;
}

interface PricingState {
  plans: PricingPlan[];
  currentSubscription: UserSubscription | null;
  paymentOrder: PaymentOrder | null;
  isLoading: boolean;
  isPaymentLoading: boolean;
  error: string | null;
}

const initialState: PricingState = {
  plans: [],
  currentSubscription: null,
  paymentOrder: null,
  isLoading: false,
  isPaymentLoading: false,
  error: null,
};

// Fetch all pricing plans (public)
export const fetchPricingPlans = createAsyncThunk<
  PricingPlan[],
  void,
  { rejectValue: string }
>(
  "pricing/fetchPlans",
  async (_, { rejectWithValue }) => {
    try {
      const response = await publicAxios.get("/payment/public/pricing-plans");
      if (response.data.status === "SUCCESS") {
        return response.data.data;
      }
      return rejectWithValue(response.data.message || "Failed to fetch plans");
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to fetch pricing plans"
      );
    }
  }
);


// Fetch current user subscription
export const fetchCurrentSubscription = createAsyncThunk<
  UserSubscription,
  void,
  { rejectValue: string }
>(
  "pricing/fetchSubscription",
  async (_, { rejectWithValue }) => {
    try {
      const response = await privateAxios.get("/payment/current-subscription");
      if (response.data.status === "SUCCESS") {
        return response.data.data;
      }
      return rejectWithValue(response.data.message || "Failed to fetch subscription");
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to fetch subscription"
      );
    }
  }
);

// Create payment order
export const createPaymentOrder = createAsyncThunk<
  PaymentOrder,
  { planId: number; amount: number },
  { rejectValue: string }
>(
  "pricing/createOrder",
  async (payload, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post("/payment/create-order", {
        planId: payload.planId,
        amount: payload.amount,
        currency: "INR",
      });
      if (response.data.status === "SUCCESS") {
        return response.data.data;
      }
      return rejectWithValue(response.data.message || "Failed to create order");
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || "Failed to create payment order"
      );
    }
  }
);

// Verify payment
export const verifyPayment = createAsyncThunk<
  string,
  {
    razorpay_order_id: string;
    razorpay_payment_id: string;
    razorpay_signature: string;
    planId: string;
  },
  { rejectValue: string }
>(
  "pricing/verifyPayment",
  async (payload, { rejectWithValue }) => {
    try {
      const response = await privateAxios.post("/payment/verify-payment", payload);
      if (response.data.status === "SUCCESS") {
        return response.data.data;
      }
      return rejectWithValue(response.data.message || "Payment verification failed");
    } catch (error: any) {
      return rejectWithValue(
        error.response?.data?.message || "Payment verification failed"
      );
    }
  }
);

const pricingSlice = createSlice({
  name: "pricing",
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearPaymentOrder: (state) => {
      state.paymentOrder = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch pricing plans
      .addCase(fetchPricingPlans.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchPricingPlans.fulfilled, (state, action) => {
        state.isLoading = false;
        state.plans = action.payload;
      })
      .addCase(fetchPricingPlans.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload ?? "Failed to fetch plans";
      })
      // Fetch subscription
      .addCase(fetchCurrentSubscription.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchCurrentSubscription.fulfilled, (state, action) => {
        state.isLoading = false;
        state.currentSubscription = action.payload;
      })
      .addCase(fetchCurrentSubscription.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload ?? "Failed to fetch subscription";
      })
      // Create payment order
      .addCase(createPaymentOrder.pending, (state) => {
        state.isPaymentLoading = true;
        state.error = null;
      })
      .addCase(createPaymentOrder.fulfilled, (state, action) => {
        state.isPaymentLoading = false;
        state.paymentOrder = action.payload;
      })
      .addCase(createPaymentOrder.rejected, (state, action) => {
        state.isPaymentLoading = false;
        state.error = action.payload ?? "Failed to create order";
      })
      // Verify payment
      .addCase(verifyPayment.pending, (state) => {
        state.isPaymentLoading = true;
        state.error = null;
      })
      .addCase(verifyPayment.fulfilled, (state) => {
        state.isPaymentLoading = false;
        state.paymentOrder = null;
      })
      .addCase(verifyPayment.rejected, (state, action) => {
        state.isPaymentLoading = false;
        state.error = action.payload ?? "Payment verification failed";
      });
  },
});

export const { clearError, clearPaymentOrder } = pricingSlice.actions;
export default pricingSlice.reducer;
