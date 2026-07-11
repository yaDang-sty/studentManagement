<template>
  <div class="login-wrapper">
    <div class="login-bg"></div>
    <div class="login-card">
      <div class="login-icon">🎓</div>
      <h2 class="login-title">教务管理系统</h2>
      <p class="login-subtitle">学生成绩综合管理平台</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @keyup.enter="handleLogin">
        <el-form-item prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入账号（学号）" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
      <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { User, Lock } from "@element-plus/icons-vue";
import api from "../api";

const router = useRouter();
const formRef = ref(null);
const loading = ref(false);
const errorMsg = ref("");

const form = reactive({
  studentNo: "",
  password: "",
});

const rules = {
  studentNo: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const getDefaultRoute = (userType) => {
  const map = {
    TECH_ADMIN: "/student",
    BUSINESS_ADMIN: "/student",
    VISITOR: "/score",
  };
  return map[userType] || "/score";
};

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  loading.value = true;
  errorMsg.value = "";
  try {
    const res = await api.post("/auth/login", { studentNo: form.studentNo, password: form.password });
    if (res.data.success) {
      const userData = { ...res.data.student, token: res.data.token };
      localStorage.setItem("loginUser", JSON.stringify(userData));
      ElMessage.success("登录成功");
      const defaultRoute = getDefaultRoute(userData.userType);
      router.push(defaultRoute);
    } else {
      errorMsg.value = res.data.message;
    }
  } catch {
    errorMsg.value = "登录请求失败，请检查后端服务是否启动";
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  z-index: 1000;
}

.login-bg {
  position: absolute;
  inset: 0;
  background: url("/bg.jpg") center/cover no-repeat;
  z-index: 0;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  padding: 40px 36px;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 8px 48px rgba(0,0,0,0.35);
}

.login-icon { text-align: center; font-size: 56px; margin-bottom: 8px; }
.login-title { text-align: center; color: #1d1e2c; font-size: 24px; font-weight: bold; letter-spacing: 3px; margin-bottom: 4px; }
.login-subtitle { text-align: center; color: #999; font-size: 13px; margin-bottom: 24px; letter-spacing: 1px; }
.login-btn { width: 100%; height: 44px; font-size: 16px; letter-spacing: 6px; margin-top: 8px; border-radius: 8px; }
.error-msg { color: #f56c6c; text-align: center; margin-top: 16px; font-size: 14px; }
</style>