import { useEffect } from "react";
import { useAppDispatch, useAppSelector } from "../store/hooks";
import { fetchPricingPlans, fetchCurrentSubscription, type PricingPlan } from "../store/slices/pricingSlice";
import { motion } from "framer-motion";
import { Check, Star, Zap, ShieldCheck, ArrowRight } from "lucide-react";
import { Navbar } from "./Navbar";
import { useNavigate } from "react-router";
import toast from "react-hot-toast";

import { Footer } from "./Footer";

export function Pricing() {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { plans, isLoading, error, currentSubscription } = useAppSelector((state) => state.pricing);
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  useEffect(() => {
    dispatch(fetchPricingPlans());
    if (isAuthenticated) {
      dispatch(fetchCurrentSubscription());
    }
  }, [dispatch, isAuthenticated]);

  const isPlanActivated = (plan: PricingPlan) => {
    if (!isAuthenticated) return false;
    
    const hasActiveSubscription = currentSubscription && currentSubscription.status === "ACTIVE";
    
    if (hasActiveSubscription) {
      return currentSubscription?.plan?.id === plan.id;
    }
    
    return plan.price === 0;
  };

  const handlePlanSelection = (plan: PricingPlan) => {
    if (isPlanActivated(plan)) {
      return;
    }
    
    if (isAuthenticated) {
      navigate("/checkout", { state: { plan } });
    } else {
      toast.error("Please sign up to continue");
      navigate("/signup");
    }
  };

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.1,
      },
    },
  };

  const itemVariants = {
    hidden: { y: 20, opacity: 0 },
    visible: {
      y: 0,
      opacity: 1,
      transition: {
        type: "spring" as const,
        damping: 25,
        stiffness: 200,
      },
    },
  };

  if (isLoading && plans.length === 0) {
    return (
      <div className="min-h-screen bg-[#fafafa] dark:bg-gray-950">
        <Navbar />
        <div className="flex items-center justify-center h-[calc(100vh-80px)]">
          <div className="w-10 h-10 border-4 border-blue-500/10 border-t-blue-500 rounded-full animate-spin"></div>
        </div>
      </div>
    );
  }

  if (error && plans.length === 0) {
    return (
      <div className="min-h-screen bg-[#fafafa] dark:bg-gray-950">
        <Navbar />
        <div className="flex items-center justify-center h-[calc(100vh-80px)] px-6">
          <div className="text-center p-8 bg-white dark:bg-gray-900 rounded-3xl border border-gray-200 dark:border-gray-800 shadow-xl max-w-md w-full">
            <ShieldCheck className="text-red-500 w-12 h-12 mx-auto mb-4" />
            <h2 className="text-xl font-bold mb-2 dark:text-white">Unable to Load Plans</h2>
            <p className="text-gray-500 dark:text-gray-400 mb-6">
              {error || "We encountered an error while fetching the pricing details."}
            </p>
            <button 
              onClick={() => dispatch(fetchPricingPlans())}
              className="w-full py-3 bg-blue-600 hover:bg-blue-500 text-white rounded-xl font-semibold transition-all"
            >
              Retry Connection
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <main className="min-h-screen bg-[#fafafa] dark:bg-gray-950 transition-colors duration-500">
      <Navbar />
      
      <div className="flex flex-col items-center pt-24 pb-20 px-4">
        <header className="flex flex-col items-center gap-4 mb-16 text-center">
          <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 text-xs font-bold uppercase tracking-widest">
            <Star className="w-3 h-3 fill-current" />
            Pricing Plans
          </div>
          <h1 className="text-4xl md:text-5xl font-black text-gray-900 dark:text-white tracking-tight">
            Simple, Transparent <span className="text-blue-600">Pricing.</span>
          </h1>
          <p className="text-gray-500 dark:text-gray-400 max-w-xl">
            Choose the perfect plan for your blogging journey. No hidden fees, just pure growth.
          </p>
        </header>

        <motion.div 
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="max-w-7xl w-full grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 px-4"
        >
          {plans.length === 0 ? (
            <div className="col-span-full text-center py-20 bg-white dark:bg-gray-900 rounded-3xl border border-gray-200 dark:border-gray-800 shadow-sm">
              <Zap className="w-12 h-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500 italic">No pricing plans available at the moment.</p>
            </div>
          ) : (
            plans.map((plan) => (
              <motion.div
                key={plan.id}
                variants={itemVariants}
                className="group relative flex flex-col bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-800 rounded-3xl p-8 hover:shadow-2xl hover:border-blue-500/30 transition-all duration-500"
              >
                {plan.price > 1000 && (
                  <div className="absolute -top-4 left-1/2 -translate-x-1/2 flex items-center gap-1.5 px-4 py-1.5 rounded-full bg-blue-600 text-white text-[11px] font-black uppercase tracking-widest shadow-xl shadow-blue-600/20 z-10">
                    <Zap className="w-3 h-3 fill-current" />
                    Most Popular
                  </div>
                )}

                <div className="mb-8">
                  <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2 group-hover:text-blue-600 transition-colors">
                    {plan.name}
                  </h3>
                  <p className="text-gray-500 dark:text-gray-400 text-sm leading-relaxed h-10 overflow-hidden line-clamp-2">
                    {plan.description || "Perfect for individuals starting their blogging journey."}
                  </p>
                </div>

                <div className="mb-8 flex items-baseline gap-1">
                  <span className="text-5xl font-black text-gray-900 dark:text-white tracking-tight">₹{plan.price}</span>
                  <span className="text-gray-500 text-sm font-medium">/ {plan.durationDays} days</span>
                </div>

                <div className="space-y-4 mb-10 flex-grow">
                  <FeatureItem text={`${plan.postLimit} High-quality Posts`} active />
                  <FeatureItem text="Unlimited draft storage" active />
                  <FeatureItem text="Analytical insights" active />
                  <FeatureItem text="Social media sharing" active />
                  {plan.price > 0 && <FeatureItem text="Priority support channel" active />}
                </div>

                <div className="space-y-4">
                  <button 
                    onClick={() => handlePlanSelection(plan)}
                    disabled={isPlanActivated(plan)}
                    className={`flex items-center justify-center gap-2 w-full py-4 rounded-2xl font-black text-sm transition-all duration-300 ${
                      isPlanActivated(plan)
                      ? "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400 cursor-default"
                      : plan.price === 0 
                        ? "bg-gray-100 hover:bg-gray-200 text-gray-900 dark:bg-gray-800 dark:hover:bg-gray-700 dark:text-white" 
                        : "bg-blue-600 hover:bg-blue-500 text-white shadow-lg shadow-blue-600/20"
                    }`}
                  >
                    {isPlanActivated(plan) ? (
                      <>
                        <Check className="w-4 h-4" />
                        Plan Activated
                      </>
                    ) : (
                      <>
                        {plan.price === 0 ? "Begin Journey" : "Upgrade Plan"}
                        <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                      </>
                    )}
                  </button>
                  
                  <div className="flex items-center justify-center gap-2 text-[10px] text-gray-400 uppercase tracking-widest font-bold">
                    <ShieldCheck className="w-3.5 h-3.5" />
                    Secure Payment
                  </div>
                </div>
              </motion.div>
            ))
          )}
        </motion.div>

      </div>
      <Footer />
    </main>
  );
}

function FeatureItem({ text, active = false }: { text: string; active?: boolean }) {
  return (
    <div className="flex items-center gap-2">
      <div className={`flex-shrink-0 w-5 h-5 rounded-full flex items-center justify-center ${active ? 'bg-blue-100 dark:bg-blue-900/30' : 'bg-gray-50 dark:bg-gray-800'}`}>
        <Check className={`w-3 h-3 ${active ? 'text-blue-600 dark:text-blue-400' : 'text-gray-400'}`} />
      </div>
      <span className={`text-xs ${active ? 'text-gray-900 dark:text-gray-200 font-medium' : 'text-gray-500 dark:text-gray-400'}`}>
        {text}
      </span>
    </div>
  );
}
