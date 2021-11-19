<template>
  <div class="login-page">
    <div class="review-and-submit">
      <div class="row">
        <p>Login</p>
      </div>
    </div>
    <div v-if="failedLogin" class="failed-login">
      <div class="row">
        <p>Your user name and password were not valid</p>
      </div>
    </div>
    <q-form class="q-gutter-md">
      <div class="row">
        <div class="col-3">
          <q-input
            outlined
            dense
            v-model="userName"
            label="Name"
            hint=""
            lazy-rules="ondemand"
            :rules="[(val) => (val && val.length > 0) || 'Please enter your name']"
          />
        </div>
      </div>

      <div class="row">
        <div class="col-3">
          <q-input
            outlined
            dense
            v-model="password"
            label="Password"
            type="password"
            hint=""
            lazy-rules="ondemand"
            :rules="[(val) => (val && val.length > 0) || 'Please enter your password']"
          />
        </div>
      </div>

      <div class="row">
        <div class="col-2">
          <div class="save-template-button">
            <q-btn color="primary" label="Submit" @click="submit()" />
          </div>
        </div>
      </div>
    </q-form>
  </div>
</template>

<script>
import { defineComponent, ref } from "vue";
import { useStore } from "vuex";

export default defineComponent({
  model: ref(null),
  name: "Login",

  setup(props) {
    const store = useStore();

    var users = [
      { userName: "Barrie", password: "bencloud" },
      { userName: "Jim", password: "bencloud" },
    ];

    const userName = ref("");
    const password = ref("");
    const failedLogin = ref(false);

    function submit() {
      let validUser = false;

      for (var i = 0; i < users.length; i++) {
        if (
          users[i].userName.toLocaleUpperCase() === userName.value.toLocaleUpperCase() &&
          users[i].password === password.value
        ) {
          validUser = true;
          break;
        }
      }

      if (validUser) {
        store.commit("auth/updateUser", userName.value);

        var redirectPath = store.state.auth.redirectPath;
        store.commit("auth/updateRedirectPath", "");

        if (redirectPath) {
          this.$router.push({ path: redirectPath });
        } else {
          this.$router.push({ path: "/" });
        }
      } else {
        failedLogin.value = true;
      }
    }

    return {
      userName,
      password,
      submit,
      failedLogin,
    };
  },
});
</script>

<style lang="scss">

.login-page {
  padding: 25px;

  .failed-login {
    color: red;
  }

}
</style>
