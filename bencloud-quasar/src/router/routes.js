const routes = [
  {
    path: "/login",
    component: () => import("layouts/MainLayoutPublic.vue"),
    children: [
      {
        path: "",
        component: () => import("pages/public/Login.vue"),
        meta: { requiresAuth: false, requiresAdmin: false },
      },
    ],
  },
  {
    path: "/",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("pages/Index.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/help",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("pages/Help.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/settings",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("pages/Settings.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/language",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("pages/Language.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/feedback",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("pages/Feedback.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/analysis",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("pages/Analysis.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/analysis/review",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("src/pages/analysis/ReviewAndSubmit.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/datacenter",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("pages/datacenter/DataCenter.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/datacenter/review-air-quality",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () =>
          import("src/pages/datacenter/airquality/ReviewAirQuality.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  {
    path: "/datacenter/manage-tasks",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("src/pages/datacenter/tasks/ManageTasks.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

   {
    path: "/datacenter/manage-tasks",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () => import("src/pages/datacenter/tasks/ManageTasks.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },
  {
    path: "/datacenter/view-export-task/:task_uuid",
    props: true,
    name: "view-results",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        component: () =>
          import("src/pages/datacenter/tasks/ViewTaskResults.vue"),
        meta: { requiresAuth: true, requiresAdmin: false },
      },
    ],
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: "/:catchAll(.*)*",
    component: () => import("pages/Error404.vue"),
  },
];

export default routes;
