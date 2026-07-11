<template>
  <div id="app-container">
    <el-container style="height: 100vh; min-width: 1024px">
      <el-aside width="220px" class="app-aside">
        <div class="aside-title">教务管理系统</div>
        <el-menu :default-active="route.path" router background-color="#1d1e2c" text-color="#bfcbd9" active-text-color="#409eff">
          <el-menu-item v-if="hasRole('TECH_ADMIN','BUSINESS_ADMIN')" index="/student"><el-icon><User /></el-icon><span>学生管理</span></el-menu-item>
          <el-menu-item v-if="hasRole('TECH_ADMIN')" index="/dormitory"><el-icon><HomeFilled /></el-icon><span>宿舍管理</span></el-menu-item>
          <el-menu-item v-if="hasRole('TECH_ADMIN','BUSINESS_ADMIN')" index="/course"><el-icon><Reading /></el-icon><span>课程管理</span></el-menu-item>
          <el-menu-item v-if="hasRole('TECH_ADMIN','BUSINESS_ADMIN','VISITOR')" index="/score"><el-icon><Edit /></el-icon><span>成绩管理</span></el-menu-item>
          <el-menu-item v-if="hasRole('TECH_ADMIN','BUSINESS_ADMIN','VISITOR')" index="/stats"><el-icon><TrendCharts /></el-icon><span>成绩统计</span></el-menu-item>
          <el-menu-item v-if="hasRole('TECH_ADMIN')" index="/users"><el-icon><Setting /></el-icon><span>权限管理</span></el-menu-item>
        </el-menu>
      </el-aside>
      <div class="top-right-bar">
        <span class="user-type-tag">{{ userTypeDisplay }}</span>
        <el-button type="danger" size="small" plain @click="handleLogout">退出登录</el-button>
      </div>
      <el-container>
        <el-header class="app-header"><h2>{{ pageTitle }}</h2></el-header>
        <el-main class="app-main"><router-view /></el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { User, HomeFilled, Reading, Edit, TrendCharts, Setting } from "@element-plus/icons-vue";

const route = useRoute();
const router = useRouter();

const loginUser = JSON.parse(localStorage.getItem("loginUser") || "{}");
const userType = loginUser.userType || "VISITOR";

const hasRole = (...roles) => roles.includes(userType);

const userTypeDisplay = computed(() => {
  const map = { TECH_ADMIN: "技术管理", BUSINESS_ADMIN: "业务管理", VISITOR: "游客" };
  const name = loginUser.name || "";
  const type = map[userType] || userType;
  return name ? `${name} (${type})` : type;
});

const pageTitle = computed(() => {
  const map = {
    "/student": "学生管理",
    "/dormitory": "宿舍管理",
    "/course": "课程管理",
    "/score": "成绩管理",
    "/stats": "成绩统计",
    "/users": "权限管理",
  };
  return map[route.path] || "教务管理系统";
});

const handleLogout = () => {
  ElMessageBox.confirm("确定要退出登录吗？", "提示", { type: "info" })
    .then(() => {
      localStorage.removeItem("loginUser");
      router.push("/login");
    })
    .catch(() => {});
};
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Microsoft YaHei", sans-serif; }
.app-aside { background-color: #1d1e2c; overflow-y: auto; }
.aside-title { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 18px; font-weight: bold; letter-spacing: 2px; border-bottom: 1px solid #2d2f4a; }
.top-right-bar { position: fixed; top: 14px; right: 20px; z-index: 999; display: flex; align-items: center; gap: 12px; }
.user-type-tag { color: #fff; font-size: 13px; background: rgba(255,255,255,0.15); padding: 4px 12px; border-radius: 4px; }
.app-header { background: linear-gradient(135deg, #409eff, #337ecc); color: #fff; display: flex; align-items: center; padding: 0 24px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15); height: 60px; }
.app-header h2 { font-size: 20px; letter-spacing: 1px; }
.app-main { background-color: #f0f2f5; padding: 20px; overflow-y: auto; height: calc(100vh - 60px); }
</style>
