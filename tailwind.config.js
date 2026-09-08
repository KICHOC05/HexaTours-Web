/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/main/resources/templates/**/*.html',
    './src/main/resources/static/assets/js/**/*.js'
  ],
  theme: {
    extend: {
      colors: {
        ink: '#011b48',
        deep: '#011b48',
        sand: '#f5f8f8',
        cream: '#ffffff',
        coral: '#f57f1e',
        sky: '#12aeb1'
      },
      fontFamily: {
        display: ['Italiana', 'serif'],
        sans: ['DM Sans', 'sans-serif']
      }
    }
  }
};
