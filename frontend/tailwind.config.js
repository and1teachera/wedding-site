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
      screens: {
        "iphone-SE": "375px",
        "ipad-air": "820px",
        "ipad-pro": "1024px",
      },
    }
  },
  plugins: [
    require('@tailwindcss/aspect-ratio'),
  ],
};


