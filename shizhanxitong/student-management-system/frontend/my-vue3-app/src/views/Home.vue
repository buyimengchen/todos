<template>
  <div class="layout-container">
    <!-- 固定左侧导航栏 -->
    <div class="sidebar" :class="{ 'collapsed': collapsed }">
      <div class="sidebar-header">
        <el-button @click="collapsed = !collapsed" icon="Menu" circle size="small" />
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        @select="handleSelect"
        :collapse="collapsed"
        background-color="#2c3e50"
        text-color="#ecf0f1"
        active-text-color="#3498db"
      >
        <el-menu-item index="home">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-menu-item index="class">
          <el-icon><Collection /></el-icon>
          <template #title>班级</template>
        </el-menu-item>
        <el-menu-item index="course">
          <el-icon><Notebook /></el-icon>
          <template #title>课程</template>
        </el-menu-item>
        <el-menu-item index="score">
          <el-icon><DataAnalysis /></el-icon>
          <template #title>成绩</template>
        </el-menu-item>
        <el-menu-item index="student">
          <el-icon><User /></el-icon>
          <template #title>学生</template>
        </el-menu-item>
        <el-menu-item index="user">
          <el-icon><Avatar /></el-icon>
          <template #title>用户</template>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content" :class="{ 'collapsed': collapsed }">
      <div class="container">
        <div class="welcome-content">
          欢迎进入学生管理系统
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMenu, ElMenuItem } from 'element-plus'
import { Menu, House, Collection, Notebook, DataAnalysis, User, Avatar } from '@element-plus/icons-vue'

const router = useRouter()
const collapsed = ref(false)
const activeMenu = ref('home')

const handleSelect = (key) => {
  if (key === 'student') {
    router.push('/student')
  } else if (key === 'home') {
    router.push('/')
  } else if (key === 'user') {
    router.push('/user')
  } else {
    router.push('/') // 其余暂时跳转首页
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  width: 200px;
  height: 100vh;
  background-color: #2c3e50;
  transition: width 0.2s;
  z-index: 1000;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 48px;
  padding: 0 16px;
  border-bottom: 1px solid #34495e;
}

.sidebar-menu {
  border-right: none;
  height: calc(100vh - 48px);
}

.main-content {
  flex: 1;
  margin-left: 200px;
  transition: margin-left 0.2s;
  background-color: #ffffff;
  overflow-y: auto;
  height: 100vh;
  width: calc(100vw - 200px);
}

.main-content.collapsed {
  margin-left: 64px;
}

.container {
  padding: 20px;
  height: 100vh;
  width: 100%;
  box-sizing: border-box;
  background-color: #ffffff;
  min-height: calc(100vh - 200px);
}

.welcome-content {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  font-size: 2rem;
  color: #409EFF;
  font-weight: 500;
}
</style>

<style scoped>
.el-aside {
  box-shadow: -2px 0 8px #f0f1f2;
}
</style>
