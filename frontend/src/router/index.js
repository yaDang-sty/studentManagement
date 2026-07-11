import { createRouter, createWebHistory } from "vue-router";
import Login from "../views/Login.vue";
import StudentList from "../views/StudentList.vue";
import DormitoryList from "../views/DormitoryList.vue";
import CourseList from "../views/CourseList.vue";
import ScoreList from "../views/ScoreList.vue";
import ScoreStats from "../views/ScoreStats.vue";
import UserList from "../views/UserList.vue";
import AccountList from "../views/AccountList.vue";

const routes = [
  { path: "/", redirect: "/login" },
  { path: "/login", name: "Login", component: Login },
  {
    path: "/student",
    name: "StudentList",
    component: StudentList,
    meta: { requiresAuth: true, roles: ["TECH_ADMIN", "BUSINESS_ADMIN"] },
  },
  {
    path: "/dormitory",
    name: "DormitoryList",
    component: DormitoryList,
    meta: { requiresAuth: true, roles: ["TECH_ADMIN"] },
  },
  {
    path: "/course",
    name: "CourseList",
    component: CourseList,
    meta: { requiresAuth: true, roles: ["TECH_ADMIN", "BUSINESS_ADMIN"] },
  },
  {
    path: "/score",
    name: "ScoreList",
    component: ScoreList,
    meta: { requiresAuth: true, roles: ["TECH_ADMIN", "BUSINESS_ADMIN", "VISITOR"] },
  },
  {
    path: "/stats",
    name: "ScoreStats",
    component: ScoreStats,
    meta: { requiresAuth: true, roles: ["TECH_ADMIN", "BUSINESS_ADMIN", "VISITOR"] },
  },
  {
    path: "/users",
    name: "UserList",
    component: UserList,
    meta: { requiresAuth: true, roles: ["TECH_ADMIN"] },
  },
  {
    path: "/accounts",
    name: "AccountList",
    component: AccountList,
    meta: { requiresAuth: true, roles: ["TECH_ADMIN"] },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫 - 权限控制
router.beforeEach((to, from, next) => {
  const userStr = localStorage.getItem("loginUser");
  const user = userStr ? JSON.parse(userStr) : null;

  if (to.meta.requiresAuth && !user) {
    next("/login");
    return;
  }

  if (to.path === "/login" && user) {
    next("/student");
    return;
  }

  if (to.meta.roles && user) {
    const userType = user.userType || "VISITOR";
    if (!to.meta.roles.includes(userType)) {
      next("/score");
      return;
    }
  }

  next();
});

export default router;