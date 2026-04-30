# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BenCloudApp is the Vue 3 / Quasar 2 single-page application frontend for the BenMAP web tool (EPA air quality health impact analysis). It pairs with BenCloudServer (Java REST API). All source code is in the `bencloud-quasar/` subdirectory — run all commands from there.

## Build & Run Commands

```bash
# Working directory for all commands
cd bencloud-quasar

# Install dependencies
npm install
npm install -g @quasar/cli

# Start dev server (Linux/Mac) — hot reload on port 8080
QENV=development quasar dev

# Start dev server (Windows)
set QENV='development' & quasar dev

# Lint
npm run lint

# Production build
quasar build
```

There are no unit tests currently (`npm test` is a no-op).

## Environment Configuration

Environment variables are driven by `bencloud-quasar/.quasar.env.json`. The `QENV` env var selects the environment at dev time. Key environments:

| QENV | API_SERVER | Notes |
|---|---|---|
| `development` | `http://localhost:4567` | Points to local BenCloudServer |
| `iecdev` | `http://colo-wtest-1:4567` | Internal dev server |
| `staging` / `production` | `""` (relative) | Served behind a reverse proxy |

`SESSION_TIMEOUT` and `SESSION_WARNING` (in seconds) are also set per environment.

In components and composables, the API base URL is referenced as `process.env.API_SERVER`.

## Auth for Local Development

The `boot/axios.js` file injects WAM (EPA Web Access Management) headers directly for local dev:

```js
axios.defaults.headers.common['uid'] = 'test@test.com';
axios.defaults.headers.common['displayname'] = 'Local User';
axios.defaults.headers.common['ismemberof'] = 'BenMAP_Users';
```

Change `ismemberof` to `BenMAP_Admins` to test admin features locally. In deployed environments, these headers are injected by the WAM reverse proxy upstream.

## Architecture

### Stack

Vue 3 (Composition API + Options API mixed) + Quasar 2 + Vuex 4 + Vue Router 4 (hash mode). OpenLayers (`ol`) handles map rendering. Axios handles all API calls.

### Boot Files (`src/boot/`)

Three boot files execute at startup in order: `i18n` → `axios` → `auth`.

- **`auth.js`** — sets up two `router.beforeEach` guards: the first checks `/api/user` to determine if the user is a valid BenMAP user and routes to `/requestaccess` or `/error` accordingly; the second enforces admin permissions via Vuex state.

### Layouts

- **`MainLayout.vue`** — authenticated layout with EPA header/footer, nav bar, and `Banner` component. Used by all protected routes.
- **`MainLayoutPublic.vue`** — minimal layout for `/login`, `/requestaccess`, `/error`.

Route `meta` flags `requiresUser` and `requiresAdmin` control access guards.

### Vuex Store Modules (`src/store/`)

| Module | Purpose |
|---|---|
| `analysis` | All state for the 7-step HIF analysis wizard |
| `exposure` | State for the exposure analysis wizard |
| `hif` | Health impact function data |
| `valuation` | Valuation function data |
| `airquality` | Air quality surface data |
| `incidence` | Incidence/prevalence dataset data |
| `grids` | Grid definition data |
| `datacenter` | Data center / task management state |
| `app` | Application-level state |
| `auth` | User identity, permissions, redirect path |

Each module follows the standard Vuex structure: `state.js`, `mutations.js`, `actions.js`, `getters.js`, `index.js`.

### Composables (`src/composables/`)

Composables handle API calls and validation logic, keeping components lean. The pattern is a factory function returning a `fetch` async method plus reactive `data`, `error`, and `loading` refs:

```js
export const loadPollutants = (url) => {
  const data = ref(null), error = ref(null), loading = ref(false);
  const fetch = async () => { /* axios call */ };
  return { fetch };
};
```

Subdirectories: `analysis/`, `common/`, `exposure/`, `tasks/`, `validation/`.

Validation composables (`validation/analysis-validations.js`, `validation/exposure-validations.js`) export pure functions that take `store` and return booleans — called by the stepper's Continue button before advancing steps.

### Analysis Wizard (`/analysis`)

The main feature is a 7-step `q-stepper` (`components/navigation/ProgressStepper.vue`):

1. **Where?** — location/grid selection (`pages/analysis/Where.vue`)
2. **What pollutant?** — pollutant selection
3. **What air quality?** — pre- and post-policy AQ surface selection
4. **Who will be exposed?** — population dataset and years
5. **What health effects?** — HIF group and function selection
6. **Value of effects?** — valuation function selection
7. **Review & submit** — builds `BatchTaskConfig` JSON and POSTs to `/api/batch-tasks`

All wizard state lives in the `analysis` Vuex module. `composables/analysis/batch-task.js` assembles the payload from store state before submission.

### Session Management

`composables/common/sessionManager.js` is a singleton that intercepts all axios requests to reset idle timers. It shows a countdown warning dialog (`SESSION_WARNING` seconds before timeout) and an expiration dialog with a page overlay when `SESSION_TIMEOUT` is reached.

### Data Center (`/datacenter`)

Separate section for managing reference data (air quality surfaces, grids, HIF groups, incidence datasets, valuation functions) and monitoring/canceling analysis tasks. Task status polling uses `composables/tasks/active-tasks.js` and `completed-tasks.js`.
