<template>
  <div class="epa-header epa-css" v-if="showHeader">
    <header class="masthead clearfix" role="banner">
      <img
        alt=""
        class="site-logo"
        src="https://www.epa.gov/sites/all/themes/epa/logo.png"
      />
      <div class="site-name-and-slogan">
        <h1 class="site-name">
          <a href="https://www.epa.gov/" rel="home" title="Go to the home page"
            ><span>US EPA</span></a
          >
        </h1>
        <div class="site-slogan">United States Environmental Protection Agency</div>
      </div>
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
  background-color: white;
  font-size: 106.25%;
  font-family: "Source Sans Pro", "Helvetica Neue", "Helvetica", "Roboto", "Arial",
    sans-serif;
  .a.menu-link {
    font-size: 88.23%;
    font-family: "Merriweather", "Georgia", "Cambria", "Times New Roman", "Times", serif;
    font-weight: bold;
    line-height: 1;
    padding: 1.2em 1.6em;
    text-decoration: none;
  }

  .epa-search {
    bottom: -2.29em;
    padding-right: 1em;
    position: absolute;
    right: 0px;
    clear: right;
    padding-top: 0px;
    float: right;
    width: 16.5em;
  }

  input.form-text {
    height: 29px;
  }
  button.epa-search-button {
    right: 16px;

    background-color: #02bfe7;
    background-image: url(https://www.epa.gov/sites/all/themes/epa/img/svg/search.svg);
    background-position: 50% 50%;
    background-repeat: no-repeat;
    background-size: 12px 12px;
    border-color: #02bfe7;
    border-radius: 0 3px 3px 0;
    box-sizing: border-box;
    color: #4aa564;
    font: 0/0 a;
    height: 27px;
    right: 0px;
    position: absolute;
    text-decoration: none;
    text-indent: -999em;
    text-shadow: none;
    top: 1px;
    width: 44px;
  }
}
</style>
