<template>
  <div class="page">
    <!-- 操作卡片：录入成绩 -->
    <el-card class="toolbar-card">
      <template #header>
        <span style="font-weight: bold">📝 录入成绩</span>
      </template>
      <el-form :model="entryForm" :rules="entryRules" ref="entryFormRef" label-width="100px" :inline="true">
        <el-form-item label="学号" prop="studentNo">
          <el-select
            v-model="entryForm.studentNo"
            filterable
            remote
            reserve-keyword
            placeholder="搜索学号"
            :remote-method="searchStudents"
            :loading="studentLoading"
            style="width: 180px"
            @change="onStudentChange"
          >
            <el-option v-for="s in studentOptions" :key="s.studentNo" :label="s.studentNo" :value="s.studentNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input :model-value="entryForm.studentName" disabled placeholder="自动填充" style="width: 140px" />
        </el-form-item>
        <el-form-item label="课程" prop="courseNo">
          <el-select
            v-model="entryForm.courseNo"
            filterable
            placeholder="搜索课程"
            style="width: 260px"
            @change="onCourseChange"
          >
            <el-option v-for="c in courseOptions" :key="c.courseNo" :label="c.display" :value="c.courseNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input-number v-model="entryForm.score" :min="0" :max="100" style="width: 120px" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="entryForm.semester" placeholder="请选择学期" clearable style="width: 130px">
            <el-option label="大一第一学期" value="大一第一学期" />
            <el-option label="大一第二学期" value="大一第二学期" />
            <el-option label="大二第一学期" value="大二第一学期" />
            <el-option label="大二第二学期" value="大二第二学期" />
            <el-option label="大三第一学期" value="大三第一学期" />
            <el-option label="大三第二学期" value="大三第二学期" />
            <el-option label="大四第一学期" value="大四第一学期" />
            <el-option label="大四第二学期" value="大四第二学期" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleEntrySubmit" :loading="entrySubmitting">
            <el-icon><Plus /></el-icon> 录入
          </el-button>
          <el-button @click="resetEntryForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 查询卡片 -->
    <el-card class="toolbar-card">
      <template #header>
        <span style="font-weight: bold">🔍 查询成绩</span>
      </template>
      <el-row :gutter="20" align="middle">
        <el-col :span="6">
          <el-input v-model="queryStudentNo" placeholder="按学号查询" clearable style="width: 100%" />
        </el-col>
        <el-col :span="6">
          <el-input v-model="queryCourseNo" placeholder="按课程编号查询" clearable style="width: 100%" />
        </el-col>
        <el-col :span="6">
          <el-input v-model="queryKeyword" placeholder="按姓名/课程名搜索" clearable style="width: 100%" />
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 成绩列表 -->
    <el-card class="table-card">
      <el-table :data="list" stripe border v-loading="loading" empty-text="暂无成绩数据" style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="courseNo" label="课程编号" width="100" />
        <el-table-column prop="courseName" label="课程名称" min-width="100" width="160" show-overflow-tooltip />
        <el-table-column prop="score" label="成绩" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="scoreTagType(row.score)" effect="plain" size="large">{{ row.score }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="150" />
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

    <!-- 编辑弹窗 -->
    <el-dialog v-model="editVisible" title="编辑成绩" width="520px" :close-on-click-modal="false" destroy-on-close>
      <el-form ref="editFormRef" :model="editForm" :rules="entryRules" label-width="100px">
        <el-form-item label="学号" prop="studentNo">
          <el-select v-model="editForm.studentNo" filterable style="width: 100%" @change="onEditStudentChange">
            <el-option v-for="s in studentOptions" :key="s.studentNo" :label="s.studentNo" :value="s.studentNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input :model-value="editForm.studentName" disabled placeholder="自动填充" />
        </el-form-item>
        <el-form-item label="课程" prop="courseNo">
          <el-select v-model="editForm.courseNo" filterable style="width: 100%" @change="onEditCourseChange">
            <el-option v-for="c in courseOptions" :key="c.courseNo" :label="c.display" :value="c.courseNo" />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input-number v-model="editForm.score" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="editForm.semester" placeholder="请选择学期" clearable style="width: 100%">
            <el-option label="大一第一学期" value="大一第一学期" />
            <el-option label="大一第二学期" value="大一第二学期" />
            <el-option label="大二第一学期" value="大二第一学期" />
            <el-option label="大二第二学期" value="大二第二学期" />
            <el-option label="大三第一学期" value="大三第一学期" />
            <el-option label="大三第二学期" value="大三第二学期" />
            <el-option label="大四第一学期" value="大四第一学期" />
            <el-option label="大四第二学期" value="大四第二学期" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEditSubmit" :loading="submitting">更新</el-button>
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
const entrySubmitting = ref(false);
const submitting = ref(false);
const entryFormRef = ref(null);
const editFormRef = ref(null);
const editVisible = ref(false);

const entryForm = reactive({
  studentNo: "",
  studentName: "",
  courseNo: "",
  courseName: "",
  score: null,
  semester: "",
});

const editForm = reactive({
  id: null,
  studentNo: "",
  studentName: "",
  courseNo: "",
  courseName: "",
  score: null,
  semester: "",
});

const queryStudentNo = ref("");
const queryCourseNo = ref("");
const queryKeyword = ref("");

// 下拉列表数据
const allStudents = ref([]);
const studentOptions = ref([]);
const courseOptions = ref([]);
const studentLoading = ref(false);

const entryRules = {
  studentNo: [{ required: true, message: "请选择学号", trigger: "change" }],
  courseNo: [{ required: true, message: "请选择课程", trigger: "change" }],
  score: [{ required: true, message: "请输入成绩", trigger: "blur" }],
};

const scoreTagType = (score) => {
  if (score >= 90) return "success";
  if (score >= 80) return "primary";
  if (score >= 70) return "warning";
  if (score >= 60) return "info";
  return "danger";
};

const loadStudents = async () => {
  try {
    const res = await api.post("/students/list", { page: 1, pageSize: 9999 });
    allStudents.value = res.data.records;
    studentOptions.value = res.data.records;
  } catch { ElMessage.error("加载学生列表失败") }
};

const loadCourses = async () => {
  try {
    const res = await api.post("/course/list", { page: 1, pageSize: 9999 });
    courseOptions.value = res.data.records.map(c => ({
      ...c,
      display: `${c.courseNo} - ${c.courseName}`,
    }));
  } catch { ElMessage.error("加载课程列表失败") }
};

const searchStudents = (query) => {
  if (!query.trim()) {
    studentOptions.value = allStudents.value;
    return;
  }
  studentLoading.value = true;
  api.post("/students/search", { keyword: query.trim(), page: 1, pageSize: 9999 }).then(res => {
    studentOptions.value = res.data.records;
    studentLoading.value = false;
  }).catch(() => { studentLoading.value = false; });
};

const onStudentChange = (val) => {
  const stu = allStudents.value.find(s => s.studentNo === val);
  entryForm.studentName = stu ? stu.name : "";
};

const onCourseChange = (val) => {
  const cou = courseOptions.value.find(c => c.courseNo === val);
  entryForm.courseName = cou ? cou.courseName : "";
};

const onEditStudentChange = (val) => {
  const stu = allStudents.value.find(s => s.studentNo === val);
  editForm.studentName = stu ? stu.name : "";
};

const onEditCourseChange = (val) => {
  const cou = courseOptions.value.find(c => c.courseNo === val);
  editForm.courseName = cou ? cou.courseName : "";
};
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const loadList = async () => {
  loading.value = true;
  try {
    const res = await api.post("/score/list", { page: currentPage.value, pageSize: pageSize.value });
    list.value = res.data.records;
    total.value = res.data.total;
  } catch { ElMessage.error("加载成绩列表失败") }
  finally { loading.value = false }
};

const handleEntrySubmit = async () => {
  const valid = await entryFormRef.value.validate().catch(() => false);
  if (!valid) return;
  entrySubmitting.value = true;
  try {
    await api.post("/score/create", entryForm);
    ElMessage.success("录入成功");
    resetEntryForm();
    loadList();
  } catch (err) { ElMessage.error(err.userMessage || "录入失败") }
  finally { entrySubmitting.value = false }
};

const resetEntryForm = () => {
  Object.assign(entryForm, { studentNo: "", studentName: "", courseNo: "", courseName: "", score: null, semester: "" });
};

const handleQuery = async () => {
  if (queryStudentNo.value.trim()) {
    loading.value = true;
    try {
      const res = await api.post("/score/query", { studentNo: queryStudentNo.value.trim(), page: currentPage.value, pageSize: pageSize.value });
      list.value = res.data.records;
      total.value = res.data.total;
    } catch { ElMessage.error("查询失败") }
    finally { loading.value = false }
    return;
  }
  if (queryCourseNo.value.trim()) {
    loading.value = true;
    try {
      const res = await api.post("/score/query", { courseNo: queryCourseNo.value.trim(), page: currentPage.value, pageSize: pageSize.value });
      list.value = res.data.records;
      total.value = res.data.total;
    } catch { ElMessage.error("查询失败") }
    finally { loading.value = false }
    return;
  }
  if (queryKeyword.value.trim()) {
    loading.value = true;
    try {
      const res = await api.post("/score/searchMultiple", { studentName: queryKeyword.value.trim(), courseName: queryKeyword.value.trim(), page: currentPage.value, pageSize: pageSize.value });
      list.value = res.data.records;
      total.value = res.data.total;
    } catch { ElMessage.error("搜索失败") }
    finally { loading.value = false }
    return;
  }
  loadList();
};

const resetQuery = () => {
  queryStudentNo.value = "";
  queryCourseNo.value = "";
  queryKeyword.value = "";
  currentPage.value = 1;
  loadList();
};

const openEditDialog = (row) => {
  Object.assign(editForm, {
    id: row.id,
    studentNo: row.studentNo,
    studentName: row.studentName,
    courseNo: row.courseNo,
    courseName: row.courseName,
    score: row.score,
    semester: row.semester || "",
  });
  editVisible.value = true;
};

const handleEditSubmit = async () => {
  const valid = await editFormRef.value.validate().catch(() => false);
  if (!valid) return;
  submitting.value = true;
  try {
    await api.post("/score/update", editForm);
    ElMessage.success("更新成功");
    editVisible.value = false;
    loadList();
  } catch (err) { ElMessage.error(err.userMessage || "更新失败") }
  finally { submitting.value = false }
};

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除「${row.studentName}」的「${row.courseName}」成绩吗？`, "确认删除", { type: "warning" })
    .then(async () => {
      try {
        await api.post("/score/delete", { id: row.id });
        ElMessage.success("删除成功");
        loadList();
      } catch (err) { ElMessage.error(err.userMessage || "删除失败") }
    }).catch(() => {});
};

onMounted(() => {
  loadList();
  loadStudents();
  loadCourses();
});
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.toolbar-card, .table-card { border-radius: 8px; }
.toolbar-card :deep(.el-card__header) { padding: 12px 20px; }
</style>
