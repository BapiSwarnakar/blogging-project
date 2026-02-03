import { useLocation, useNavigate } from "react-router";
import { useAppDispatch, useAppSelector } from "../store/hooks";
import { createPaymentOrder, verifyPayment, fetchCurrentSubscription, type PricingPlan } from "../store/slices/pricingSlice";
import { Navbar } from "./Navbar";
import { Footer } from "./Footer";
import { useState, useEffect } from "react";
import { ShieldCheck, CreditCard, User, Mail, Phone, ArrowLeft } from "lucide-react";
import toast from "react-hot-toast";

declare global {
  interface Window {
    Razorpay: any;
  }
}

export function Checkout() {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { user } = useAppSelector((state) => state.auth);
  const { isPaymentLoading } = useAppSelector((state) => state.pricing);

  const plan = location.state?.plan as PricingPlan;

  const [formData, setFormData] = useState({
    name: user?.name || "",
    email: user?.email || "",
    phone: "", // We don't have phone in user object yet
  });

  useEffect(() => {
    if (!plan) {
      toast.error("Please select a plan first");
      navigate("/pricing");
    }
  }, [plan, navigate]);

  if (!plan) return null;

  const handlePay = async () => {
    if (!formData.phone) {
      toast.error("Please enter your contact number");
      return;
    }

    try {
      const resultAction = await dispatch(createPaymentOrder({
        planId: plan.id,
        amount: plan.price
      }));

      if (createPaymentOrder.fulfilled.match(resultAction)) {
        const orderData = resultAction.payload;
        
        const options = {
          key: orderData.razorpayKey,
          amount: orderData.amount,
          currency: orderData.currency,
          name: "Blogging Platform",
          description: `Payment for ${plan.name} plan`,
          order_id: orderData.orderId,
          handler: async function (response: any) {
            const verifyResult = await dispatch(verifyPayment({
              razorpay_order_id: response.razorpay_order_id,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_signature: response.razorpay_signature,
              planId: plan.id.toString()
            }));

            if (verifyPayment.fulfilled.match(verifyResult)) {
              toast.success("Payment successful! Your subscription is active.");
              // Refresh subscription data to show "Plan Activated" in UI
              dispatch(fetchCurrentSubscription());
              navigate("/");
            } else {
              toast.error("Payment verification failed");
            }
          },
          prefill: {
            name: formData.name,
            email: formData.email,
            contact: formData.phone
          },
          theme: {
            color: "#2563eb"
          }
        };

        const rzp = new window.Razorpay(options);
        rzp.open();
      } else {
        toast.error((resultAction.payload as string) || "Failed to initiate payment");
      }
    } catch (err) {
      toast.error("Something went wrong");
      console.error(err);
    }
  };

  return (
    <main className="min-h-screen bg-[#fafafa] dark:bg-gray-950 transition-colors duration-500">
      <Navbar />
      
      <div className="pt-32 pb-20 px-4 max-w-4xl mx-auto">
        <button 
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-gray-500 hover:text-blue-600 mb-8 transition-colors group"
        >
          <ArrowLeft className="w-4 h-4 group-hover:-translate-x-1 transition-transform" />
          Back to Pricing
        </button>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
          {/* Order Summary */}
          <div className="space-y-8">
            <div>
              <h1 className="text-3xl font-black text-gray-900 dark:text-white mb-4">Checkout</h1>
              <p className="text-gray-500 dark:text-gray-400">Complete your payment to activate your plan.</p>
            </div>

            <div className="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-3xl p-8 shadow-sm">
              <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-6">Order Summary</h2>
              <div className="space-y-4">
                <div className="flex justify-between items-center pb-4 border-b border-gray-100 dark:border-gray-800">
                  <div>
                    <p className="font-bold text-gray-900 dark:text-white">{plan.name}</p>
                    <p className="text-sm text-gray-500">{plan.durationDays} Days Access</p>
                  </div>
                  <span className="font-black text-gray-900 dark:text-white">₹{plan.price}</span>
                </div>
                <div className="flex justify-between items-center pt-2">
                  <span className="text-gray-500 uppercase tracking-widest text-xs font-bold">Total Amount</span>
                  <span className="text-2xl font-black text-blue-600">₹{plan.price}</span>
                </div>
              </div>
            </div>

            <div className="flex items-center gap-3 p-4 bg-blue-50 dark:bg-blue-900/10 rounded-2xl border border-blue-100 dark:border-blue-900/20">
              <ShieldCheck className="w-5 h-5 text-blue-600" />
              <p className="text-sm text-blue-800 dark:text-blue-300">
                Your payment is encrypted and secured by <strong>Razorpay</strong>.
              </p>
            </div>
          </div>

          {/* Billing Details Form */}
          <div className="bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-3xl p-8 shadow-xl">
            <h2 className="text-lg font-bold text-gray-900 dark:text-white mb-8">Billing Details</h2>
            
            <div className="space-y-6">
              <div className="space-y-2">
                <label className="text-sm font-bold text-gray-700 dark:text-gray-300 flex items-center gap-2">
                  <User className="w-4 h-4" /> Full Name
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                  placeholder="John Doe"
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-bold text-gray-700 dark:text-gray-300 flex items-center gap-2">
                  <Mail className="w-4 h-4" /> Email Address
                </label>
                <input
                  type="email"
                  value={formData.email}
                  onChange={(e) => setFormData({...formData, email: e.target.value})}
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                  placeholder="john@example.com"
                />
              </div>

              <div className="space-y-2">
                <label className="text-sm font-bold text-gray-700 dark:text-gray-300 flex items-center gap-2">
                  <Phone className="w-4 h-4" /> Contact Number
                </label>
                <input
                  type="tel"
                  value={formData.phone}
                  onChange={(e) => setFormData({...formData, phone: e.target.value})}
                  className="w-full px-4 py-3 rounded-xl border border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-blue-500 transition-all"
                  placeholder="+91 98765 43210"
                />
              </div>

              <button
                onClick={handlePay}
                disabled={isPaymentLoading}
                className="w-full mt-4 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white py-4 rounded-2xl font-black flex items-center justify-center gap-2 shadow-lg shadow-blue-600/30 transition-all active:scale-[0.98]"
              >
                {isPaymentLoading ? (
                  <div className="w-5 h-5 border-2 border-white/20 border-t-white rounded-full animate-spin"></div>
                ) : (
                  <>
                    <CreditCard className="w-5 h-5" />
                    Pay Now ₹{plan.price}
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      </div>
      <Footer />
    </main>
  );
}
