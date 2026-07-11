<template>
  <div class="page">
    <!-- 搜索与操作栏 -->
    <el-card class="toolbar-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索姓名 / 楼栋 / 房间"
            clearable
            @keyup.enter="handleSearch"
            @clear="loadList"
          >
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-col>
        <el-col :span="12" class="text-right">
          <el-button type="success" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 分配宿舍
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 数据列表 -->
    <el-card class="table-card">
      <el-table :data="list" stripe border v-loading="loading" empty-text="暂无宿舍数据" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="building" label="楼栋" width="120" />
        <el-table-column prop="room" label="房间号" width="100" align="center" />
        <el-table-column prop="bed" label="床位" width="80" align="center" />
        <el-table-column prop="phone" label="联系电话" width="140" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditDialog(row)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
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

    <!-- 添加/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑宿舍' : '分配宿舍'" width="550px" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" label-position="right">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼栋" prop="building">
              <el-input v-model="form.building" placeholder="如：A栋、B栋" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房间号" prop="room">
              <el-input v-model="form.room" placeholder="如：301" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="床位" prop="bed">
              <el-input v-model="form.bed" placeholder="如：1号床" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ isEdit ? '更新' : '添加' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import api from "../api";

const list = ref([]);
const loading = ref(false);
const searchKeyword = ref("");
const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const formRef = ref(null);

const form = reactive({
  studentNo: "",
  studentName: "",
  building: "",
  room: "",
  bed: "",
  phone: "",
});

const rules = {
  studentNo: [{ required: true, message: "请输入学号", trigger: "blur" }],
  studentName: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  building: [{ required: true, message: "请输入楼栋", trigger: "blur" }],
  room: [{ required: true, message: "请输入房间号", trigger: "blur" }],
};

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const loadList = async () => {
  loading.value = true;
  try {
    const res = await api.post("/dormitory/list", { page: currentPage.value, pageSize: pageSize.value });
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("加载宿舍列表失败") }
  finally { loading.value = false }
};

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) { loadList(); return }
  loading.value = true;
  try {
    const res = await api.post("/dormitory/search", { keyword: searchKeyword.value.trim(), page: currentPage.value, pageSize: pageSize.value });
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("搜索失败") }
  finally { loading.value = false }
};

const resetSearch = () => { searchKeyword.value = ""; currentPage.value = 1; loadList() };

const openAddDialog = () => {
  isEdit.value = false;
  Object.assign(form, { studentNo: "", studentName: "", building: "", room: "", bed: "", phone: "" });
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  Object.assign(form, { id: row.id, studentNo: row.studentNo, studentName: row.studentName, building: row.building, room: row.room, bed: row.bed || "", phone: row.phone || "" });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await api.post("/dormitory/update", form);
      ElMessage.success("更新成功");
    } else {
      await api.post("/dormitory/create", form);
      ElMessage.success("分配成功");
    }
    dialogVisible.value = false;
    loadList();
  } catch (err) { ElMessage.error(err.userMessage || "操作失败") }
  finally { submitting.value = false }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除「${row.studentName}」的宿舍记录吗？`, "确认删除", { type: "warning" })
    .then(async () => {
      try {
        await api.post("/dormitory/delete", { id: row.id });
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
.text-right { text-align: right; }
</style>
