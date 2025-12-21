import { useState, useEffect } from "react";
import type { ReactNode } from "react";
import { Sidebar } from "./Sidebar";
import { Topbar } from "./Topbar";

interface AdminLayoutProps {
  readonly children: ReactNode;
  readonly title: string;
}

export function AdminLayout({ children, title }: AdminLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  useEffect(() => {
    // Set initial state based on screen width
    if (window.innerWidth < 1024) {
      setSidebarOpen(false);
    }
  }, []);

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950">
      {/* Overlay for mobile */}
      {sidebarOpen && (
        <button
          className="fixed inset-0 bg-black bg-opacity-50 z-30 lg:hidden w-full h-full cursor-default focus:outline-none"
          onClick={() => setSidebarOpen(false)}
          onKeyDown={(e) => {
            if (e.key === 'Escape') {
              setSidebarOpen(false);
            }
          }}
          aria-label="Close sidebar"
        />
      )}

      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      
      <div className={`transition-all duration-300 ${sidebarOpen ? "lg:ml-64" : "ml-0"}`}>
        <Topbar 
          onToggleSidebar={() => setSidebarOpen(!sidebarOpen)} 
          title={title}
        />
        
        <main className="p-4 md:p-6">
          {children}
        </main>
      </div>
    </div>
  );
}
