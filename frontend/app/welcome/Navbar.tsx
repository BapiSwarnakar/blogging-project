import React, { useState, useRef, useEffect } from 'react';
import { Link, useNavigate } from "react-router";
import { useAppSelector, useAppDispatch } from "~/store/hooks";
import { logoutUser } from "~/store/slices/authSlice";
import logoLight from "./logo-light.svg";
import logoDark from "./logo-dark.svg";

export const Navbar: React.FC = () => {
  const { isAuthenticated, user } = useAppSelector((state) => state.auth);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setIsDropdownOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    dispatch(logoutUser(user?.refreshToken));
    setIsDropdownOpen(false);
    navigate("/");
  };

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
          <Link to="/pricing" className="hover:text-blue-600 dark:text-gray-300 dark:hover:text-white transition-colors">Pricing</Link>
          <a href="#" className="hover:text-blue-600 dark:text-gray-300 dark:hover:text-white transition-colors">Categories</a>
        </div>


        <div className="flex items-center gap-4">
          {isAuthenticated ? (
            <div className="flex items-center gap-4 relative" ref={dropdownRef}>
              <span className="hidden sm:inline-block text-sm font-medium dark:text-gray-200">
                Hi, {user?.name || 'User'}
              </span>
              <button 
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                className="w-10 h-10 rounded-full bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white font-bold shadow-lg shadow-blue-500/20 transform transition-transform active:scale-90 hover:ring-2 hover:ring-offset-2 hover:ring-blue-500"
              >
                {user?.name?.charAt(0) || 'U'}
              </button>

              {/* User Dropdown */}
              {isDropdownOpen && (
                <div className="absolute right-0 top-full mt-2 w-48 bg-white dark:bg-gray-900 border border-gray-100 dark:border-gray-800 rounded-xl shadow-2xl py-2 overflow-hidden animate-in fade-in zoom-in slide-in-from-top-2 duration-200">
                  <div className="px-4 py-2 border-b border-gray-100 dark:border-gray-800">
                    <p className="text-xs text-gray-400 font-bold uppercase tracking-widest mb-1">Account</p>
                    <p className="text-sm font-bold truncate dark:text-white">{user?.name}</p>
                    <p className="text-[10px] text-gray-500 truncate">{user?.email}</p>
                  </div>
                  <Link 
                    to="/admin" 
                    onClick={() => setIsDropdownOpen(false)}
                    className="flex items-center gap-3 px-4 py-3 text-sm text-gray-700 dark:text-gray-300 hover:bg-blue-50 dark:hover:bg-blue-900/20 transition-colors"
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" /></svg>
                    Dashboard
                  </Link>
                  <button 
                    onClick={handleLogout}
                    className="w-full flex items-center gap-3 px-4 py-3 text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
                  >
                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" /></svg>
                    Logout
                  </button>
                </div>
              )}
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
