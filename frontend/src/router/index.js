import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import StudentList from "../views/StudentList.vue";
import DormitoryList from "../views/DormitoryList.vue";
import CourseList from "../views/CourseList.vue";
import ScoreList from "../views/ScoreList.vue";
import ScoreStats from "../views/ScoreStats.vue";

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", name: "Login", component: Login },
  {
    path: "/student",
    name: "StudentList",
    component: StudentList,
    meta: { requiresAuth: true },
  },
  {
    path: "/dormitory",
    name: "DormitoryList",
    component: DormitoryList,
    meta: { requiresAuth: true },
  },
  {
    path: "/course",
    name: "CourseList",
    component: CourseList,
    meta: { requiresAuth: true },
  },
  {
    path: "/score",
    name: "ScoreList",
    component: ScoreList,
    meta: { requiresAuth: true },
  },
  {
    path: "/stats",
    name: "ScoreStats",
    component: ScoreStats,
    meta: { requiresAuth: true },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem("loginUser");
  if (to.meta.requiresAuth && !user) {
    next("/login");
  } else if (to.path === "/login" && user) {
    next("/student");
  } else {
    next();
  }
});

export default router;
