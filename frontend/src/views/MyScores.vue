<template>
  <div class="page">
    <el-card class="toolbar-card">
      <el-row :gutter="12" align="middle">
        <el-col :span="6">
          <span style="font-weight: bold; font-size: 16px;">{{ studentName }} 的成绩单</span>
        </el-col>
        <el-col :span="6">
          <el-select v-model="semester" placeholder="全部学期" clearable style="width: 100%" @change="loadList">
            <el-option label="大一第一学期" value="大一第一学期" />
            <el-option label="大一第二学期" value="大一第二学期" />
            <el-option label="大二第一学期" value="大二第一学期" />
            <el-option label="大二第二学期" value="大二第二学期" />
            <el-option label="大三第一学期" value="大三第一学期" />
            <el-option label="大三第二学期" value="大三第二学期" />
            <el-option label="大四第一学期" value="大四第一学期" />
            <el-option label="大四第二学期" value="大四第二学期" />
          </el-select>
        </el-col>
        <el-col :span="2">
          <el-button type="primary" @click="loadList"><el-icon><Search /></el-icon> 查询</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" stripe border v-loading="loading" empty-text="暂无成绩数据" style="width: 100%">
        <el-table-column prop="courseNo" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="160" width="140" show-overflow-tooltip />
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="scoreTagType(row.score)" effect="plain" size="large">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="150" />
      </el-table>
      <div style="display:flex;justify-content:flex-end;margin-top:16px">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadList"
          @size-change="loadList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import api from "../api";

const list = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const studentName = ref("");
const studentNo = ref("");
const semester = ref("");

const scoreTagType = (score) => {
  if (score >= 90) return "success";
  if (score >= 80) return "primary";
  if (score >= 70) return "warning";
  if (score >= 60) return "info";
  return "danger";
};

const loadList = async () => {
  if (!studentNo.value) return;
  loading.value = true;
  try {
    const params = { studentNo: studentNo.value, page: currentPage.value, pageSize: pageSize.value };
    if (semester.value) params.semester = semester.value;
    const res = await api.post("/score/query", params);
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("加载成绩失败") }
  finally { loading.value = false }
};

onMounted(() => {
  const loginUser = localStorage.getItem("loginUser");
  if (loginUser) {
    const userData = JSON.parse(loginUser);
    studentNo.value = userData.studentNo || "";
    studentName.value = userData.name || "";
  }
  loadList();
});
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.toolbar-card, .table-card { border-radius: 8px; }
</style>