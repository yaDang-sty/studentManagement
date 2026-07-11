<template>
  <div class="page">
    <el-card class="toolbar-card">
      <el-row :gutter="12" align="middle">
        <el-col :span="3">
          <el-input v-model="searchKeyword" placeholder="搜索账号" clearable @keyup.enter="handleSearch" @clear="loadList" />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon> 搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="openAddDialog"><el-icon><Plus /></el-icon> 新增账户</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" stripe border v-loading="loading" empty-text="暂无账户数据" style="width: 100%">
        <el-table-column prop="id" label="账号" width="150"  />
        <el-table-column prop="password" label="密码" width="150">
          <template #default="{ row }">
            <span>{{ showPasswords[row.id] ? row.password : "********" }}</span>
            <el-button size="small" link @click="togglePassword(row.id)">
              <el-icon><View v-if="!showPasswords[row.id]" /><Hide v-else /></el-icon>
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditDialog(row)"><el-icon><Edit /></el-icon> 修改密码</el-button>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改密码' : '新增账户'" width="400px" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="账号" prop="id" v-if="!isEdit">
          <el-input v-model="form.id" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item :label="isEdit ? '新密码' : '密码'" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">{{ isEdit ? '更新' : '确定' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Plus, Edit, Delete, View, Hide } from "@element-plus/icons-vue";
import api from "../api";

const list = ref([]);
const loading = ref(false);
const searchKeyword = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const showPasswords = reactive({});

const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const formRef = ref(null);
const form = reactive({ id: "", password: "" });

const rules = {
  id: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

const loadList = async () => {
  loading.value = true;
  try {
    const res = await api.post("/accounts/list", { page: currentPage.value, pageSize: pageSize.value });
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("加载账户列表失败") }
  finally { loading.value = false }
};

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) { loadList(); return }
  loading.value = true;
  try {
    const res = await api.post("/accounts/search", { keyword: searchKeyword.value.trim(), page: currentPage.value, pageSize: pageSize.value });
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("搜索失败") }
  finally { loading.value = false }
};

const resetSearch = () => { searchKeyword.value = ""; currentPage.value = 1; loadList() };

const togglePassword = (id) => { showPasswords[id] = !showPasswords[id] };

const openAddDialog = () => {
  isEdit.value = false;
  form.id = "";
  form.password = "";
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  form.id = row.id;
  form.password = "";
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await api.post("/accounts/update", { id: form.id, password: form.password });
      ElMessage.success("修改密码成功");
    } else {
      await api.post("/accounts/create", { id: form.id, password: form.password });
      ElMessage.success("新增账户成功");
    }
    dialogVisible.value = false;
    loadList();
  } catch (err) { ElMessage.error(err.userMessage || "操作失败") }
  finally { submitting.value = false }
};

const handleDelete = (row) => {
  ElMessageBox.confirm("确定删除账户 " + row.id + " 吗？", "确认删除", {
    type: "warning", confirmButtonText: "确定", cancelButtonText: "取消",
  }).then(async () => {
    try {
      await api.post("/accounts/delete", { id: row.id });
      ElMessage.success("删除成功");
      loadList();
    } catch (err) { ElMessage.error(err.userMessage || "删除失败") }
  }).catch(() => {});
};

onMounted(() => loadList());
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.toolbar-card, .table-card { border-radius: 8px; }
</style>