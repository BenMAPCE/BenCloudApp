<template>
  <div class="epa-header epa-css" v-if="showHeader">
    <header class="banner masthead clearfix" role="banner">
      <a class="site-logo" href="https://www.epa.gov/" title="Home" rel="home">
        <span class="site-logo__image">
          <svg class="site-logo__svg-main" aria-hidden="true" viewBox="0 0 454.1 154.7" xmlns="http://www.w3.org/2000/svg"><path d="m112.8 61.2c-4.8 18.6-22.9 33.3-42.9 33.3-20.1 0-38-14.7-42.9-33.4h.2s9.8 10.3-.2 0c3.1 3.1 6.2 4.4 10.7 4.4s7.7-1.3 10.7-4.4c3.1 3.1 6.3 4.5 10.9 4.4 4.5 0 7.6-1.3 10.7-4.4 3.1 3.1 6.2 4.4 10.7 4.4s7.7-1.3 10.7-4.4c3.1 3.1 6.3 4.5 10.9 4.4 4.3 0 7.4-1.2 10.5-4.3z"></path><path d="m113.2 51.2c0-24-19.4-43.5-43.3-43.5-24 0-43.5 19.5-43.5 43.5h39.1c-4.8-1.8-8.1-6.3-8.1-11.6 0-7 5.7-12.5 12.5-12.5 7 0 12.7 5.5 12.7 12.5 0 5.2-3.1 9.6-7.6 11.6z"></path><path d="m72.6 147c.7-36.9 29.7-68.8 66.9-70 0 37.2-30 68-66.9 70z"></path><path d="m67.1 147c-.7-36.9-29.7-68.8-67.1-70 0 37.2 30.2 68 67.1 70z"></path><path d="m240 10.8h-87.9v133.1h87.9v-20.4h-60.3v-36h60.3v-21h-60.3v-35h60.3z"></path><path d="m272.8 66.5h27.1c9.1 0 15.2-8.6 15.1-17.7-.1-9-6.1-17.3-15.1-17.3h-25.3v112.4h-27.8v-133.1h62.3c20.2 0 35 17.8 35.2 38 .2 20.4-14.8 38.7-35.2 38.7h-36.3z"></path><path d="m315.9 143.9h29.7l12.9-35h54.2l-8.1-21.9h-38.4l18.9-50.7 39.2 107.6h29.7l-53.1-133.1h-33.7z"></path></svg>
          <svg class="site-logo__svg-text" aria-hidden="true" viewBox="0 0 595.4 154.7" xmlns="http://www.w3.org/2000/svg"><path d="m9 8.5v22.4c0 1.9.2 3.3.5 4.3s.7 1.7 1 2.2c1.2 1.4 2.5 2.4 3.9 2.9 1.5.5 2.8.7 4.1.7 2.4 0 4.2-.4 5.5-1.3 1.3-.8 2.2-1.8 2.8-2.9s.9-2.3 1-3.4.1-2 .1-2.6v-22.3h4.7v22.3 1.7c0 .7-.1 1.5-.4 2.4-.3 1.8-1.2 3.6-2.5 5.4-1.8 2.1-3.8 3.5-6 4.2-2.2.6-4 .9-5.3.9-1.8 0-3.8-.3-6.2-1.1s-4.5-2.3-6.2-4.7c-.5-.8-1-1.8-1.4-3.2-.4-1.3-.6-3.3-.6-5.9v-22z"></path><path d="m43.2 22.2v-2.9l4.6.1-.1 4.1c.2-.3.4-.7.8-1.2.3-.5.8-.9 1.4-1.4s1.4-.9 2.3-1.3c.9-.3 2.1-.5 3.4-.4.6 0 1.4.1 2.4.3.9.2 1.9.6 2.9 1.2s1.8 1.5 2.4 2.6c.6 1.2.9 2.8.9 4.7l-.4 17-4.6-.1.4-16c0-.9 0-1.7-.2-2.4-.1-.7-.5-1.3-1.1-1.9-1.2-1.2-2.6-1.8-4.3-1.8s-3.1.5-4.4 1.7-2 3.1-2.1 5.7l-.3 14.5-4.5-.1z"></path><path d="m72.9 8.6h5.5v5.1h-5.5zm.5 10.9h4.6v25.1h-4.6z"></path><path d="m83.5 19.4h4.3v-5.3l4.5-1.5v6.8h5.4v3.4h-5.4v15.1c0 .3 0 .6.1 1 0 .4.1.7.4 1.1.2.4.5.6 1 .8.4.3 1 .4 1.8.4 1 0 1.7-.1 2.2-.2v3.7c-.9.2-2.1.3-3.8.3-2.1 0-3.6-.4-4.6-1.2s-1.5-2.2-1.5-4.2v-16.8h-4.3v-3.4z"></path><path d="m106.6 32.9c-.1 2.6.5 4.8 1.7 6.5 1.1 1.7 2.9 2.6 5.3 2.6 1.5 0 2.8-.4 3.9-1.3 1-.8 1.6-2.2 1.8-4h4.6c0 .6-.2 1.4-.4 2.3-.3 1-.8 2-1.7 3-.2.3-.6.6-1 1-.5.4-1 .7-1.7 1.1s-1.5.6-2.4.8c-.9.3-2 .4-3.3.4-7.6-.2-11.3-4.5-11.3-12.9 0-2.5.3-4.8 1-6.8s2-3.7 3.8-5.1c1.2-.8 2.4-1.3 3.7-1.6 1.3-.2 2.2-.3 3-.3 2.7 0 4.8.6 6.3 1.6s2.5 2.3 3.1 3.9c.6 1.5 1 3.1 1.1 4.6.1 1.6.1 2.9 0 4h-17.5m12.9-3c0-.3 0-.7 0-1.1s0-.8-.1-1.2c-.1-.9-.4-1.7-.8-2.5s-1-1.5-1.8-2c-.9-.5-2-.8-3.4-.8-.8 0-1.5.1-2.3.3s-1.5.7-2.2 1.3-1.2 1.3-1.6 2.3-.7 2.2-.8 3.6h13z"></path><path d="m148.6 8.6h4.6v32.1c0 1 .1 2.3.2 4h-4.6l-.1-4c-.2.3-.4.7-.7 1.2s-.8 1-1.4 1.5c-1 .7-2 1.2-3.1 1.4-.5.1-1 .2-1.5.3s-.9.1-1.4.1c-.4 0-.8 0-1.3-.1s-1.1-.2-1.7-.3c-1.1-.3-2.3-.9-3.4-1.8s-2.1-2.2-2.9-3.8c-.8-1.7-1.2-3.9-1.2-6.6.1-4.8 1.2-8.3 3.4-10.5 2.1-2.1 4.7-3.2 7.6-3.2 1.3 0 2.4.2 3.4.5.9.3 1.6.7 2.2 1.2.6.4 1 .9 1.3 1.4s.6.8.7 1.1zm.1 23.1c0-1.9-.2-3.3-.5-4.4-.4-1.1-.8-2-1.4-2.6-.5-.7-1.2-1.3-2-1.8-.9-.5-2-.7-3.3-.7-1.7 0-2.9.5-3.8 1.3s-1.6 1.9-2 3.1-.7 2.3-.7 3.4c-.1 1.1-.2 1.9-.1 2.4 0 1.1.1 2.2.3 3.4.2 1.1.5 2.2 1 3.1.5 1 1.2 1.7 2 2.3.9.6 2 .9 3.3.9 1.8 0 3.2-.5 4.2-1.4 1-.8 1.7-1.8 2.1-3s.7-2.4.8-3.4c.1-1.4.1-2.1.1-2.6z"></path><path d="m179.6 34.1c0 .6.1 1.3.3 2.1.1.8.5 1.6 1 2.3.5.8 1.4 1.4 2.5 1.9s2.7.8 4.7.8c1.8 0 3.3-.3 4.4-.8s1.9-1.1 2.5-1.8 1-1.5 1.1-2.2.2-1.2.2-1.7c0-1-.2-1.9-.5-2.6-.4-.6-.9-1.2-1.6-1.6-1.4-.8-3.4-1.4-5.9-2-4.9-1.1-8.1-2.2-9.5-3.2s-2.3-2.2-2.9-3.5c-.6-1.2-.8-2.4-.8-3.6.1-3.7 1.5-6.4 4.2-8.1 2.6-1.7 5.7-2.5 9.1-2.5 1.3 0 2.9.2 4.8.5 1.9.4 3.6 1.4 5 3 .5.5.9 1.1 1.2 1.7.3.5.5 1.1.6 1.6.2 1.1.3 2.1.3 2.9h-5c-.2-2.2-1-3.7-2.4-4.5-1.5-.7-3.1-1.1-4.9-1.1-5.1.1-7.7 2-7.8 5.8 0 1.5.5 2.7 1.6 3.5 1 .8 2.6 1.4 4.7 1.9 4 1 6.7 1.8 8.1 2.2.8.2 1.4.5 1.8.7.5.2 1 .5 1.4.9.8.5 1.4 1.1 1.9 1.8s.8 1.4 1.1 2.1c.3 1.4.5 2.5.5 3.4 0 3.3-1.2 6-3.5 8-2.3 2.1-5.8 3.2-10.3 3.3-1.4 0-3.2-.3-5.4-.8-1-.3-2-.7-3-1.2-.9-.5-1.8-1.2-2.5-2.1-.9-1.4-1.5-2.7-1.7-4.1-.3-1.3-.4-2.4-.3-3.2h5z"></path><path d="m205.7 19.4h4.3v-5.3l4.5-1.5v6.8h5.4v3.4h-5.4v15.1c0 .3 0 .6.1 1 0 .4.1.7.4 1.1.2.4.5.6 1 .8.4.3 1 .4 1.8.4 1 0 1.7-.1 2.2-.2v3.7c-.9.2-2.1.3-3.8.3-2.1 0-3.6-.4-4.6-1.2s-1.5-2.2-1.5-4.2v-16.8h-4.4z"></path><path d="m241 44.6c-.3-1.2-.5-2.5-.4-3.7-.5 1-1.1 1.8-1.7 2.4-.7.6-1.4 1.1-2 1.4-1.4.5-2.7.8-3.7.8-2.8 0-4.9-.8-6.4-2.2s-2.2-3.1-2.2-5.2c0-1 .2-2.3.8-3.7s1.7-2.6 3.5-3.7c1.4-.7 2.9-1.2 4.5-1.5 1.6-.1 2.9-.2 3.9-.2s2.1 0 3.3.1c.1-2.9-.2-4.8-.9-5.6-.5-.6-1.1-1.1-1.9-1.3s-1.6-.4-2.3-.4c-1.1 0-2 .2-2.6.5-.7.3-1.2.7-1.5 1.2s-.5.9-.6 1.4-.2.9-.2 1.2h-4.6c.1-.7.2-1.4.4-2.3.2-.8.6-1.6 1.3-2.5.5-.6 1-1 1.7-1.3.6-.3 1.3-.6 2-.8 1.5-.4 2.8-.6 4.2-.6 1.8 0 3.6.3 5.2.9s2.8 1.6 3.4 2.9c.4.7.6 1.4.7 2s.1 1.2.1 1.8l-.2 12c0 1 .1 3.1.4 6.3h-4.2m-.5-12.1c-.7-.1-1.6-.1-2.6-.1-.5 0-1.2 0-2.1 0-1 .1-2 .3-3 .6s-1.9.8-2.6 1.5c-.8.7-1.2 1.7-1.2 3 0 .4.1.8.2 1.3s.4 1 .8 1.5.9.8 1.6 1.1 1.5.5 2.5.5c2.3 0 4.1-.9 5.2-2.7.5-.8.8-1.7 1-2.7.1-.9.2-2.2.2-4z"></path><path d="m250.2 19.4h4.3v-5.3l4.5-1.5v6.8h5.4v3.4h-5.4v15.1c0 .3 0 .6.1 1 0 .4.1.7.4 1.1.2.4.5.6 1 .8.4.3 1 .4 1.8.4 1 0 1.7-.1 2.2-.2v3.7c-.9.2-2.1.3-3.8.3-2.1 0-3.6-.4-4.6-1.2s-1.5-2.2-1.5-4.2v-16.8h-4.3v-3.4z"></path><path d="m273.3 32.9c-.1 2.6.5 4.8 1.7 6.5 1.1 1.7 2.9 2.6 5.3 2.6 1.5 0 2.8-.4 3.9-1.3 1-.8 1.6-2.2 1.8-4h4.6c0 .6-.2 1.4-.4 2.3-.3 1-.8 2-1.7 3-.2.3-.6.6-1 1-.5.4-1 .7-1.7 1.1s-1.5.6-2.4.8c-.9.3-2 .4-3.3.4-7.6-.2-11.3-4.5-11.3-12.9 0-2.5.3-4.8 1-6.8s2-3.7 3.8-5.1c1.2-.8 2.4-1.3 3.7-1.6 1.3-.2 2.2-.3 3-.3 2.7 0 4.8.6 6.3 1.6s2.5 2.3 3.1 3.9c.6 1.5 1 3.1 1.1 4.6.1 1.6.1 2.9 0 4h-17.5m12.9-3c0-.3 0-.7 0-1.1s0-.8-.1-1.2c-.1-.9-.4-1.7-.8-2.5s-1-1.5-1.8-2c-.9-.5-2-.8-3.4-.8-.8 0-1.5.1-2.3.3s-1.5.7-2.2 1.3-1.2 1.3-1.6 2.3-.7 2.2-.8 3.6h13z"></path><path d="m301 37.2c0 .5.1 1 .2 1.4.1.5.4 1 .8 1.5s.9.8 1.6 1.1 1.6.5 2.7.5c1 0 1.8-.1 2.5-.3s1.3-.6 1.7-1.2c.5-.7.8-1.5.8-2.4 0-1.2-.4-2-1.3-2.5s-2.2-.9-4.1-1.2c-1.3-.3-2.4-.6-3.6-1-1.1-.3-2.1-.8-3-1.3s-1.5-1.2-2-2.1c-.5-.8-.8-1.9-.8-3.2 0-2.4.9-4.2 2.6-5.6 1.7-1.3 4-2 6.8-2.1 1.6 0 3.3.3 5 .8 1.7.6 2.9 1.6 3.7 3.1.4 1.4.6 2.6.6 3.7h-4.6c0-1.8-.6-3-1.7-3.5-1.1-.4-2.1-.6-3.1-.6-.2 0-.5 0-1 0s-1.1.2-1.7.4-1.1.5-1.5 1.1c-.5.5-.7 1.2-.7 2.1 0 1.1.5 1.9 1.3 2.3.7.4 1.5.7 2.1.9 3.3.7 5.6 1.3 6.9 1.8 1.3.4 2.2 1 2.8 1.7.7.7 1.1 1.4 1.4 2.2s.4 1.6.4 2.5c0 1.4-.3 2.7-.9 3.8s-1.4 2-2.4 2.6c-1.1.6-2.2 1-3.4 1.3s-2.5.4-3.8.4c-2.5 0-4.7-.6-6.6-1.8-1.8-1.2-2.8-3.3-2.9-6.3z"></path><path d="m3.4 58.5h21.9v4.2h-17.1v11.3h16.3v4.2h-16.3v12.1h17.5v4.3h-22.3z"></path><path d="m34.7 72.4-.1-2.9h4.6v4.1c.2-.3.4-.8.7-1.2.3-.5.8-1 1.3-1.5.6-.5 1.4-1 2.3-1.3s2-.5 3.4-.5c.6 0 1.4.1 2.4.2.9.2 1.9.5 2.9 1.1s1.8 1.4 2.5 2.5c.6 1.2 1 2.7 1 4.7v17.1h-4.6v-16c0-.9-.1-1.7-.2-2.4-.2-.7-.5-1.3-1.1-1.9-1.2-1.1-2.6-1.7-4.3-1.7s-3.1.6-4.3 1.8c-1.3 1.2-2 3.1-2 5.7v14.5h-4.5z"></path><path d="m60.3 69.5h5.1l7.7 19.9 7.6-19.9h5l-10.6 25.1h-4.6z"></path><path d="m91.4 58.6h5.5v5.1h-5.5zm.5 10.9h4.6v25.1h-4.6z"></path><path d="m106 74.7c0-1.8-.1-3.5-.3-5.1h4.6l.1 4.9c.5-1.8 1.4-3 2.5-3.7s2.2-1.2 3.3-1.3c1.4-.2 2.4-.2 3.1-.1v4.6c-.2-.1-.5-.2-.9-.2-.3 0-.8 0-1.3 0-1.3 0-2.4.2-3.3.5-.9.4-1.5.9-2 1.6-.9 1.4-1.4 3.2-1.3 5.4v13.3h-4.6v-19.9z"></path><path d="m123.3 82.4c0-1.6.2-3.2.6-4.8s1.1-3 2-4.4c1-1.3 2.2-2.4 3.8-3.2s3.6-1.2 5.9-1.2c2.4 0 4.5.4 6.1 1.3 1.5.9 2.7 2 3.6 3.3s1.5 2.8 1.8 4.3c.2.8.3 1.5.4 2.2v2.2c0 3.7-1 6.9-3 9.5s-5.1 4-9.3 4c-4-.1-7-1.4-9-3.9-1.9-2.5-2.9-5.6-2.9-9.3m4.8-.3c0 2.7.6 5 1.8 6.9 1.2 2 3 3 5.6 3.1.9 0 1.8-.2 2.7-.5.8-.3 1.6-.9 2.3-1.7s1.3-1.9 1.8-3.2c.4-1.3.6-2.9.6-4.7-.1-6.4-2.5-9.6-7.1-9.6-.7 0-1.5.1-2.4.3-.8.3-1.7.8-2.5 1.6-.8.7-1.4 1.7-1.9 3-.6 1.1-.9 2.8-.9 4.8z"></path><path d="m155.9 72.4-.1-2.9h4.6v4.1c.2-.3.4-.8.7-1.2.3-.5.8-1 1.3-1.5.6-.5 1.4-1 2.3-1.3s2-.5 3.4-.5c.6 0 1.4.1 2.4.2.9.2 1.9.5 2.9 1.1s1.8 1.4 2.5 2.5c.6 1.2 1 2.7 1 4.7v17.1h-4.6v-16c0-.9-.1-1.7-.2-2.4-.2-.7-.5-1.3-1.1-1.9-1.2-1.1-2.6-1.7-4.3-1.7s-3.1.6-4.3 1.8c-1.3 1.2-2 3.1-2 5.7v14.5h-4.6z"></path><path d="m185.7 72.8-.1-3.3h4.6v3.6c1.2-1.9 2.6-3.2 4.1-3.7 1.5-.4 2.7-.6 3.8-.6 1.4 0 2.6.2 3.6.5.9.3 1.7.7 2.3 1.1 1.1 1 1.9 2 2.3 3.1.2-.4.5-.8 1-1.3.4-.5.9-1 1.5-1.6.6-.5 1.5-.9 2.5-1.3 1-.3 2.2-.5 3.5-.5.9 0 1.9.1 3 .3 1 .2 2 .7 3 1.3s1.7 1.5 2.3 2.7.9 2.7.9 4.6v16.9h-4.6v-16.2c0-1.1-.1-2-.2-2.5-.1-.6-.3-1-.6-1.3-.4-.6-1-1.2-1.8-1.6s-1.8-.6-3.1-.6c-1.5 0-2.7.4-3.6 1-.4.3-.8.5-1.1.9-.3.3-.6.6-.8.8-.5.8-.8 1.8-1 2.8-.1 1.1-.2 2-.1 2.6v14.1h-4.6v-16.7c0-1.6-.5-2.9-1.4-4-.9-1-2.3-1.5-4.2-1.5-1.6 0-2.9.4-3.8 1.1s-1.5 1.2-1.8 1.7c-.5.7-.8 1.5-.9 2.5-.1.9-.2 1.8-.2 2.6v14.3h-4.5z"></path><path d="m236.2 82.9c-.1 2.6.5 4.8 1.7 6.5 1.1 1.7 2.9 2.6 5.3 2.6 1.5 0 2.8-.4 3.9-1.3 1-.8 1.6-2.2 1.8-4h4.6c0 .6-.2 1.4-.4 2.3-.3 1-.8 2-1.7 3-.2.3-.6.6-1 1-.5.4-1 .7-1.7 1.1s-1.5.6-2.4.8c-.9.3-2 .4-3.3.4-7.6-.2-11.3-4.5-11.3-12.9 0-2.5.3-4.8 1-6.8s2-3.7 3.8-5.1c1.2-.8 2.4-1.3 3.7-1.6 1.3-.2 2.2-.3 3-.3 2.7 0 4.8.6 6.3 1.6s2.5 2.3 3.1 3.9c.6 1.5 1 3.1 1.1 4.6.1 1.6.1 2.9 0 4h-17.5m12.8-3c0-.3 0-.7 0-1.1s0-.8-.1-1.2c-.1-.9-.4-1.7-.8-2.5s-1-1.5-1.8-2c-.9-.5-2-.8-3.4-.8-.8 0-1.5.1-2.3.3s-1.5.7-2.2 1.3-1.2 1.3-1.6 2.3-.7 2.2-.8 3.6z"></path><path d="m261.4 72.4-.1-2.9h4.6v4.1c.2-.3.4-.8.7-1.2.3-.5.8-1 1.3-1.5.6-.5 1.4-1 2.3-1.3s2-.5 3.4-.5c.6 0 1.4.1 2.4.2.9.2 1.9.5 2.9 1.1s1.8 1.4 2.5 2.5c.6 1.2 1 2.7 1 4.7v17.1h-4.6v-16c0-.9-.1-1.7-.2-2.4-.2-.7-.5-1.3-1.1-1.9-1.2-1.1-2.6-1.7-4.3-1.7s-3.1.6-4.3 1.8c-1.3 1.2-2 3.1-2 5.7v14.5h-4.6z"></path><path d="m288 69.4h4.3v-5.2l4.5-1.5v6.8h5.4v3.4h-5.4v15.1c0 .3 0 .6.1 1 0 .4.1.7.4 1.1.2.4.5.6 1 .8.4.3 1 .4 1.8.4 1 0 1.7-.1 2.2-.2v3.6c-.9.2-2.1.3-3.8.3-2.1 0-3.6-.4-4.6-1.2s-1.5-2.2-1.5-4.2v-16.8h-4.3v-3.4z"></path><path d="m323.3 94.6c-.3-1.2-.5-2.5-.4-3.7-.5 1-1.1 1.8-1.7 2.4-.7.6-1.4 1.1-2 1.4-1.4.5-2.7.8-3.7.8-2.8 0-4.9-.8-6.4-2.2s-2.2-3.1-2.2-5.2c0-1 .2-2.3.8-3.7s1.7-2.6 3.5-3.7c1.4-.7 2.9-1.2 4.5-1.5 1.6-.1 2.9-.2 3.9-.2s2.1 0 3.3.1c.1-2.9-.2-4.8-.9-5.6-.5-.6-1.1-1.1-1.9-1.3s-1.6-.4-2.3-.4c-1.1 0-2 .2-2.6.5-.7.3-1.2.7-1.5 1.2s-.5.9-.6 1.4-.2.9-.2 1.2h-4.6c.1-.7.2-1.4.4-2.3.2-.8.6-1.6 1.3-2.5.5-.6 1-1 1.7-1.3.6-.3 1.3-.6 2-.8 1.5-.4 2.8-.6 4.2-.6 1.8 0 3.6.3 5.2.9s2.8 1.6 3.4 2.9c.4.7.6 1.4.7 2s.1 1.2.1 1.8l-.2 12c0 1 .1 3.1.4 6.3zm-.5-12.2c-.7-.1-1.6-.1-2.6-.1-.5 0-1.2 0-2.1 0-1 .1-2 .3-3 .6s-1.9.8-2.6 1.5c-.8.7-1.2 1.7-1.2 3 0 .4.1.8.2 1.3s.4 1 .8 1.5.9.8 1.6 1.1 1.5.5 2.5.5c2.3 0 4.1-.9 5.2-2.7.5-.8.8-1.7 1-2.7.1-.9.2-2.2.2-4z"></path><path d="m336.4 58.6h4.6v36.1h-4.6z"></path><path d="m364.1 58.5h11.7c2.1 0 3.9.1 5.5.4.8.2 1.5.4 2.2.9.7.4 1.3.9 1.8 1.6 1.7 1.9 2.6 4.2 2.6 7 0 2.7-.9 5.1-2.8 7.1-.8.9-2 1.7-3.6 2.2-1.6.6-3.9.9-6.9.9h-5.7v16.1h-4.8zm4.8 15.9h5.8c.8 0 1.7-.1 2.6-.2s1.8-.3 2.6-.7 1.5-1 2-1.9c.5-.8.8-2 .8-3.4s-.2-2.5-.7-3.3-1.1-1.3-1.9-1.7c-1.6-.5-3.1-.8-4.5-.7h-6.8v11.9z"></path><path d="m393.8 74.7c0-1.8-.1-3.5-.3-5.1h4.6l.1 4.9c.5-1.8 1.4-3 2.5-3.7s2.2-1.2 3.3-1.3c1.4-.2 2.4-.2 3.1-.1v4.6c-.2-.1-.5-.2-.9-.2-.3 0-.8 0-1.3 0-1.3 0-2.4.2-3.3.5-.9.4-1.5.9-2 1.6-.9 1.4-1.4 3.2-1.3 5.4v13.3h-4.6v-19.9z"></path><path d="m411.2 82.4c0-1.6.2-3.2.6-4.8s1.1-3 2-4.4c1-1.3 2.2-2.4 3.8-3.2s3.6-1.2 5.9-1.2c2.4 0 4.5.4 6.1 1.3 1.5.9 2.7 2 3.6 3.3s1.5 2.8 1.8 4.3c.2.8.3 1.5.4 2.2v2.2c0 3.7-1 6.9-3 9.5s-5.1 4-9.3 4c-4-.1-7-1.4-9-3.9-1.9-2.5-2.9-5.6-2.9-9.3m4.8-.3c0 2.7.6 5 1.8 6.9 1.2 2 3 3 5.6 3.1.9 0 1.8-.2 2.7-.5.8-.3 1.6-.9 2.3-1.7s1.3-1.9 1.8-3.2c.4-1.3.6-2.9.6-4.7-.1-6.4-2.5-9.6-7.1-9.6-.7 0-1.5.1-2.4.3-.8.3-1.7.8-2.5 1.6-.8.7-1.4 1.7-1.9 3-.7 1.1-.9 2.8-.9 4.8z"></path><path d="m439.8 69.4h4.3v-5.2l4.5-1.5v6.8h5.4v3.4h-5.4v15.1c0 .3 0 .6.1 1 0 .4.1.7.4 1.1.2.4.5.6 1 .8.4.3 1 .4 1.8.4 1 0 1.7-.1 2.2-.2v3.6c-.9.2-2.1.3-3.8.3-2.1 0-3.6-.4-4.6-1.2s-1.5-2.2-1.5-4.2v-16.8h-4.3v-3.4z"></path><path d="m462.9 82.9c-.1 2.6.5 4.8 1.7 6.5 1.1 1.7 2.9 2.6 5.3 2.6 1.5 0 2.8-.4 3.9-1.3 1-.8 1.6-2.2 1.8-4h4.6c0 .6-.2 1.4-.4 2.3-.3 1-.8 2-1.7 3-.2.3-.6.6-1 1-.5.4-1 .7-1.7 1.1s-1.5.6-2.4.8c-.9.3-2 .4-3.3.4-7.6-.2-11.3-4.5-11.3-12.9 0-2.5.3-4.8 1-6.8s2-3.7 3.8-5.1c1.2-.8 2.4-1.3 3.7-1.6 1.3-.2 2.2-.3 3-.3 2.7 0 4.8.6 6.3 1.6s2.5 2.3 3.1 3.9c.6 1.5 1 3.1 1.1 4.6.1 1.6.1 2.9 0 4h-17.5m12.9-3c0-.3 0-.7 0-1.1s0-.8-.1-1.2c-.1-.9-.4-1.7-.8-2.5s-1-1.5-1.8-2c-.9-.5-2-.8-3.4-.8-.8 0-1.5.1-2.3.3s-1.5.7-2.2 1.3-1.2 1.3-1.6 2.3-.7 2.2-.8 3.6z"></path><path d="m501.8 77.5c0-.3 0-.8-.1-1.4s-.3-1.1-.6-1.8c-.2-.6-.7-1.2-1.4-1.6s-1.6-.6-2.7-.6c-1.5 0-2.7.4-3.5 1.2-.9.8-1.5 1.7-1.9 2.8s-.6 2.2-.7 3.2c-.1 1.1-.2 1.8-.1 2.4 0 1.3.1 2.5.3 3.7s.5 2.3.9 3.3c.8 2 2.4 3 4.8 3.1 1.9 0 3.3-.7 4.1-1.9.8-1.1 1.2-2.3 1.2-3.6h4.6c-.2 2.5-1.1 4.6-2.7 6.3-1.7 1.8-4.1 2.7-7.1 2.7-.9 0-2.1-.2-3.6-.6-.7-.2-1.4-.6-2.2-1s-1.5-1-2.2-1.7c-.7-.9-1.4-2.1-2-3.6s-.9-3.5-.9-6.1.4-4.8 1.1-6.6c.7-1.7 1.6-3.1 2.7-4.2 1.1-1 2.3-1.8 3.6-2.2s2.5-.6 3.7-.6h1.6c.6.1 1.3.2 1.9.4.7.2 1.4.5 2.1 1 .7.4 1.3 1 1.8 1.7.9 1.1 1.4 2.1 1.7 3.1.2 1 .3 1.8.3 2.6z"></path><path d="m509.3 69.4h4.3v-5.2l4.5-1.5v6.8h5.4v3.4h-5.4v15.1c0 .3 0 .6.1 1 0 .4.1.7.4 1.1.2.4.5.6 1 .8.4.3 1 .4 1.8.4 1 0 1.7-.1 2.2-.2v3.6c-.9.2-2.1.3-3.8.3-2.1 0-3.6-.4-4.6-1.2s-1.5-2.2-1.5-4.2v-16.8h-4.3v-3.4z"></path><path d="m529.2 58.6h5.5v5.1h-5.5zm.5 10.9h4.6v25.1h-4.6z"></path><path d="m541.8 82.4c0-1.6.2-3.2.6-4.8s1.1-3 2-4.4c1-1.3 2.2-2.4 3.8-3.2s3.6-1.2 5.9-1.2c2.4 0 4.5.4 6.1 1.3 1.5.9 2.7 2 3.6 3.3s1.5 2.8 1.8 4.3c.2.8.3 1.5.4 2.2v2.2c0 3.7-1 6.9-3 9.5s-5.1 4-9.3 4c-4-.1-7-1.4-9-3.9-1.9-2.5-2.9-5.6-2.9-9.3m4.7-.3c0 2.7.6 5 1.8 6.9 1.2 2 3 3 5.6 3.1.9 0 1.8-.2 2.7-.5.8-.3 1.6-.9 2.3-1.7s1.3-1.9 1.8-3.2c.4-1.3.6-2.9.6-4.7-.1-6.4-2.5-9.6-7.1-9.6-.7 0-1.5.1-2.4.3-.8.3-1.7.8-2.5 1.6-.8.7-1.4 1.7-1.9 3-.6 1.1-.9 2.8-.9 4.8z"></path><path d="m574.3 72.4-.1-2.9h4.6v4.1c.2-.3.4-.8.7-1.2.3-.5.8-1 1.3-1.5.6-.5 1.4-1 2.3-1.3s2-.5 3.4-.5c.6 0 1.4.1 2.4.2.9.2 1.9.5 2.9 1.1s1.8 1.4 2.5 2.5c.6 1.2 1 2.7 1 4.7v17.1h-4.6v-16c0-.9-.1-1.7-.2-2.4-.2-.7-.5-1.3-1.1-1.9-1.2-1.1-2.6-1.7-4.3-1.7s-3.1.6-4.3 1.8c-1.3 1.2-2 3.1-2 5.7v14.5h-4.6z"></path><path d="m14.8 108.5h5.2l14.1 36.1h-5.3l-3.8-9.4h-16.2l-3.8 9.4h-5zm-4.4 22.7h13.3l-6.5-17.8z"></path><path d="m44.4 146.5c.1.7.2 1.4.4 1.9.2.6.5 1.1.9 1.6.8.9 2.3 1.4 4.4 1.5 1.6 0 2.8-.3 3.7-.9s1.5-1.4 1.9-2.4c.4-1.1.6-2.3.7-3.7s.1-2.9.1-4.6c-.5.9-1.1 1.7-1.8 2.3s-1.5 1-2.3 1.3c-1.7.4-3 .6-3.9.6-1.2 0-2.4-.2-3.8-.6s-2.6-1.2-3.7-2.5c-1-1.3-1.7-2.8-2.1-4.4s-.6-3.2-.6-4.8c0-4.3 1.1-7.4 3.2-9.5 2-2.1 4.6-3.1 7.6-3.1 1.3 0 2.3.1 3.2.4s1.6.6 2.1 1c.6.4 1.1.8 1.5 1.2.3.4.6.8.9 1.2v-3.4h4.4l-.1 4.5v15.7c0 2.9-.1 5.2-.2 6.7-.2 1.6-.5 2.8-1 3.7-1.1 1.9-2.6 3.2-4.6 3.7-1.9.6-3.8.8-5.6.8-2.4 0-4.3-.3-5.6-.8-1.4-.5-2.4-1.2-3-2s-1-1.7-1.2-2.7c-.2-.9-.3-1.8-.4-2.7zm5.3-5.8c1.4 0 2.5-.2 3.3-.7s1.5-1.1 2-1.8c.5-.6.9-1.4 1.2-2.5.3-1 .4-2.6.4-4.8 0-1.6-.2-2.9-.4-3.9-.3-1-.8-1.8-1.4-2.4-1.3-1.4-3-2.2-5.2-2.2-1.4 0-2.5.3-3.4 1s-1.6 1.5-2 2.4c-.4 1-.7 2-.9 3s-.2 2-.2 2.8c0 1 .1 1.9.3 2.9.2 1.1.5 2.1 1 3s1.2 1.6 2 2.2c.8.7 1.9 1 3.3 1z"></path><path d="m73.3 132.9c-.1 2.6.5 4.8 1.7 6.5 1.1 1.7 2.9 2.6 5.3 2.6 1.5 0 2.8-.4 3.9-1.3 1-.8 1.6-2.2 1.8-4h4.6c0 .6-.2 1.4-.4 2.3-.3 1-.8 2-1.7 3-.2.3-.6.6-1 1-.5.4-1 .7-1.7 1.1s-1.5.6-2.4.8c-.9.3-2 .4-3.3.4-7.6-.2-11.3-4.5-11.3-12.9 0-2.5.3-4.8 1-6.8s2-3.7 3.8-5.1c1.2-.8 2.4-1.3 3.7-1.6 1.3-.2 2.2-.3 3-.3 2.7 0 4.8.6 6.3 1.6s2.5 2.3 3.1 3.9c.6 1.5 1 3.1 1.1 4.6.1 1.6.1 2.9 0 4h-17.5m12.9-3c0-.3 0-.7 0-1.1s0-.8-.1-1.2c-.1-.9-.4-1.7-.8-2.5s-1-1.5-1.8-2.1c-.9-.5-2-.8-3.4-.8-.8 0-1.5.1-2.3.3s-1.5.7-2.2 1.3-1.2 1.3-1.6 2.3-.7 2.2-.8 3.7z"></path><path d="m98.6 122.4-.1-2.9h4.6v4.1c.2-.3.4-.8.7-1.2.3-.5.8-1 1.3-1.5.6-.5 1.4-1 2.3-1.3s2-.5 3.4-.5c.6 0 1.4.1 2.4.2.9.2 1.9.5 2.9 1.1s1.8 1.4 2.5 2.5c.6 1.2 1 2.7 1 4.7v17.1h-4.6v-16c0-.9-.1-1.7-.2-2.4-.2-.7-.5-1.3-1.1-1.9-1.2-1.1-2.6-1.7-4.3-1.7s-3.1.6-4.3 1.8c-1.3 1.2-2 3.1-2 5.7v14.5h-4.6z"></path><path d="m142.7 127.5c0-.3 0-.8-.1-1.4s-.3-1.1-.6-1.8c-.2-.6-.7-1.2-1.4-1.6s-1.6-.6-2.7-.6c-1.5 0-2.7.4-3.5 1.2-.9.8-1.5 1.7-1.9 2.8s-.6 2.2-.7 3.2c-.1 1.1-.2 1.8-.1 2.4 0 1.3.1 2.5.3 3.7s.5 2.3.9 3.3c.8 2 2.4 3 4.8 3.1 1.9 0 3.3-.7 4.1-1.9.8-1.1 1.2-2.3 1.2-3.6h4.6c-.2 2.5-1.1 4.6-2.7 6.3-1.7 1.8-4.1 2.7-7.1 2.7-.9 0-2.1-.2-3.6-.6-.7-.2-1.4-.6-2.2-1s-1.5-1-2.2-1.7c-.7-.9-1.4-2.1-2-3.6s-.9-3.5-.9-6.1.4-4.8 1.1-6.6c.7-1.7 1.6-3.1 2.7-4.2 1.1-1 2.3-1.8 3.6-2.2s2.5-.6 3.7-.6h1.6c.6.1 1.3.2 1.9.4.7.2 1.4.5 2.1 1 .7.4 1.3 1 1.8 1.7.9 1.1 1.4 2.1 1.7 3.1.2 1 .3 1.8.3 2.6z"></path><path d="m164.8 144.8-3.4 9.3h-4.7l3.8-9.6-10.3-25h5.2l7.6 19.8 7.7-19.8h5z"></path></svg>
        </span>
      </a>
      
      <div class="region-header">
        <div class="block-epa-core-gsa-epa-search" id="block-epa-core-gsa-epa-search">
          <form action="https://search.epa.gov/epasearch" class="epa-search" method="get">
            <label class="element-hidden" for="search-box">Search</label>
            <input
              class="form-text"
              id="search-box"
              name="querytext"
              placeholder="Search EPA.gov"
              value=""
            />
            <button
              class="epa-search-button"
              id="search-button"
              title="Search"
              type="submit"
            >
              Search
            </button>
            <input name="areaname" type="hidden" value="" />
            <input name="areacontacts" type="hidden" value="" />
            <input name="areasearchurl" type="hidden" value="" />
            <input name="typeofsearch" type="hidden" value="epa" />
            <input name="result_template" type="hidden" value="2col.ftl" />
          </form>
        </div>
      </div>
    </header>
    <nav class="nav main-nav clearfix" role="navigation">
      <div class="nav__inner">
        <h2 class="element-invisible">Main menu</h2>
        <ul class="menu" role="menu">
          <li class="menu-item" role="presentation">
            <a
              class="menu-link"
              href="https://www.epa.gov/environmental-topics"
              role="menuitem"
              title="Learn about Environmental Topics that EPA covers."
              >Environmental Topics</a
            >
          </li>
          <li class="menu-item" role="presentation">
            <a
              class="menu-link"
              href="https://www.epa.gov/laws-regulations"
              role="menuitem"
              title="Laws written by Congress provide the authority for EPA to write regulations. Regulations explain the technical, operational, and legal details necessary to implement laws."
              >Laws &amp; Regulations</a
            >
          </li>
          <li class="menu-item" role="presentation">
            <a
              class="menu-link"
              href="https://www.epa.gov/report-violation"
              role="menuitem"
              title=""
              >Report a Violation</a
            >
          </li>
          <li class="menu-item" role="presentation">
            <a
              class="menu-link"
              href="https://www.epa.gov/aboutepa"
              role="menuitem"
              title="Learn more about our mission and what we do, how we are organized, and our history."
              >About EPA</a
            >
          </li>
        </ul>
      </div>
    </nav>
    <div class="mobile-nav" id="mobile-nav">
      <div class="mobile-bar clearfix">
        <label class="menu-button" for="mobile-nav-toggle">Menu</label>
      </div>
      <input checked id="mobile-nav-toggle" type="checkbox" />
      <div class="mobile-links element-hidden" id="mobile-links" style="height: 2404px">
        <ul class="mobile-menu">
          <li class="menu-item" role="presentation">
            <a
              class="menu-link"
              href="https://www.epa.gov/environmental-topics"
              role="menuitem"
              title="Learn about Environmental Topics that EPA covers."
              >Environmental Topics</a
            >
          </li>
          <li class="menu-item" role="presentation">
            <a
              class="menu-link"
              href="https://www.epa.gov/laws-regulations"
              role="menuitem"
              title="Laws written by Congress provide the authority for EPA to write regulations. Regulations explain the technical, operational, and legal details necessary to implement laws."
              >Laws &amp; Regulations</a
            >
          </li>
          <li class="menu-item" role="presentation">
            <a
              class="menu-link"
              href="https://www.epa.gov/aboutepa"
              role="menuitem"
              title="Learn more about our mission and what we do, how we are organized, and our history."
              >About EPA</a
            >
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from "vue";

export default defineComponent({
  name: "EPAHeader",
  props: {
    showEpaHeaderFooter: {
      type: Boolean,
      default: false,
    },
  },

  setup(props, { emit }) {
    const showHeader = ref(false);

    onMounted(() => {
      if (props.showEpaHeaderFooter) {
		  console.log(props.showEpaHeaderFooter)
        showHeader.value = true
      }
    });

    return {
      showHeader,
    };
  },
});
</script>

<style lang="scss" scoped>
.epa-header {
  background-color: #005ea2;
  font-size: 106.25%;
  font-family: "Source Sans Pro", "Helvetica Neue", "Helvetica", "Roboto", "Arial",
    sans-serif;

  .banner {
    background: #005ea2;
  }
  nav.main-nav > .nav__inner > .menu > .menu-item > .menu-link {
    color: #fff;
    font-size: .98rem !important;
    font-family: "Merriweather", "Georgia", "Cambria", "Times New Roman", "Times", serif;
    font-weight: bold;
    line-height: 1;
    padding: 1.2em 1.6em;
    text-decoration: none;
  }

  .site-logo {
    width: 22.625rem;
    display: block;
    margin: 0 -0.25rem;
    max-width: 100%;
    padding: .25rem;
  }

  .site-logo__image {
    display: flex;
    gap: .1875rem;
    padding: 0;
    position: relative;
    width: 100%;
  }

  .site-logo__svg-main {
    position: relative;
    width: 28.38125rem;
    fill: #fff;
    height: 100%;
  }

  .site-logo__svg-text {
    display: block;
    width: 37.2125rem;
    fill: #fff;
    height: 100%;
  }

  .epa-search {
    bottom: 2rem;
    padding-right: 1em;
    position: absolute;
    right: 30px;
    clear: right;
    padding-top: 0px;
    float: right;
    width: 16.5em;
  }

  .main-nav {
    background: #1a4480;
  }

  input.form-text {
    height: 29px;
  }
  button.epa-search-button {
    right: 16px;

    background-color: #1a4480;;
    background-image: url(https://www.epa.gov/sites/all/themes/epa/img/svg/search.svg);
    background-position: 50% 50%;
    background-repeat: no-repeat;
    background-size: 12px 12px;
    border-color: #1a4480;;
    border-radius: 0 3px 3px 0;
    box-sizing: border-box;
    color: #4aa564;
    font: 0/0 a;
    height: 29px;
    right: 0px;
    position: absolute;
    text-decoration: none;
    text-indent: -999em;
    text-shadow: none;
    width: 44px;
  }
}
</style>
