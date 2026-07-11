<template>
  <div class="page">
    <!-- 搜索栏 -->
    <el-card class="toolbar-card">
      <el-row :gutter="12" align="middle">
        <el-col :span="8">
          <el-input v-model="searchKeyword" placeholder="搜索学号 / 姓名 / 角色" clearable @keyup.enter="handleSearch" @clear="loadList" />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="openAddDialog"><el-icon><Plus /></el-icon> 新增用户</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" stripe border v-loading="loading" empty-text="暂无用户数据" style="width: 100%">
        <el-table-column prop="studentNo" label="学号(ID)" width="150" align="center" />
        <el-table-column prop="name" label="姓名" width="150" />
        <el-table-column label="用户类型" min-width="250">
          <template #default="{ row }">
            <el-select v-model="row.userType" @change="(val) => handleTypeChange(row, val)" style="width: 160px">
              <el-option label="技术管理" value="TECH_ADMIN" />
              <el-option label="业务管理" value="BUSINESS_ADMIN" />
              <el-option label="游客" value="VISITOR" />
            </el-select>
            <el-tag :type="typeTagType(row.userType)" effect="plain" style="margin-left: 10px">
              {{ row.userTypeDisplay || typeDisplayName(row.userType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="handleDelete(row)"><el-icon><Delete /></el-icon> 删除</el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="addDialogVisible" title="新增用户" width="450px" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="addForm" :rules="rules" label-width="80px">
        <el-form-item label="选择学生" prop="studentId">
          <el-select v-model="addForm.studentId" filterable placeholder="请选择学生" style="width: 100%">
            <el-option v-for="s in availableStudents" :key="s.id" :label="s.studentNo + ' - ' + s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="addForm.userType" placeholder="选择用户类型" style="width: 100%">
            <el-option label="技术管理" value="TECH_ADMIN" />
            <el-option label="业务管理" value="BUSINESS_ADMIN" />
            <el-option label="游客" value="VISITOR" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Plus, Delete } from "@element-plus/icons-vue";
import api from "../api";

const list = ref([]);
const loading = ref(false);
const searchKeyword = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const addDialogVisible = ref(false);
const submitting = ref(false);
const formRef = ref(null);
const availableStudents = ref([]);
const addForm = ref({ studentId: null, userType: "VISITOR" });
const rules = { studentId: [{ required: true, message: "请选择学生", trigger: "change" }] };

const loadList = async () => {
  loading.value = true;
  try {
    const res = await api.post("/users/list", { page: currentPage.value, pageSize: pageSize.value });
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("加载用户列表失败") }
  finally { loading.value = false }
};

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) { loadList(); return }
  loading.value = true;
  try {
    const res = await api.post("/users/search", { keyword: searchKeyword.value.trim(), page: currentPage.value, pageSize: pageSize.value });
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("搜索失败") }
  finally { loading.value = false }
};

const resetSearch = () => { searchKeyword.value = ""; currentPage.value = 1; loadList() };

const handleTypeChange = async (row, newType) => {
  try {
    await api.post("/users/updateUserType", { id: row.id, userType: newType });
    ElMessage.success("更新权限成功");
    const loginUser = localStorage.getItem("loginUser");
    if (loginUser) {
      const userData = JSON.parse(loginUser);
      if (userData.studentNo === row.studentNo) {
        userData.userType = newType;
        localStorage.setItem("loginUser", JSON.stringify(userData));
      }
    }
  } catch (err) { ElMessage.error(err.userMessage || "更新失败"); loadList() }
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定删除用户 " + row.name + " 的权限记录吗？", "确认删除", {
    type: "warning", confirmButtonText: "确定", cancelButtonText: "取消",
  }).then(async () => {
    try {
      await api.post("/users/delete", { id: row.id });
      ElMessage.success("删除成功");
      loadList();
    } catch (err) { ElMessage.error(err.userMessage || "删除失败") }
  }).catch(() => {});
};

const openAddDialog = async () => {
  addForm.value = { studentId: null, userType: "VISITOR" };
  try {
    const res = await api.post("/users/studentsWithoutUser");
    availableStudents.value = res.data;
  } catch { ElMessage.error("获取学生列表失败") }
  addDialogVisible.value = true;
};

const handleAddSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    await api.post("/users/create", addForm.value);
    ElMessage.success("新增用户成功");
    addDialogVisible.value = false;
    loadList();
  } catch (err) { ElMessage.error(err.userMessage || "新增失败") }
  finally { submitting.value = false }
};

const typeDisplayName = (type) => {
  const map = { TECH_ADMIN: "技术管理", BUSINESS_ADMIN: "业务管理", VISITOR: "游客" };
  return map[type] || type;
};

const typeTagType = (type) => {
  const map = { TECH_ADMIN: "danger", BUSINESS_ADMIN: "warning", VISITOR: "info" };
  return map[type] || "info";
};

onMounted(() => loadList());
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.toolbar-card, .table-card { border-radius: 8px; }
</style>