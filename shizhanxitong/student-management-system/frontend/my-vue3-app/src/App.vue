<template>
  <router-view />
</template>

<style>
body {
  margin: 0;
  padding: 0;
  background: #f5f6fa;
  font-family: Arial, sans-serif;
}

/* 确保所有页面的侧边栏样式一致 */
.el-aside {
  height: 100vh;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.12);
  z-index: 10;
}

/* 确保主内容区域占满剩余空间 */
.el-main {
  flex: 1;
  padding: 20px;
}

/* 确保表格内容可以完整显示 */
.el-table {
  width: 100% !important;
}
</style>




