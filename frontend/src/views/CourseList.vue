<template>
  <div class="page">
    <!-- 搜索与操作栏 -->
    <el-card class="toolbar-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程名 / 教师 / 课程编号"
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
            <el-icon><Plus /></el-icon> 添加课程
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 数据列表 -->
    <el-card class="table-card">
      <el-table :data="list" stripe border v-loading="loading" empty-text="暂无课程数据" style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="courseNo" label="课程编号" width="130" />
        <el-table-column prop="courseName" label="课程名称" width="150" show-overflow-tooltip />
        <el-table-column prop="teacher" label="授课教师" width="100" />
        <el-table-column prop="credit" label="学分" width="70" align="center" />
        <el-table-column prop="classroom" label="教室" width="120" />
        <el-table-column prop="schedule" label="上课时间" width="180" show-overflow-tooltip />
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '添加课程'" width="550px" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" label-position="right">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程编号" prop="courseNo">
              <el-input v-model="form.courseNo" placeholder="如：CS101" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="授课教师" prop="teacher">
              <el-input v-model="form.teacher" placeholder="请输入教师姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学分" prop="credit">
              <el-input-number v-model="form.credit" :min="1" :max="20" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="教室" prop="classroom">
              <el-input v-model="form.classroom" placeholder="如：A-301" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上课时间" prop="schedule">
              <el-input v-model="form.schedule" placeholder="如：周一 1-2节" />
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
  courseNo: "",
  courseName: "",
  teacher: "",
  credit: 3,
  classroom: "",
  schedule: "",
});

const rules = {
  courseNo: [{ required: true, message: "请输入课程编号", trigger: "blur" }],
  courseName: [{ required: true, message: "请输入课程名称", trigger: "blur" }],
};

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const loadList = async () => {
  loading.value = true;
  try {
    const res = await api.post("/course/list", { page: currentPage.value, pageSize: pageSize.value });
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("加载课程列表失败") }
  finally { loading.value = false }
};

const handleSearch = async () => {
  if (!searchKeyword.value.trim()) { loadList(); return }
  loading.value = true;
  try {
    const res = await api.post("/course/search", { keyword: searchKeyword.value.trim() });
      list.value = res.data.records;
      total.value = res.data.total;
  } catch { ElMessage.error("搜索失败") }
  finally { loading.value = false }
};

const resetSearch = () => { searchKeyword.value = ""; currentPage.value = 1; loadList() };

const openAddDialog = () => {
  isEdit.value = false;
  Object.assign(form, { courseNo: "", courseName: "", teacher: "", credit: 3, classroom: "", schedule: "" });
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  Object.assign(form, { id: row.id, courseNo: row.courseNo, courseName: row.courseName, teacher: row.teacher || "", credit: row.credit ?? 3, classroom: row.classroom || "", schedule: row.schedule || "" });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    if (isEdit.value) {
      await api.post("/course/update", form);
      ElMessage.success("更新成功");
    } else {
      await api.post("/course/create", form);
      ElMessage.success("添加成功");
    }
    dialogVisible.value = false;
    loadList();
  } catch (err) { ElMessage.error(err.userMessage || "操作失败") }
  finally { submitting.value = false }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除课程「${row.courseName}」吗？`, "确认删除", { type: "warning" })
    .then(async () => {
      try {
        await api.post("/course/delete", { id: row.id });
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
