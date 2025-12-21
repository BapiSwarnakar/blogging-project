import { Link } from "react-router";

export function Signup() {
  return (
    <main className="flex items-center justify-center pt-16 pb-4 min-h-screen">
      <div className="flex-1 flex flex-col items-center gap-16 min-h-0">
        <header className="flex flex-col items-center gap-4">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Create Account</h1>
          <p className="text-gray-500 dark:text-gray-400 text-center">
            Join our blogging community today
          </p>
        </header>
        
        <div className="max-w-[400px] w-full space-y-6 px-4">
          <form className="rounded-3xl border border-gray-200 p-8 dark:border-gray-700 space-y-6 bg-white dark:bg-gray-900 shadow-xl" onSubmit={(e) => e.preventDefault()}>
            <div className="space-y-4">
              <div className="space-y-2">
                <label 
                  htmlFor="fullname" 
                  className="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Full Name
                </label>
                <input
                  id="fullname"
                  name="fullname"
                  type="text"
                  required
                  placeholder="John Doe"
                  className="block w-full px-4 py-3 rounded-xl border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                />
              </div>

              <div className="space-y-2">
                <label 
                  htmlFor="email" 
                  className="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Email address
                </label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  required
                  placeholder="name@example.com"
                  className="block w-full px-4 py-3 rounded-xl border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                />
              </div>
              
              <div className="space-y-2">
                <label 
                  htmlFor="password" 
                  className="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Password
                </label>
                <input
                  id="password"
                  name="password"
                  type="password"
                  required
                  placeholder="••••••••"
                  className="block w-full px-4 py-3 rounded-xl border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                />
              </div>

              <div className="space-y-2">
                <label 
                  htmlFor="confirmPassword" 
                  className="block text-sm font-medium text-gray-700 dark:text-gray-300"
                >
                  Confirm Password
                </label>
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  required
                  placeholder="••••••••"
                  className="block w-full px-4 py-3 rounded-xl border border-gray-300 dark:border-gray-600 bg-white dark:bg-gray-800 text-gray-900 dark:text-white focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-all"
                />
              </div>

              <div className="flex items-start">
                <div className="flex items-center h-5">
                  <input
                    id="terms"
                    name="terms"
                    type="checkbox"
                    required
                    className="w-4 h-4 border border-gray-300 rounded bg-white dark:bg-gray-800 focus:ring-2 focus:ring-blue-500 dark:border-gray-600"
                  />
                </div>
                <div className="ml-3 text-sm">
                  <label htmlFor="terms" className="text-gray-600 dark:text-gray-400">
                    I agree to the{" "}
                    <a href="#" className="font-medium text-blue-600 hover:text-blue-500">
                      Terms and Conditions
                    </a>
                  </label>
                </div>
              </div>
            </div>

            <button
              type="submit"
              className="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-colors"
            >
              Create Account
            </button>

            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300 dark:border-gray-600"></div>
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white dark:bg-gray-900 text-gray-500 italic">
                  Or sign up with
                </span>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <button className="flex items-center justify-center px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-xl hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                <span className="text-sm font-medium">Google</span>
              </button>
              <button className="flex items-center justify-center px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-xl hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                <span className="text-sm font-medium">GitHub</span>
              </button>
            </div>
          </form>

          <p className="text-center text-sm text-gray-600 dark:text-gray-400">
            Already have an account?{" "}
            <Link to="/login" className="font-medium text-blue-600 hover:text-blue-500">
              Sign in here
            </Link>
          </p>
        </div>
      </div>
    </main>
  );
}
