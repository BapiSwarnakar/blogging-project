import { Link } from "react-router";
import { PostList } from "./post/PostList";
import { Navbar } from "./Navbar";
import { HeroTypewriter } from "./HeroTypewriter";
import { Footer } from "./Footer";

export function Welcome() {
  return (
    <main className="min-h-screen bg-[#fafafa] dark:bg-gray-950 transition-colors duration-500">
      <Navbar />

      {/* Hero Section */}
      <header className="relative pt-20 pb-12 overflow-hidden">
        <div className="absolute top-0 right-0 -z-10 w-1/2 h-full bg-gradient-to-b from-blue-50/50 to-transparent dark:from-blue-900/10 opacity-50 blur-3xl rounded-full"></div>
        <div className="max-w-7xl mx-auto px-4 text-center">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-blue-50 dark:bg-blue-900/20 text-blue-600 dark:text-blue-400 text-xs font-bold uppercase tracking-widest mb-6">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-blue-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-blue-500"></span>
            </span>
            Stay Updated
          </div>
          <h1 className="text-5xl md:text-7xl font-black text-gray-900 dark:text-white mb-6 tracking-tight">
            Insights for the<br />
            <HeroTypewriter />
          </h1>
        </div>
      </header>

      {/* Main Content (Post List) */}
      <section className="relative">
        <PostList />
      </section>

      <Footer />
    </main>
  );
}
