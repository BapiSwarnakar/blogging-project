import React, { useState, useEffect } from 'react';

const PHRASES = [
  "Modern Creator",
  "Software Engineer",
  "Tech Enthusiast",
  "Digital Architect",
  "Problem Solver"
];

export const HeroTypewriter = () => {
  const [displayText, setDisplayText] = useState("");
  const [phraseIndex, setPhraseIndex] = useState(0);
  const [isDeleting, setIsDeleting] = useState(false);
  const [typingSpeed, setTypingSpeed] = useState(150);

  useEffect(() => {
    const handleTyping = () => {
      const currentPhrase = PHRASES[phraseIndex];
      
      if (isDeleting) {
        setDisplayText(currentPhrase.substring(0, displayText.length - 1));
        setTypingSpeed(50);
      } else {
        setDisplayText(currentPhrase.substring(0, displayText.length + 1));
        setTypingSpeed(150);
      }

      // If word is complete
      if (!isDeleting && displayText === currentPhrase) {
        // Pause before deleting
        setTypingSpeed(2000);
        setIsDeleting(true);
      } 
      // If word is deleted
      else if (isDeleting && displayText === "") {
        setIsDeleting(false);
        setPhraseIndex((prev) => (prev + 1) % PHRASES.length);
        setTypingSpeed(500);
      }
    };

    const timer = setTimeout(handleTyping, typingSpeed);
    return () => clearTimeout(timer);
  }, [displayText, isDeleting, phraseIndex, typingSpeed]);

  return (
    <span className="relative">
      <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 via-indigo-600 to-purple-600">
        {displayText}
      </span>
      <span className="typewriter-cursor" aria-hidden="true" />
    </span>
  );
};
