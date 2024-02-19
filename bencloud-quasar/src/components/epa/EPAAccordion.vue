<template>
    <head>
        <link href='https://fonts.googleapis.com/css?family=Source+Sans+Pro' rel='stylesheet' type='text/css'>
    </head>
    <section
        class="usa-banner"
        aria-label="Official website of the United States government"
    >
        <div class="usa-accordion" v-if="showHeader">
        <header class="usa-banner__header">
            <div class="usa-banner__inner">
            <div class="grid-col-auto">
                <img
                aria-hidden="true"
                class="usa-banner__header-flag"
                src="./assets/us_flag_small.png"
                alt="U.S Flag"
                />
            </div>
            <div class="grid-col-fill tablet:grid-col-auto" aria-hidden="true">
                <p class="usa-banner__header-text">
                An official website of the United States government
                </p>
            </div>
            <button
                type="button"
                class="usa-accordion__button usa-banner__button"
                aria-expanded="false"
                aria-controls="gov-banner"
                @click="toggleContent()"
            >
                <span class="usa-banner__button-text">Here’s how you know <q-icon v-if="!showContent" name="mdi-chevron-down" size="xs" /><q-icon v-if="showContent" name="mdi-chevron-up" size="xs" /></span>
            </button>
            </div>
        </header>
        <div
            class="usa-banner__content usa-accordion__content"
            id="gov-banner" v-if="showContent"
        >
            <div class="grid-row grid-gap-lg">
            <div class="usa-banner__guidance tablet:grid-col-6">
                <img
                class="usa-banner__icon usa-media-block__img"
                src="./assets/icon-dot-gov.svg"
                role="img"
                alt=""
                aria-hidden="true"
                />
                <div class="usa-media-block__body">
                <p>
                    <strong>Official websites use .gov</strong><br />A
                    <strong>.gov</strong> website belongs to an official government
                    organization in the United States.
                </p>
                </div>
            </div>
            <div class="usa-banner__guidance tablet:grid-col-6">
                <img
                class="usa-banner__icon usa-media-block__img"
                src="./assets/icon-https.svg"
                role="img"
                alt=""
                aria-hidden="true"
                />
                <div class="usa-media-block__body">
                <p>
                    <strong>Secure .gov websites use HTTPS</strong><br />A
                    <strong>lock</strong> (
                    <span class="icon-lock"
                    ><svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="52"
                        height="64"
                        viewBox="0 0 52 64"
                        class="usa-banner__lock-image"
                        role="img"
                        aria-labelledby="banner-lock-description-default"
                        focusable="false"
                    >
                        <title id="banner-lock-title-default">Lock</title>
                        <desc id="banner-lock-description-default">Locked padlock icon</desc>
                        <path
                        fill="#000000"
                        fill-rule="evenodd"
                        d="M26 0c10.493 0 19 8.507 19 19v9h3a4 4 0 0 1 4 4v28a4 4 0 0 1-4 4H4a4 4 0 0 1-4-4V32a4 4 0 0 1 4-4h3v-9C7 8.507 15.507 0 26 0zm0 8c-5.979 0-10.843 4.77-10.996 10.712L15 19v9h22v-9c0-6.075-4.925-11-11-11z"
                        />
                    </svg> </span
                    >) or <strong>https://</strong> means you’ve safely connected to
                    the .gov website. Share sensitive information only on official,
                    secure websites.
                </p>
                </div>
            </div>
            </div>
        </div>
        </div>
    </section>
</template>

<script>
import { defineComponent, ref, onMounted } from "vue";

export default defineComponent({
  name: "EPAAccordion",
  props: {
    showEpaHeaderFooter: {
      type: Boolean,
      default: false,
    },
  },

  setup(props, { emit }) {
    const showHeader = ref(false);
    const showContent = ref(false);

    
    function toggleContent() {
        showContent.value = !showContent.value;
    }

    onMounted(() => {
      if (props.showEpaHeaderFooter) {
        showHeader.value = true
      }
    });

    return {
      showHeader,
      showContent,
      toggleContent,
    };
  },
});
</script>

<style lang="scss">
.usa-accordion {
    font-family: "Source Sans Pro", "Noto Sans Arabic", "Noto Sans BN homepage", "Noto Sans GU homepage", "Noto Sans KR homepage", "Noto Sans SC homepage", "Noto Sans BN", "Noto Sans GU", "Noto Sans KR", "Noto Sans SC", "Noto Sans TC", "Helvetica Neue", Helvetica, Arial, sans;
    background-color: #f0f0f0;
    color: #1b1b1b;
    line-height: 1.5;
    list-style-type: none;
    font-size: 1.06rem;
    margin: 0;
    padding: 0;
    width: 100%;
    word-wrap: break-word;
    letter-spacing: .1px;

    .usa-banner__inner {
        -webkit-box-align: center;
        align-items: center;
        margin-left: auto;
        padding-left: 2rem;
        padding-right: 2rem;
        margin-right: auto;
        max-width: 64rem;
        display: flex;
    }

    .usa-banner__header {
        padding-bottom: .25rem;
        padding-top: .25rem;
        position: relative;
        color: #1b1b1b;
        min-height: 0;
        font-size: .9rem;
        font-weight: normal;
    }

    .usa-banner__header--expanded
    {
        background-color: rgba(0,0,0,0);
        display: block;
        font-size: .9rem;
        font-weight: normal;
        min-height: 0;
        padding-right: 0;
    }

    .grid-col-auto {
        flex: 0 1 auto;
        width: auto;
        max-width: 100%;
        -webkit-box-flex: 0;
    }

    .usa-banner__header-flag {
        margin-right: .5rem;
        padding-top: 0;
        float: left;
        width: 1rem;
        height: 11px;
        width: 16px;
    }

    .usa-banner__header-text {
        margin-bottom: 0;
        margin-top: 0;
        font-size: .9rem;
        line-height: 1.1;
    }

    .usa-banner__button{
        color: #005ea2;
        line-height: 1.1;
        font-weight: normal;
        text-align: left;
        height: auto;
        padding: 0;
        position: relative;
        bottom: auto;
        left: auto;
        right: auto;
        top: 1px;
        display: inline;
        margin-left: .5rem;
        border: none;
    }

    .usa-banner__button-text {
        text-decoration: underline;
    }

    .usa-banner__button::after {
        position: absolute;
        background-color: #005ea2;
        background: url(https://www.epa.gov/sites/all/themes/epa/img/svg/expand_more.svg) no-repeat;

    }

    .usa-banner__button:hover {
        color: #1a4480;
        background-color: rgba(0,0,0,0);
        box-shadow: none;
    }

    .usa-banner__content {
        padding-bottom: 1.5rem;
        padding-top: 1.5rem;
        padding-left: 2rem;
        padding-right: 2rem;
        margin-left: auto;
        margin-right: auto;
        max-width: 64rem;
        background-color: rgba(0,0,0,0);
        font-size: 1.1rem;
        overflow: hidden;
        width: 100%;
        color: #1b1b1b;
        margin-top: 0;
    }

    .usa-banner .grid-row.grid-gap-lg {
        margin-left: -0.75rem;
        margin-right: -0.75rem;
    }

    .usa-banner .grid-row.grid-gap-lg > * {
        padding-left: .75rem;
        padding-right: .75rem;
    }

    .grid-row {
        display: flex;
        flex-wrap: wrap;
    }

    .grid-col {
        position: relative;
        box-sizing: border-box;
    }

    .usa-banner__guidance {
        -webkit-box-flex: 0;
        flex: 0 1 auto;
        width: 50%;
        display: flex;
        -webkit-box-align: start;
        -ms-flex-align: start;
        align-items: flex-start;
        max-width: 64ex;
    }

    .usa-accordion__content > :last-child {
        margin-bottom: 0;
    }

    .usa-accordion__content > :first-child {
        margin-top: 0;
    }

    .usa-banner__icon {
        width: 2.5rem;
    }

    .usa-media-block__img {
        -ms-flex-negative: 0;
        flex-shrink: 0;
        margin-right: .5rem;
    }

    .usa-media-block__body {
        -webkit-box-flex: 1;
        -ms-flex: 1 1 0%;
        flex: 1 1 0%;
    }

    .usa-banner__lock-image {
        height: 1.5ex;
        width: 1.21875ex;
    }

    .usa-banner__content p:first-child {
        margin: 0;
    }

    strong {
        font-weight: bolder;
    }

    img {
        border: 0;
        font-style: italic;
        height: auto;
        max-width: 100%;
    }
}
</style>