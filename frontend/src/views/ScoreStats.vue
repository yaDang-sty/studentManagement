<template>
  <div class="page">
    <!-- ===== 第一部分：各年级各班各科平均成绩 ===== -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span style="font-weight: bold">📳 各年级各班各科平均成绩</span>
          <div style="display: flex; gap: 12px; align-items: center">
            <el-select v-model="selectedGrade" placeholder="请选择年级" style="width: 140px" @change="loadAvgData">
              <el-option label="大一" value="大一" />
              <el-option label="大二" value="大二" />
              <el-option label="大三" value="大三" />
              <el-option label="大四" value="大四" />
            </el-select>
            <el-button size="small" @click="loadAvgData" :loading="loading">刷新</el-button>
          </div>
        </div>
      </template>
      <div v-if="avgNoData" style="text-align: center; color: #999; padding: 60px 0">
        暂无该年级的成绩数据，请先选择年级或录入成绩
      </div>
      <div v-else ref="avgChartRef" style="width: 100%; height: 500px"></div>
    </el-card>

    <!-- ===== 第二部分：查询学生各科成绩 ===== -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span style="font-weight: bold">🔍 学生个人成绩查询</span>
          <div style="display: flex; gap: 12px; align-items: center; flex-wrap: wrap">
            <el-input
              v-model="studentQuery"
              placeholder="输入学号或姓名"
              clearable
              style="width: 180px"
              @keyup.enter="loadStudentScores"
            />
            <el-select v-model="querySemester" placeholder="全部学期" clearable style="width: 150px">
              <el-option label="大一第一学期" value="大一第一学期" />
              <el-option label="大一第二学期" value="大一第二学期" />
              <el-option label="大二第一学期" value="大二第一学期" />
              <el-option label="大二第二学期" value="大二第二学期" />
              <el-option label="大三第一学期" value="大三第一学期" />
              <el-option label="大三第二学期" value="大三第二学期" />
              <el-option label="大四第一学期" value="大四第一学期" />
              <el-option label="大四第二学期" value="大四第二学期" />
            </el-select>
            <el-button type="primary" size="small" @click="loadStudentScores" :loading="studentLoading">
              <el-icon><Search /></el-icon> 查询
            </el-button>
          </div>
        </div>
      </template>
      <div v-if="studentNoData" style="text-align: center; color: #999; padding: 60px 0">
        请输入学号或姓名查询学生成绩
      </div>
      <div v-else-if="studentEmpty" style="text-align: center; color: #e6a23c; padding: 60px 0">
        未找到该学生的成绩记录
      </div>
      <div v-else ref="studentChartRef" style="width: 100%; height: 400px"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { ElMessage } from "element-plus";
import { Search } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import api from "../api";

// ===== 第一部分：平均成绩图表 =====
const avgChartRef = ref(null);
const loading = ref(false);
const avgNoData = ref(false);
const selectedGrade = ref("大一");
let avgChart = null;

// ===== 第二部分：学生个人成绩 =====
const studentChartRef = ref(null);
const studentLoading = ref(false);
const studentNoData = ref(true);
const studentEmpty = ref(false);
const studentQuery = ref("");
const querySemester = ref("");
let studentChart = null;

const colorList = ["#409eff", "#67c23a", "#e6a23c", "#f56c6c", "#9b59b6", "#1abc9c", "#e74c3c", "#3498db"];

// ----- 平均成绩图表 -----
const loadAvgData = async () => {
  if (!selectedGrade.value) return;
  loading.value = true;
  try {
    const res = await api.post("/stats/avg-by-class", { grade: selectedGrade.value });
    const data = res.data;
    if (!data || data.length === 0) {
      avgNoData.value = true;
      return;
    }
    avgNoData.value = false;

    const classes = [...new Set(data.map(d => d.className))];
    const courses = [...new Set(data.map(d => d.courseName))];

    const series = courses.map((course, idx) => ({
      name: course,
      type: "bar",
      barWidth: 28,
      itemStyle: { color: colorList[idx % colorList.length] },
      data: classes.map(cls => {
        const item = data.find(d => d.className === cls && d.courseName === course);
        return item ? Math.round(item.avgScore * 10) / 10 : 0;
      }),
    }));

    await nextTick();
    if (!avgChartRef.value) return;

    if (avgChart) avgChart.dispose();
    avgChart = echarts.init(avgChartRef.value);
    avgChart.setOption({
      title: {
        text: `${selectedGrade.value}各班各科平均成绩`,
        left: "center",
        top: 0,
        textStyle: { fontSize: 16 },
      },
      tooltip: {
        trigger: "axis",
        axisPointer: { type: "shadow" },
        formatter: params => {
          let html = `<b>${params[0].axisValue}</b><br/>`;
          params.forEach(p => { html += `${p.marker} ${p.seriesName}：<b>${p.value}分</b><br/>`; });
          return html;
        },
      },
      legend: { data: courses, top: 32 },
      grid: { left: 60, right: 30, top: 80, bottom: 40 },
      xAxis: { type: "category", data: classes, axisLabel: { fontSize: 14, fontWeight: "bold" } },
      yAxis: { type: "value", name: "平均分", min: 0, max: 100, axisLabel: { formatter: "{value}分" } },
      series,
    });
  } catch (e) {
    console.error("avg stats error:", e);
    ElMessage.error("加载统计数据失败");
  } finally {
    loading.value = false;
  }
};

// ----- 学生个人成绩查询 -----
const loadStudentScores = async () => {
  const query = studentQuery.value.trim();
  if (!query) {
    studentNoData.value = true;
    studentEmpty.value = false;
    if (studentChart) { studentChart.dispose(); studentChart = null; }
    return;
  }
  studentLoading.value = true;
  studentNoData.value = false;
  try {
    const res = await api.post("/stats/student-scores", {
      query: query,
      semester: querySemester.value || "",
    });
    const data = res.data;
    if (!data || data.length === 0) {
      studentEmpty.value = true;
      if (studentChart) { studentChart.dispose(); studentChart = null; }
      return;
    }
    studentEmpty.value = false;

    const courseNames = data.map(d => d.courseName);
    const scores = data.map(d => d.score);

    await nextTick();
    if (!studentChartRef.value) return;

    if (studentChart) studentChart.dispose();
    studentChart = echarts.init(studentChartRef.value);
    studentChart.setOption({
      title: {
        text: `${data[0].studentName}（${data[0].studentNo}）各科成绩`,
        left: "center",
        top: 0,
        textStyle: { fontSize: 16 },
      },
      tooltip: {
        trigger: "axis",
        axisPointer: { type: "shadow" },
        formatter: params => {
          const p = params[0];
          return `${p.name}<br/>${p.marker} 成绩：<b>${p.value}分</b>`;
        },
      },
      grid: { left: 60, right: 30, top: 50, bottom: 40 },
      xAxis: {
        type: "category",
        data: courseNames,
        axisLabel: { fontSize: 13, rotate: courseNames.length > 4 ? 20 : 0 },
      },
      yAxis: { type: "value", name: "分数", min: 0, max: 100, axisLabel: { formatter: "{value}分" } },
      series: [{
        name: "成绩",
        type: "bar",
        barWidth: 36,
        data: scores.map((score, idx) => ({
          value: score,
          itemStyle: {
            color: score >= 90 ? "#67c23a" : score >= 80 ? "#409eff" : score >= 70 ? "#e6a23c" : score >= 60 ? "#f56c6c" : "#c0c4cc",
          },
        })),
        label: {
          show: true,
          position: "top",
          formatter: "{c}分",
          fontSize: 13,
          fontWeight: "bold",
        },
      }],
    });
  } catch (e) {
    console.error("student scores error:", e);
    ElMessage.error("查询学生成绩失败");
  } finally {
    studentLoading.value = false;
  }
};

const handleResize = () => {
  if (avgChart) avgChart.resize();
  if (studentChart) studentChart.resize();
};

onMounted(() => {
  loadAvgData();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
  if (avgChart) { avgChart.dispose(); avgChart = null; }
  if (studentChart) { studentChart.dispose(); studentChart = null; }
});
</script>

<style scoped>
.page { display: flex; flex-direction: column; gap: 16px; }
.chart-card { border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px; }
</style>