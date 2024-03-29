import { store } from 'quasar/wrappers'
import { createStore } from 'vuex'

// import example from './module-example'

import airquality from './airquality'
import app from './app'
import analysis from './analysis'
import auth from './auth'
import exposure from './exposure'
import incidence from './incidence'
import datacenter from './datacenter'
import grids from './grids'

/*
 * If not building with SSR mode, you can
 * directly export the Store instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Store instance.
 */

export default store(function (/* { ssrContext } */) {
  const Store = createStore({
    modules: {
      airquality,
      app,
      analysis,
      auth,
      exposure,
      incidence,
      datacenter,
      grids
    },

    // enable strict mode (adds overhead!)
    // for dev mode and --debug builds only
    strict: process.env.DEBUGGING
  })

  return Store
})
