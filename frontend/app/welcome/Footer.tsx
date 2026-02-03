import { Link } from "react-router";
import logoLight from "./logo-light.svg";
import logoDark from "./logo-dark.svg";

export function Footer() {
  return (
    <footer className="mt-20 py-20 bg-white dark:bg-gray-900 border-t border-gray-100 dark:border-gray-800">
      <div className="max-w-7xl mx-auto px-4 grid grid-cols-1 md:grid-cols-4 gap-12">
        <div className="col-span-1 md:col-span-2">
          <Link to="/" className="w-40 block mb-6">
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
          <p className="text-gray-500 dark:text-gray-400 max-w-sm mb-8 leading-relaxed">
            Bringing you the best of technology, design, and lifestyle insights. 
            Join our community of over 50,000 readers.
          </p>
        </div>
        <div>
          <h4 className="font-bold text-gray-900 dark:text-white mb-6">Resources</h4>
          <ul className="space-y-4 text-gray-500 dark:text-gray-400">
            <li><a href="https://reactrouter.com/docs" target="_blank" rel="noreferrer" className="hover:text-blue-600 transition-colors">Documentation</a></li>
            <li><a href="https://rmx.as/discord" target="_blank" rel="noreferrer" className="hover:text-blue-600 transition-colors">Community</a></li>
            <li><Link to="/login" className="hover:text-blue-600 transition-colors">Account</Link></li>
            <li><Link to="/admin" className="hover:text-blue-600 transition-colors">Admin Panel</Link></li>
          </ul>
        </div>
        <div>
          <h4 className="font-bold text-gray-900 dark:text-white mb-6">Connect</h4>
          <div className="flex gap-4">
            <a href="#" className="w-10 h-10 rounded-full bg-gray-50 dark:bg-gray-800 flex items-center justify-center hover:bg-blue-600 hover:text-white transition-all">
              <svg fill="currentColor" viewBox="0 0 24 24" className="w-5 h-5"><path d="M22 12c0-5.523-4.477-10-10-10S2 6.477 2 12c0 4.991 3.657 9.128 8.438 9.878v-6.987h-2.54V12h2.54V9.797c0-2.506 1.492-3.89 3.777-3.89 1.094 0 2.238.195 2.238.195v2.46h-1.26c-1.243 0-1.63.771-1.63 1.562V12h2.773l-0.443 2.89h-2.33v6.988C18.343 21.128 22 16.991 22 12z" /></svg>
            </a>
            <a href="#" className="w-10 h-10 rounded-full bg-gray-50 dark:bg-gray-800 flex items-center justify-center hover:bg-sky-500 hover:text-white transition-all">
              <svg fill="currentColor" viewBox="0 0 24 24" className="w-5 h-5"><path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.84 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z" /></svg>
            </a>
          </div>
        </div>
      </div>
      <div className="max-w-7xl mx-auto px-4 mt-20 pt-8 border-t border-gray-100 dark:border-gray-800 text-center text-sm text-gray-500">
        © 2026 Blog Project. All rights reserved. Built with Passion.
      </div>
    </footer>
  );
}
