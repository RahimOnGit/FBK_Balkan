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
    themes : [
      {
        fbkbalkan:
            {
              "primary": "#153B6F",
              "secondary": "#F5B301",
              "accent": "#22c55e",
              "neutral": "#1f2937",
              "base-100": "#ffffff",


            }
      }
    ],
    base : true,
    styled : true ,
    utils : true ,
    logs : true ,

  }
}