<template>
  <div class="student-page">
    <!-- 搜索与操作栏 -->
    <el-card class="toolbar-card">
      <el-row :gutter="20" align="middle">
        <el-col :span="4">
          <el-input v-model="searchForm.name" placeholder="姓名" clearable size="default" />
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchForm.studentNo" placeholder="学号" clearable size="default" />
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchForm.major" placeholder="专业" clearable size="default" />
        </el-col>
        <el-col :span="3">
          <el-input v-model="searchForm.grade" placeholder="年级" clearable size="default" />
        </el-col>
        <el-col :span="4">
          <el-input v-model="searchForm.studentClass" placeholder="班级" clearable size="default" />
        </el-col>
        <el-col :span="5">
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="resetSearch">重置</el-button>
          <el-button type="success" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 添加学生
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 学生列表 -->
    <el-card class="table-card">
      <el-table
        :data="students"
        stripe
        border
        v-loading="loading"
        empty-text="暂无学生数据"
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="gender" label="性别" width="70" align="center" />
        <el-table-column prop="age" label="年龄" width="70" align="center" />
        <el-table-column prop="grade" label="年级" width="90" align="center" />
        <el-table-column prop="studentClass" label="班级" width="200" align="center" />
        <el-table-column prop="major" label="专业" width="150" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" width="190" show-overflow-tooltip />
        <el-table-column prop="phone" label="电话" width="130" />
        <el-table-column prop="address" label="地址" min-width="160" show-overflow-tooltip />
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
          @current-change="loadStudents"
          @size-change="loadStudents"
        />
      </div>
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学生' : '添加学生'"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        label-position="right"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number
                v-model="form.age"
                :min="1"
                :max="150"
                placeholder="年龄"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-select v-model="form.grade" placeholder="请选择年级" clearable style="width: 100%">
                <el-option label="大一" value="大一" />
                <el-option label="大二" value="大二" />
                <el-option label="大三" value="大三" />
                <el-option label="大四" value="大四" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="studentClass">
              <el-input v-model="form.studentClass" placeholder="如：计科1班" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
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

const students = ref([]);
const loading = ref(false);
const searchForm = reactive({
  name: "",
  studentNo: "",
  major: "",
  grade: "",
  studentClass: "",
});

const dialogVisible = ref(false);
const isEdit = ref(false);
const submitting = ref(false);
const editId = ref(null);
const formRef = ref(null);

const form = reactive({
  studentNo: "",
  name: "",
  gender: "男",
  age: null,
  grade: "",
  studentClass: "",
  major: "",
  email: "",
  phone: "",
  address: "",
});

const rules = {
  studentNo: [{ required: true, message: "请输入学号", trigger: "blur" }],
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  email: [{ type: "email", message: "请输入正确的邮箱格式", trigger: "blur" }],
};

const loadStudents = async () => {
  loading.value = true;
  try {
    const res = await api.post("/students/list", { page: currentPage.value, pageSize: pageSize.value });
    students.value = res.data.records;
    total.value = res.data.total;
  } catch {
    ElMessage.error("加载学生列表失败");
  } finally {
    loading.value = false;
  }
};

const handleSearch = async () => {
  const hasFilters = searchForm.name || searchForm.studentNo || searchForm.major || searchForm.grade || searchForm.studentClass;
  if (!hasFilters) {
    loadStudents();
    return;
  }
  loading.value = true;
  try {
    const res = await api.post("/students/searchMultiple", {
      name: searchForm.name || "",
      studentNo: searchForm.studentNo || "",
      major: searchForm.major || "",
      grade: searchForm.grade || "",
      studentClass: searchForm.studentClass || "",
      page: currentPage.value,
      pageSize: pageSize.value,
    });
    students.value = res.data.records;
    total.value = res.data.total;
  } catch {
    ElMessage.error("搜索失败");
  } finally {
    loading.value = false;
  }
};

const resetSearch = () => {
  searchForm.name = "";
  searchForm.studentNo = "";
  searchForm.major = "";
  searchForm.grade = "";
  searchForm.studentClass = "";
  currentPage.value = 1;
  loadStudents();
};

const openAddDialog = () => {
  isEdit.value = false;
  editId.value = null;
  Object.assign(form, {
    studentNo: "",
    name: "",
    gender: "男",
    age: null,
    grade: "",
    studentClass: "",
    major: "",
    email: "",
    phone: "",
    address: "",
  });
  dialogVisible.value = true;
};

const openEditDialog = (row) => {
  isEdit.value = true;
  editId.value = row.id;
  Object.assign(form, {
    id: row.id,
    studentNo: row.studentNo,
    name: row.name,
    gender: row.gender || "男",
    age: row.age,
    grade: row.grade || "",
    studentClass: row.studentClass || "",
    major: row.major || "",
    email: row.email || "",
    phone: row.phone || "",
    address: row.address || "",
  });
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) return;

  submitting.value = true;
  try {
    if (isEdit.value) {
      await api.post("/students/update", form);
      ElMessage.success("更新成功");
    } else {
      await api.post("/students/create", form);
      ElMessage.success("添加成功");
    }
    dialogVisible.value = false;
    loadStudents();
  } catch (err) {
    ElMessage.error(err.userMessage || "操作失败");
  } finally {
    submitting.value = false;
  }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除学生「${row.name}」吗？`, "确认删除", {
    type: "warning",
    confirmButtonText: "确定",
    cancelButtonText: "取消",
  }).then(async () => {
    try {
      await api.post("/students/delete", { id: row.id });
      ElMessage.success("删除成功");
      loadStudents();
    } catch (err) {
      ElMessage.error(err.userMessage || "删除失败");
    }
  }).catch(() => {});
};

const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

onMounted(() => {
  loadStudents();
});
</script>

<style scoped>
.student-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.toolbar-card {
  border-radius: 8px;
}

.table-card {
  border-radius: 8px;
}

.text-right {
  text-align: right;
}
</style>
