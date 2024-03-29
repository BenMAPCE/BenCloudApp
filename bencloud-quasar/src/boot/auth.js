import { boot } from "quasar/wrappers";
import { LocalStorage, Notify } from "quasar";
import axios from 'axios';
import { ref } from "vue";

export var isAdmin = ref(false);

export default boot(async ({ router, store }) => {
  console.log("----- in auth.js -----");
  console.log(store);

  // Before routing to each path
  router.beforeEach(async (to, from) => {
  try {
    var isUser = false;
    var status = null;
    // Check if the current user is a BenMAP user, store the response status
    try {
      const result = await axios
        .get(process.env.API_SERVER + "/api/user")
        .then((response) => {
          isUser = response.data.isUser;
          isAdmin = response.data.isAdmin;
          if(!!response) {
            status = response.status;
          } else if (!!error) {
            status = error.status;
          }
        })
        .catch(error => {
          console.log("/api/user ERROR: " + error.response.data.error);
        })
    } catch(ex) {
      console.log(ex)
    }
    // Non-200 status means BenMAP is down, route the user to the error page
    // Don't include /error to avoid an endless loop
    if(to.path != '/error' && status != 200) {
      return '/error';
    } else if (to.path === '/error' && status === 200) { 
      return '/';
    } else if (to.meta.requiresUser) {
      // If the requested page requires the user to be a BenMAP user and they are not a BenMAP user, route them to the request access page
      if(!isUser) {
        console.log("Current user is not a BenMAP user.");
        return '/requestaccess';
      } 
    }
    if(to.path === '/requestaccess' || to.path === '/requestaccess/') {
      var isUser = false;
      // If the current user is a BenMAP user, we don't want them to get stuck on the request access page
      try {
        const result = await axios
          .get(process.env.API_SERVER + "/api/user")
          .then((response) => {
            isUser = response.data.isUser;
            isAdmin = response.data.isAdmin;
          })
      } catch(ex) {
        console.log(ex)
      }
      // If they are a BenMAP user, route them to the main BenMAP page
      if(isUser) {
        console.log("Current user is a BenMAP user, routing away from /requestaccess.");
        return '/';
      }
    }
  } catch(ex) {
    console.log(ex)
  }
  });




  router.beforeEach((to, from, next) => {

    if (process.env.AUTH_ENABLED) {
      console.log("Auth enabled");

      if (to.meta.requiresAuth) {
        store.commit("auth/updateRedirectPath", to.path);

        if (store.state.auth.user) {
          console.log("found user: " + store.state.auth.user);
          if (to.meta.requiresAdmin) {
            if (
              // needs to be fixed
              store.state.auth.user.permissions &&
              store.state.auth.user.permissions.includes("admin")
            ) {
              console.log("on to next");
              next();
            } else {
              console.log(" no admin permission");
              Notify.create({
                message: `Your account is not authorized to see this view. If this is in error, please contact support.`,
                color: "negative",
              });
              next("/account");
            }
          } else if (
            to.path === "/" ||
            to.path === "/login" ||
            to.path === "/register"
          ) {
            next();
          } else {
            next();
          }
        } else {
          if (to.path !== "/login") {
            next("/login");
          } else {
            next();
          }
        }
      } else {
        // console.log("does not require admin");
        next();
      }
    } else {
      next();
    }
  });
});
