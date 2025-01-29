/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      fontFamily: {
        'display': ['Playfair Display', 'serif'],
        'body': ['Cormorant Garamond', 'serif'],
      },
    }
  },
  plugins: [
    require('@tailwindcss/aspect-ratio'),
  ],
};


