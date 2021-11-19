import { boot } from "quasar/wrappers";
import { LocalStorage, Notify } from "quasar";
import { ref } from "vue";

export default boot(async ({ router, store }) => {
  console.log("----- in auth.js -----");
  console.log(store);

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
