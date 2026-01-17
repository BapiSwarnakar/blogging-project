import React from 'react';
import { Link } from "react-router";
import { useAppSelector } from "~/store/hooks";
import logoLight from "./logo-light.svg";
import logoDark from "./logo-dark.svg";

export const Navbar: React.FC = () => {
  const { isAuthenticated, user } = useAppSelector((state) => state.auth);

  return (
    <nav className="sticky top-0 z-50 bg-white/70 dark:bg-gray-950/70 backdrop-blur-2xl border-b border-gray-100 dark:border-gray-800 transition-all">
      <div className="max-w-7xl mx-auto px-4 h-20 flex items-center justify-between">
        <div className="flex items-center gap-8">
          <Link to="/" className="w-40">
            <img
              src={logoLight}
              alt="Logo"
              className="block w-full dark:hidden"
            />
            <img
              src={logoDark}
              alt="Logo"
              className="hidden w-full dark:block"
            />
          </Link>
        </div>

        <div className="hidden md:flex items-center gap-8 text-sm font-medium">
          <Link to="/" className="hover:text-blue-600 dark:text-gray-300 dark:hover:text-white transition-colors">Explorer</Link>
          <a href="#" className="hover:text-blue-600 dark:text-gray-300 dark:hover:text-white transition-colors">Trending</a>
          <a href="#" className="hover:text-blue-600 dark:text-gray-300 dark:hover:text-white transition-colors">Categories</a>
        </div>

        <div className="flex items-center gap-4">
          {isAuthenticated ? (
            <div className="flex items-center gap-4">
              <span className="hidden sm:inline-block text-sm font-medium dark:text-gray-200">
                Hi, {user?.name || 'User'}
              </span>
              <div className="w-10 h-10 rounded-full bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white font-bold shadow-lg shadow-blue-500/20">
                {user?.name?.charAt(0) || 'U'}
              </div>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Link to="/login" className="px-5 py-2.5 text-sm font-semibold hover:bg-gray-100 dark:hover:bg-gray-900 rounded-xl transition-colors">
                Login
              </Link>
              <Link to="/signup" className="px-6 py-2.5 text-sm font-semibold bg-gray-900 dark:bg-white text-white dark:text-gray-900 rounded-xl hover:bg-black dark:hover:bg-gray-100 transition-all transform active:scale-95 shadow-xl">
                Get Started
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};
