module.exports = {
  content: [
    "./src/main/resources/templates/**/*.html",
    "./src/main/resources/templates/fragments/**/*.html",
    "./src/main/resources/static/**/*.js"
  ],
  theme: {
    extend: {
      fontFamily: {
        virage: ['"Roboto Condensed"', 'sans-serif']
      }
    }
  },
  plugins: [require('daisyui')] ,

  daisyui : {
    themes : true,
    base : true,
    styled : true ,
    utils : true ,
    logs : true ,

  }
}