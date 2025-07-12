# Vue3 + SpringBoot 学生管理系统完整指南

## 项目概述

本项目是一个基于现代Web技术栈的学生管理系统，采用前后端分离架构：
- **前端**: Vue3 + Element Plus + Axios
- **后端**: SpringBoot + MyBatis Plus + MySQL
- **认证**: JWT Token
- **权限**: RBAC（基于角色的访问控制）

## 技术架构图

```
┌─────────────────┐    HTTP/HTTPS     ┌─────────────────┐    JDBC/SQL    ┌─────────────────┐
│   Vue3 前端      │ ←──────────────→ │  SpringBoot后端  │ ←──────────→ │   MySQL数据库    │
│                 │    JSON数据交换    │                 │   数据持久化    │                 │
│ • Vue Router    │                   │ • Spring MVC    │              │ • 用户表         │
│ • Vuex/Pinia    │                   │ • Spring Security│              │ • 学生表         │
│ • Element Plus  │                   │ • MyBatis Plus  │              │ • 班级表         │
│ • Axios         │                   │ • JWT Utils     │              │ • 权限表         │
└─────────────────┘                   └─────────────────┘              └─────────────────┘
```

## 项目目录结构

```
student-management-system/
├── frontend/                          # Vue3前端项目
│   ├── public/                       # 静态资源
│   │   ├── index.html               # 主HTML文件
│   │   └── favicon.ico              # 网站图标
│   ├── src/                         # 源代码目录
│   │   ├── main.js                  # 应用入口文件
│   │   ├── App.vue                  # 根组件
│   │   ├── router/                  # 路由配置
│   │   │   └── index.js            # 路由定义
│   │   ├── store/                   # 状态管理
│   │   │   └── index.js            # Vuex/Pinia配置
│   │   ├── views/                   # 页面组件
│   │   │   ├── Login.vue           # 登录页面
│   │   │   ├── Dashboard.vue       # 仪表板
│   │   │   ├── StudentList.vue     # 学生列表
│   │   │   └── UserManagement.vue  # 用户管理
│   │   ├── components/              # 通用组件
│   │   │   ├── Header.vue          # 头部组件
│   │   │   ├── Sidebar.vue         # 侧边栏
│   │   │   └── StudentForm.vue     # 学生表单
│   │   ├── api/                     # API接口
│   │   │   ├── request.js          # Axios配置
│   │   │   ├── auth.js             # 认证接口
│   │   │   └── student.js          # 学生接口
│   │   ├── utils/                   # 工具函数
│   │   │   ├── auth.js             # 认证工具
│   │   │   └── permission.js       # 权限工具
│   │   └── assets/                  # 资源文件
│   │       ├── styles/             # 样式文件
│   │       └── images/             # 图片资源
│   ├── package.json                 # 依赖配置
│   └── vite.config.js              # Vite配置
├── backend/                         # SpringBoot后端项目
│   ├── src/main/java/              # Java源代码
│   │   └── com/example/sms/        # 包路径
│   │       ├── SmsApplication.java # 启动类
│   │       ├── config/             # 配置类
│   │       │   ├── SecurityConfig.java    # 安全配置
│   │       │   ├── CorsConfig.java        # 跨域配置
│   │       │   └── MyBatisPlusConfig.java # MyBatis配置
│   │       ├── controller/         # 控制器层
│   │       │   ├── AuthController.java    # 认证控制器
│   │       │   ├── StudentController.java # 学生控制器
│   │       │   └── UserController.java    # 用户控制器
│   │       ├── service/            # 服务层
│   │       │   ├── AuthService.java       # 认证服务
│   │       │   ├── StudentService.java    # 学生服务
│   │       │   └── UserService.java       # 用户服务
│   │       ├── mapper/             # 数据访问层
│   │       │   ├── UserMapper.java        # 用户Mapper
│   │       │   └── StudentMapper.java     # 学生Mapper
│   │       ├── entity/             # 实体类
│   │       │   ├── User.java              # 用户实体
│   │       │   ├── Student.java           # 学生实体
│   │       │   └── Role.java              # 角色实体
│   │       ├── dto/                # 数据传输对象
│   │       │   ├── LoginRequest.java      # 登录请求
│   │       │   └── StudentRequest.java    # 学生请求
│   │       ├── vo/                 # 视图对象
│   │       │   ├── LoginResponse.java     # 登录响应
│   │       │   └── StudentResponse.java   # 学生响应
│   │       └── utils/              # 工具类
│   │           ├── JwtUtils.java          # JWT工具
│   │           └── ResponseUtils.java     # 响应工具
│   ├── src/main/resources/         # 资源文件
│   │   ├── application.yml         # 应用配置
│   │   ├── mapper/                 # MyBatis映射文件
│   │   └── db/                     # 数据库脚本
│   │       ├── schema.sql          # 表结构
│   │       └── data.sql            # 初始数据
│   └── pom.xml                     # Maven配置
└── README.md                       # 项目说明
```

## 核心实现原理

### 1. 前端Vue3实现

#### 1.1 项目入口 (main.js)
```javascript
// main.js - Vue3应用入口文件
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 创建Vue应用实例
const app = createApp(App)

// 使用插件和配置
app.use(store)      // 状态管理
app.use(router)     // 路由管理
app.use(ElementPlus) // UI组件库

// 挂载应用到DOM
app.mount('#app')
```

#### 1.2 路由配置 (router/index.js)
```javascript
// router/index.js - 路由配置文件
import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

// 路由定义 - 定义页面路径和对应组件
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false } // 不需要认证
  },
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { requiresAuth: true }  // 需要认证
  },
  {
    path: '/students',
    name: 'StudentList',
    component: () => import('@/views/StudentList.vue'),
    meta: { requiresAuth: true, roles: ['admin', 'teacher'] } // 需要特定角色
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 在每次路由跳转前检查权限
router.beforeEach((to, from, next) => {
  const token = getToken() // 获取用户token
  
  if (to.meta.requiresAuth) {
    // 需要认证的页面
    if (token) {
      // 已登录，检查角色权限
      if (to.meta.roles) {
        const userRole = store.getters.userRole
        if (to.meta.roles.includes(userRole)) {
          next() // 有权限，允许访问
        } else {
          next('/403') // 无权限，跳转到403页面
        }
      } else {
        next() // 不需要特定角色，允许访问
      }
    } else {
      next('/login') // 未登录，跳转到登录页
    }
  } else {
    next() // 不需要认证，直接访问
  }
})

export default router
```

#### 1.3 API请求封装 (api/request.js)
```javascript
// api/request.js - Axios请求封装
import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端API基础地址
  timeout: 10000 // 请求超时时间
})

// 请求拦截器 - 在发送请求前添加token
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      // 在请求头中添加Authorization字段
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理响应和错误
request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data
    
    if (code === 200) {
      return data // 请求成功，返回数据
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  error => {
    if (error.response?.status === 401) {
      // token过期或无效
      ElMessage.error('登录已过期，请重新登录')
      removeToken()
      router.push('/login')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request

#### 1.4 学生列表页面 (views/StudentList.vue)
```vue
<!-- StudentList.vue - 学生列表页面组件 -->
<template>
  <div class="student-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>学生管理</h2>
      <el-button
        v-if="hasPermission('student:create')"
        type="primary"
        @click="showAddDialog = true"
      >
        添加学生
      </el-button>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入学生姓名"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="searchForm.className" placeholder="请选择班级">
            <el-option label="全部" value="" />
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.name"
              :value="cls.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 学生数据表格 -->
    <el-table
      :data="studentList"
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="studentId" label="学号" width="150" />
      <el-table-column prop="className" label="班级" width="120" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />

      <!-- 根据权限显示敏感信息 -->
      <el-table-column
        v-if="hasPermission('student:view:sensitive')"
        prop="address"
        label="地址"
        width="200"
      />
      <el-table-column
        v-if="hasPermission('student:view:sensitive')"
        prop="idCard"
        label="身份证"
        width="180"
      />

      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            v-if="hasPermission('student:edit')"
            size="small"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            v-if="hasPermission('student:delete')"
            size="small"
            type="danger"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentList, deleteStudent } from '@/api/student'
import { hasPermission } from '@/utils/permission'

// 响应式数据定义
const loading = ref(false)
const studentList = ref([])
const classList = ref([])
const showAddDialog = ref(false)

// 搜索表单数据
const searchForm = reactive({
  name: '',
  className: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取学生列表数据
const fetchStudentList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.current,
      size: pagination.size
    }

    const result = await getStudentList(params)
    studentList.value = result.records
    pagination.total = result.total
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  fetchStudentList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, { name: '', className: '' })
  handleSearch()
}

// 编辑学生
const handleEdit = (row) => {
  // 编辑逻辑
}

// 删除学生
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除学生 ${row.name} 吗？`,
      '确认删除',
      { type: 'warning' }
    )

    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    fetchStudentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  fetchStudentList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchStudentList()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchStudentList()
})
</script>

<style scoped>
.student-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-area {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}
</style>
```

### 2. SpringBoot后端实现

#### 2.1 项目启动类 (UniversityBackendApplication.java)
```java
// UniversityBackendApplication.java - SpringBoot应用启动类
package com.example.university_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// SpringBoot主启动类
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // 排除默认安全配置
@MapperScan("com.example.university_backend.mapper") // 扫描Mapper接口
public class UniversityBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(UniversityBackendApplication.class, args);
    }
}
```

#### 2.2 学生实体类 (entity/Student.java)
```java
// Student.java - 学生实体类
package com.example.university_backend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data // Lombok注解，自动生成getter/setter/toString等方法
@TableName("students") // 指定数据库表名
public class Student {

    @TableId(type = IdType.AUTO) // 主键自增
    private Long id;

    private String name;        // 学生姓名
    private String studentId;   // 学号
    private String className;   // 班级名称
    private String phone;       // 电话号码
    private String email;       // 邮箱地址
    private String address;     // 家庭住址（敏感信息）
    private String idCard;      // 身份证号（敏感信息）
    private String gender;      // 性别
    private LocalDateTime birthDate;      // 出生日期
    private LocalDateTime enrollmentDate; // 入学日期
    private String status;      // 学生状态：active/graduated/suspended
    private LocalDateTime createdAt;      // 创建时间
    private LocalDateTime updatedAt;      // 更新时间

    // 根据用户角色过滤敏感信息的方法
    public Student filterSensitiveInfo(String userRole) {
        if (!"admin".equals(userRole)) {
            this.address = null;  // 非管理员不能看到地址
            this.idCard = null;   // 非管理员不能看到身份证
        }
        return this;
    }
}
```

#### 2.3 学生控制器 (controller/StudentController.java)
```java
// StudentController.java - 学生管理控制器
package com.example.university_backend.controller;

import com.example.university_backend.entity.Student;
import com.example.university_backend.service.StudentService;
import com.example.university_backend.util.JwtUtils;
import com.example.university_backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController // 标识为REST控制器，返回JSON数据
@RequestMapping("/api/students") // 请求路径前缀
@CrossOrigin(origins = "http://localhost:5173") // 允许前端跨域访问
public class StudentController {

    @Autowired
    private StudentService studentService; // 注入学生服务

    @Autowired
    private JwtUtils jwtUtils; // 注入JWT工具类

    /**
     * 获取学生列表 - 对应前端的getStudentList()调用
     * 根据用户角色返回不同权限的数据
     */
    @GetMapping
    public Result<List<Student>> getStudentList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String className,
            HttpServletRequest request) {

        try {
            // 从请求头获取JWT token
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // 移除"Bearer "前缀
            }

            // 解析token获取用户信息
            Map<String, Object> userInfo = jwtUtils.parseToken(token);
            String userRole = (String) userInfo.get("role");
            Long userId = (Long) userInfo.get("userId");

            // 根据用户角色获取相应的学生数据
            List<Student> students;
            if ("admin".equals(userRole)) {
                // 管理员可以看到所有学生的完整信息
                students = studentService.getAllStudents(page, size, name, className);
            } else if ("teacher".equals(userRole)) {
                // 教师可以看到所有学生但不包括敏感信息
                students = studentService.getStudentsForTeacher(page, size, name, className);
            } else {
                // 学生只能看到同班同学的基本信息和自己的完整信息
                students = studentService.getStudentsForStudent(userId, page, size, name, className);
            }

            return Result.success(students);

        } catch (Exception e) {
            return Result.error("获取学生列表失败: " + e.getMessage());
        }
    }

    /**
     * 创建学生 - 对应前端的createStudent()调用
     * 只有管理员可以创建学生记录
     */
    @PostMapping
    public Result<Student> createStudent(@RequestBody Student student, HttpServletRequest request) {
        try {
            // 验证用户权限
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Map<String, Object> userInfo = jwtUtils.parseToken(token);
            String userRole = (String) userInfo.get("role");

            // 只有管理员可以创建学生
            if (!"admin".equals(userRole)) {
                return Result.error("权限不足，只有管理员可以创建学生");
            }

            // 调用服务层创建学生
            Student createdStudent = studentService.createStudent(student);
            return Result.success(createdStudent);

        } catch (Exception e) {
            return Result.error("创建学生失败: " + e.getMessage());
        }
    }

    /**
     * 更新学生信息
     */
    @PutMapping("/{id}")
    public Result<Student> updateStudent(@PathVariable Long id, @RequestBody Student student, HttpServletRequest request) {
        try {
            // 验证权限和更新学生信息
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Map<String, Object> userInfo = jwtUtils.parseToken(token);
            String userRole = (String) userInfo.get("role");
            Long userId = (Long) userInfo.get("userId");

            // 权限检查：管理员可以编辑所有学生，教师可以编辑部分字段，学生只能编辑自己
            boolean canEdit = false;
            if ("admin".equals(userRole)) {
                canEdit = true;
            } else if ("teacher".equals(userRole)) {
                canEdit = true;
                // 教师不能编辑敏感信息
                student.setAddress(null);
                student.setIdCard(null);
            } else if ("student".equals(userRole)) {
                // 学生只能编辑自己的信息
                Student existingStudent = studentService.getStudentById(id);
                if (existingStudent != null && existingStudent.getId().equals(userId)) {
                    canEdit = true;
                }
            }

            if (!canEdit) {
                return Result.error("权限不足");
            }

            student.setId(id);
            Student updatedStudent = studentService.updateStudent(student);
            return Result.success(updatedStudent);

        } catch (Exception e) {
            return Result.error("更新学生信息失败: " + e.getMessage());
        }
    }

    /**
     * 删除学生 - 只有管理员可以删除
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteStudent(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 验证管理员权限
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Map<String, Object> userInfo = jwtUtils.parseToken(token);
            String userRole = (String) userInfo.get("role");

            if (!"admin".equals(userRole)) {
                return Result.error("权限不足，只有管理员可以删除学生");
            }

            studentService.deleteStudent(id);
            return Result.success(null);

        } catch (Exception e) {
            return Result.error("删除学生失败: " + e.getMessage());
        }
    }
}
```

#### 2.4 学生服务层 (service/StudentService.java)
```java
// StudentService.java - 学生业务逻辑服务
package com.example.university_backend.service;

import com.example.university_backend.entity.Student;
import com.example.university_backend.mapper.StudentMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service // 标识为服务层组件
public class StudentService {

    @Autowired
    private StudentMapper studentMapper; // 注入数据访问层

    /**
     * 获取所有学生信息 - 管理员权限
     * 返回完整的学生信息，包括敏感数据
     */
    public List<Student> getAllStudents(int page, int size, String name, String className) {
        // 构建分页对象
        Page<Student> pageObj = new Page<>(page, size);

        // 构建查询条件
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("name", name); // 模糊查询姓名
        }
        if (className != null && !className.trim().isEmpty()) {
            queryWrapper.eq("class_name", className); // 精确查询班级
        }
        queryWrapper.orderByDesc("created_at"); // 按创建时间降序排列

        // 执行分页查询
        Page<Student> result = studentMapper.selectPage(pageObj, queryWrapper);
        return result.getRecords();
    }

    /**
     * 获取教师可见的学生信息 - 教师权限
     * 不包含敏感信息如身份证号和详细地址
     */
    public List<Student> getStudentsForTeacher(int page, int size, String name, String className) {
        List<Student> students = getAllStudents(page, size, name, className);

        // 过滤敏感信息
        return students.stream()
                .map(student -> student.filterSensitiveInfo("teacher"))
                .toList();
    }

    /**
     * 获取学生可见的信息 - 学生权限
     * 同班同学只显示基本信息，自己显示完整信息
     */
    public List<Student> getStudentsForStudent(Long userId, int page, int size, String name, String className) {
        // 首先获取当前学生的班级信息
        Student currentStudent = studentMapper.selectById(userId);
        if (currentStudent == null) {
            throw new RuntimeException("学生信息不存在");
        }

        String userClassName = currentStudent.getClassName();

        // 构建查询条件：只查询同班同学
        Page<Student> pageObj = new Page<>(page, size);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_name", userClassName); // 限制为同班同学

        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like("name", name);
        }
        if (className != null && !className.trim().isEmpty()) {
            queryWrapper.eq("class_name", className);
        }

        Page<Student> result = studentMapper.selectPage(pageObj, queryWrapper);
        List<Student> students = result.getRecords();

        // 处理权限：自己可以看到完整信息，同学只能看到基本信息
        return students.stream()
                .map(student -> {
                    if (student.getId().equals(userId)) {
                        return student; // 自己的完整信息
                    } else {
                        return student.filterSensitiveInfo("student"); // 同学的基本信息
                    }
                })
                .toList();
    }

    /**
     * 创建学生记录
     */
    public Student createStudent(Student student) {
        // 设置创建时间
        student.setCreatedAt(LocalDateTime.now());
        student.setUpdatedAt(LocalDateTime.now());

        // 检查学号是否已存在
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", student.getStudentId());
        Student existingStudent = studentMapper.selectOne(queryWrapper);

        if (existingStudent != null) {
            throw new RuntimeException("学号已存在");
        }

        // 插入数据库
        int result = studentMapper.insert(student);
        if (result > 0) {
            return student;
        } else {
            throw new RuntimeException("创建学生失败");
        }
    }

    /**
     * 更新学生信息
     */
    public Student updateStudent(Student student) {
        student.setUpdatedAt(LocalDateTime.now());

        int result = studentMapper.updateById(student);
        if (result > 0) {
            return studentMapper.selectById(student.getId());
        } else {
            throw new RuntimeException("更新学生信息失败");
        }
    }

    /**
     * 删除学生
     */
    public void deleteStudent(Long id) {
        int result = studentMapper.deleteById(id);
        if (result == 0) {
            throw new RuntimeException("删除学生失败，学生不存在");
        }
    }

    /**
     * 根据ID获取学生信息
     */
    public Student getStudentById(Long id) {
        return studentMapper.selectById(id);
    }
}
```

#### 2.5 数据访问层 (mapper/StudentMapper.java)
```java
// StudentMapper.java - 学生数据访问接口
package com.example.university_backend.mapper;

import com.example.university_backend.entity.Student;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper // 标识为MyBatis映射器
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * BaseMapper已提供基本的CRUD操作：
     * - insert(entity): 插入记录
     * - deleteById(id): 根据ID删除
     * - updateById(entity): 根据ID更新
     * - selectById(id): 根据ID查询
     * - selectList(wrapper): 条件查询
     * - selectPage(page, wrapper): 分页查询
     */

    /**
     * 自定义查询：根据班级获取学生列表
     */
    @Select("SELECT * FROM students WHERE class_name = #{className} ORDER BY name")
    List<Student> selectByClassName(String className);

    /**
     * 自定义查询：获取学生统计信息
     */
    @Select("SELECT COUNT(*) FROM students WHERE status = 'active'")
    int countActiveStudents();

    /**
     * 自定义查询：根据学号查询学生
     */
    @Select("SELECT * FROM students WHERE student_id = #{studentId}")
    Student selectByStudentId(String studentId);
}
```

### 3. 前后端通信原理详解

#### 3.1 通信流程图
```
前端Vue3应用                    后端SpringBoot应用                 MySQL数据库
┌─────────────────┐           ┌─────────────────┐              ┌─────────────────┐
│  用户操作界面    │           │                 │              │                 │
│  ↓             │           │                 │              │                 │
│  Vue组件方法    │  HTTP请求  │  Controller     │   SQL查询    │  数据表          │
│  ↓             │ ────────→ │  ↓             │ ──────────→ │                 │
│  API调用        │  JSON数据  │  Service        │   结果返回    │                 │
│  ↓             │           │  ↓             │ ←────────── │                 │
│  Axios请求      │           │  Mapper         │              │                 │
│  ↓             │  HTTP响应  │  ↓             │              │                 │
│  响应处理       │ ←──────── │  数据库操作      │              │                 │
│  ↓             │  JSON数据  │                 │              │                 │
│  界面更新       │           │                 │              │                 │
└─────────────────┘           └─────────────────┘              └─────────────────┘
```

#### 3.2 具体通信示例

**步骤1: 前端发起请求**
```javascript
// 前端 - 用户点击"获取学生列表"按钮
const fetchStudentList = async () => {
  loading.value = true
  try {
    // 1. 构建请求参数
    const params = {
      page: pagination.current,    // 当前页码
      size: pagination.size,       // 每页大小
      name: searchForm.name,       // 搜索姓名
      className: searchForm.className // 搜索班级
    }

    // 2. 调用API接口（通过Axios发送HTTP请求）
    const result = await getStudentList(params)

    // 3. 处理返回结果
    studentList.value = result.records  // 更新学生列表
    pagination.total = result.total     // 更新总数

  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// API调用函数
export const getStudentList = (params) => {
  return request({
    url: '/students',      // 请求路径
    method: 'GET',         // 请求方法
    params: params         // 查询参数
  })
}
```

**步骤2: Axios拦截器处理**
```javascript
// 请求拦截器 - 自动添加认证token
request.interceptors.request.use(config => {
  const token = getToken()
  if (token) {
    // 在请求头中添加Authorization字段
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 实际发送的HTTP请求：
// GET http://localhost:8080/api/students?page=1&size=10&name=张三
// Headers: {
//   "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
//   "Content-Type": "application/json"
// }
```

**步骤3: 后端接收请求**
```java
// SpringBoot控制器接收请求
@GetMapping
public Result<List<Student>> getStudentList(
        @RequestParam(defaultValue = "1") int page,        // 接收page参数
        @RequestParam(defaultValue = "10") int size,       // 接收size参数
        @RequestParam(required = false) String name,       // 接收name参数
        @RequestParam(required = false) String className,  // 接收className参数
        HttpServletRequest request) {                      // 接收HTTP请求对象

    // 1. 从请求头获取JWT token
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
        token = token.substring(7); // 移除"Bearer "前缀
    }

    // 2. 解析token获取用户信息
    Map<String, Object> userInfo = jwtUtils.parseToken(token);
    String userRole = (String) userInfo.get("role");
    Long userId = (Long) userInfo.get("userId");

    // 3. 根据用户角色调用不同的服务方法
    List<Student> students;
    if ("admin".equals(userRole)) {
        students = studentService.getAllStudents(page, size, name, className);
    } else if ("teacher".equals(userRole)) {
        students = studentService.getStudentsForTeacher(page, size, name, className);
    } else {
        students = studentService.getStudentsForStudent(userId, page, size, name, className);
    }

    // 4. 返回结果
    return Result.success(students);
}
```

**步骤4: 服务层处理业务逻辑**
```java
// 服务层根据权限获取数据
public List<Student> getAllStudents(int page, int size, String name, String className) {
    // 1. 构建分页对象
    Page<Student> pageObj = new Page<>(page, size);

    // 2. 构建查询条件
    QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
    if (name != null && !name.trim().isEmpty()) {
        queryWrapper.like("name", name); // SQL: WHERE name LIKE '%张三%'
    }
    if (className != null && !className.trim().isEmpty()) {
        queryWrapper.eq("class_name", className); // SQL: WHERE class_name = '高一(1)班'
    }
    queryWrapper.orderByDesc("created_at"); // SQL: ORDER BY created_at DESC

    // 3. 执行数据库查询
    Page<Student> result = studentMapper.selectPage(pageObj, queryWrapper);
    return result.getRecords();
}
```

**步骤5: 数据库查询**
```sql
-- MyBatis Plus自动生成的SQL查询
SELECT id, name, student_id, class_name, phone, email, address, id_card,
       gender, birth_date, enrollment_date, status, created_at, updated_at
FROM students
WHERE name LIKE '%张三%'
  AND class_name = '高一(1)班'
ORDER BY created_at DESC
LIMIT 0, 10;

-- 查询结果返回给Mapper -> Service -> Controller
```

**步骤6: 后端返回响应**
```java
// 控制器返回统一格式的响应
return Result.success(students);

// Result类的结构：
public class Result<T> {
    private int code;        // 状态码：200成功，400/500错误
    private String message;  // 响应消息
    private T data;         // 响应数据

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        result.data = data;
        return result;
    }
}

// 实际返回的JSON响应：
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "张三",
      "studentId": "2023001",
      "className": "高一(1)班",
      "phone": "13800138001",
      "email": "zhangsan@school.com",
      "address": "北京市朝阳区xxx街道",  // 根据权限可能为null
      "idCard": "110101200501011234",    // 根据权限可能为null
      "createdAt": "2023-09-01T08:00:00"
    }
  ]
}
```

**步骤7: 前端处理响应**
```javascript
// Axios响应拦截器处理
request.interceptors.response.use(
  response => {
    const { code, message, data } = response.data

    if (code === 200) {
      return data // 请求成功，返回数据给业务代码
    } else {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message))
    }
  },
  error => {
    if (error.response?.status === 401) {
      // token过期，跳转登录页
      ElMessage.error('登录已过期，请重新登录')
      removeToken()
      router.push('/login')
    }
    return Promise.reject(error)
  }
)

// 业务代码接收到数据后更新界面
const result = await getStudentList(params)
studentList.value = result.records  // 更新Vue响应式数据
pagination.total = result.total     // 界面自动重新渲染
```

### 4. 数据库设计与配置

#### 4.1 数据库表结构
```sql
-- 创建数据库
CREATE DATABASE student_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_management;

-- 用户表 - 存储系统用户信息
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码哈希',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    role ENUM('admin', 'teacher', 'student') NOT NULL COMMENT '用户角色',
    status ENUM('active', 'inactive') DEFAULT 'active' COMMENT '用户状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB COMMENT='系统用户表';

-- 学生表 - 存储学生详细信息
CREATE TABLE students (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT COMMENT '关联用户表ID',
    name VARCHAR(50) NOT NULL COMMENT '学生姓名',
    student_id VARCHAR(20) UNIQUE NOT NULL COMMENT '学号',
    class_name VARCHAR(50) COMMENT '班级名称',
    phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱地址',
    address TEXT COMMENT '家庭住址（敏感信息）',
    id_card VARCHAR(18) COMMENT '身份证号（敏感信息）',
    gender ENUM('male', 'female', 'other') COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    enrollment_date DATE COMMENT '入学日期',
    graduation_date DATE COMMENT '毕业日期',
    status ENUM('active', 'graduated', 'suspended') DEFAULT 'active' COMMENT '学生状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_student_id (student_id),
    INDEX idx_class_name (class_name),
    INDEX idx_name (name),
    INDEX idx_status (status)
) ENGINE=InnoDB COMMENT='学生信息表';

-- 班级表 - 存储班级信息
CREATE TABLE classes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(50) UNIQUE NOT NULL COMMENT '班级名称',
    teacher_id BIGINT COMMENT '班主任ID',
    grade_level INT COMMENT '年级',
    academic_year VARCHAR(10) COMMENT '学年',
    max_students INT DEFAULT 50 COMMENT '最大学生数',
    description TEXT COMMENT '班级描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_class_name (class_name),
    INDEX idx_teacher_id (teacher_id)
) ENGINE=InnoDB COMMENT='班级信息表';
```

#### 4.2 SpringBoot配置文件 (application.yml)
```yaml
# application.yml - SpringBoot应用配置
server:
  port: 8080  # 服务器端口
  servlet:
    context-path: /  # 应用上下文路径

spring:
  # 数据源配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/student_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

    # 连接池配置
    hikari:
      minimum-idle: 5          # 最小空闲连接数
      maximum-pool-size: 20    # 最大连接池大小
      auto-commit: true        # 自动提交
      idle-timeout: 30000      # 空闲超时时间
      pool-name: StudentManagementHikariCP
      max-lifetime: 1800000    # 连接最大生存时间
      connection-timeout: 30000 # 连接超时时间

# MyBatis Plus配置
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 下划线转驼峰
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志输出
  global-config:
    db-config:
      id-type: auto           # 主键自增
      logic-delete-field: deleted  # 逻辑删除字段
      logic-delete-value: 1   # 逻辑删除值
      logic-not-delete-value: 0  # 逻辑未删除值
  mapper-locations: classpath*:/mapper/**/*.xml  # Mapper XML文件位置

# JWT配置
jwt:
  secret: your-secret-key-here-make-it-long-and-random  # JWT密钥
  expiration: 86400  # token过期时间（秒）

# 日志配置
logging:
  level:
    com.example.university_backend: debug  # 应用日志级别
    org.springframework.security: debug    # Spring Security日志
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

#### 4.3 跨域配置 (config/CorsConfig.java)
```java
// CorsConfig.java - 跨域资源共享配置
package com.example.university_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * 配置跨域访问
     * 允许前端Vue应用访问后端API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // 对所有API路径生效
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")  // 允许的前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的HTTP方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true)  // 允许携带认证信息
                .maxAge(3600);  // 预检请求缓存时间
    }

    /**
     * 配置CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的源地址
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedOrigin("http://localhost:3000");

        // 允许的HTTP方法
        configuration.addAllowedMethod("*");

        // 允许的请求头
        configuration.addAllowedHeader("*");

        // 允许携带认证信息
        configuration.setAllowCredentials(true);

        // 应用到所有路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
```

### 5. JWT认证机制实现

#### 5.1 JWT工具类 (util/JwtUtils.java)
```java
// JwtUtils.java - JWT令牌工具类
package com.example.university_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;  // JWT密钥

    @Value("${jwt.expiration}")
    private Long expiration;  // 过期时间

    /**
     * 生成JWT令牌
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        return createToken(claims, username);
    }

    /**
     * 创建令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);

        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        return Jwts.builder()
                .setClaims(claims)          // 设置载荷
                .setSubject(subject)        // 设置主题
                .setIssuedAt(now)          // 设置签发时间
                .setExpiration(expiryDate)  // 设置过期时间
                .signWith(key, SignatureAlgorithm.HS256)  // 设置签名算法
                .compact();
    }

    /**
     * 解析JWT令牌
     */
    public Map<String, Object> parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Map<String, Object> result = new HashMap<>();
            result.put("userId", claims.get("userId"));
            result.put("username", claims.get("username"));
            result.put("role", claims.get("role"));
            result.put("expiration", claims.getExpiration());

            return result;

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("不支持的Token");
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Token格式错误");
        } catch (SecurityException e) {
            throw new RuntimeException("Token签名验证失败");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Token参数错误");
        }
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Map<String, Object> claims = parseToken(token);
        return (String) claims.get("username");
    }

    /**
     * 检查令牌是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Map<String, Object> claims = parseToken(token);
            Date expiration = (Date) claims.get("expiration");
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
```

### 6. 项目运行指南

#### 6.1 环境准备

**系统要求：**
- Java 17 或更高版本
- Node.js 16 或更高版本
- MySQL 8.0 或更高版本
- Maven 3.6 或更高版本

**开发工具推荐：**
- 后端：IntelliJ IDEA 或 Eclipse
- 前端：VS Code
- 数据库：MySQL Workbench 或 Navicat

#### 6.2 后端启动步骤

**1. 创建数据库**
```sql
-- 登录MySQL
mysql -u root -p

-- 创建数据库
CREATE DATABASE student_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'student_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON student_management.* TO 'student_user'@'localhost';
FLUSH PRIVILEGES;
```

**2. 配置application.yml**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/student_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root  # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码
```

**3. 启动后端应用**
```bash
# 进入后端项目目录
cd backend/university-backend

# 使用Maven编译和运行
mvn clean compile
mvn spring-boot:run

# 或者使用Maven包装器
./mvnw spring-boot:run

# 看到以下信息表示启动成功：
# Started UniversityBackendApplication in 3.456 seconds
# Tomcat started on port(s): 8080 (http)
```

#### 6.3 前端启动步骤

**1. 安装依赖**
```bash
# 进入前端项目目录
cd frontend/my-vue3-app

# 安装npm依赖
npm install

# 或使用yarn
yarn install
```

**2. 启动开发服务器**
```bash
# 启动Vite开发服务器
npm run dev

# 或使用yarn
yarn dev

# 看到以下信息表示启动成功：
# Local:   http://localhost:5173/
# Network: http://192.168.1.100:5173/
```

#### 6.4 系统使用流程

**完整的用户操作流程：**

1. **用户访问系统**
   ```
   用户在浏览器输入 http://localhost:5173
   ↓
   Vue Router检查路由，发现需要认证
   ↓
   自动跳转到登录页面 /login
   ```

2. **用户登录**
   ```
   用户输入用户名和密码
   ↓
   前端验证表单数据
   ↓
   发送POST请求到 /api/auth/login
   ↓
   后端验证用户凭据
   ↓
   生成JWT token并返回
   ↓
   前端存储token到localStorage
   ↓
   跳转到主页面
   ```

3. **获取学生列表**
   ```
   用户点击"学生管理"菜单
   ↓
   Vue Router导航到 /students
   ↓
   StudentList组件挂载，调用fetchStudentList()
   ↓
   发送GET请求到 /api/students（携带JWT token）
   ↓
   后端验证token，解析用户角色
   ↓
   根据角色权限查询数据库
   ↓
   返回相应的学生数据
   ↓
   前端更新界面显示
   ```

4. **权限控制示例**
   ```
   管理员登录 → 看到所有学生完整信息（包括敏感数据）
   教师登录   → 看到所有学生基本信息（不包括敏感数据）
   学生登录   → 只看到同班同学基本信息 + 自己完整信息
   ```

### 7. 文件间的联系关系

#### 7.1 前端文件关系图
```
main.js (应用入口)
├── App.vue (根组件)
├── router/index.js (路由配置)
│   ├── views/Login.vue (登录页面)
│   ├── views/Home.vue (首页)
│   ├── views/StudentList.vue (学生列表)
│   └── views/User.vue (用户管理)
├── utils/auth.js (认证工具)
├── api/request.js (HTTP请求封装)
└── components/ (通用组件)
    ├── Header.vue
    ├── Sidebar.vue
    └── StudentForm.vue
```

**文件调用关系：**
- `main.js` → 创建Vue应用，注册路由和插件
- `App.vue` → 应用根组件，包含路由出口
- `router/index.js` → 定义路由规则，配置路由守卫
- `views/*.vue` → 页面组件，调用API获取数据
- `api/request.js` → 封装HTTP请求，统一处理响应
- `utils/auth.js` → 管理token，提供认证方法

#### 7.2 后端文件关系图
```
UniversityBackendApplication.java (启动类)
├── controller/ (控制器层)
│   ├── StudentController.java
│   ├── UserController.java
│   └── AuthController.java
├── service/ (服务层)
│   ├── StudentService.java
│   ├── UserService.java
│   └── AuthService.java
├── mapper/ (数据访问层)
│   ├── StudentMapper.java
│   ├── UserMapper.java
│   └── BaseMapper (MyBatis Plus)
├── entity/ (实体类)
│   ├── Student.java
│   ├── User.java
│   └── Role.java
├── config/ (配置类)
│   ├── CorsConfig.java
│   └── MyBatisPlusConfig.java
└── util/ (工具类)
    ├── JwtUtils.java
    └── Result.java
```

**调用链路：**
1. `Controller` 接收HTTP请求
2. `Controller` 调用 `Service` 处理业务逻辑
3. `Service` 调用 `Mapper` 访问数据库
4. `Mapper` 使用 `Entity` 映射数据库记录
5. 结果逐层返回到 `Controller`
6. `Controller` 返回JSON响应

### 8. 核心原理总结

#### 8.1 前后端分离原理
```
前端职责：
- 用户界面展示
- 用户交互处理
- 数据展示和表单处理
- 路由管理和页面跳转
- 状态管理

后端职责：
- 业务逻辑处理
- 数据库操作
- 用户认证和授权
- API接口提供
- 数据验证和安全控制

通信方式：
- HTTP/HTTPS协议
- JSON数据格式
- RESTful API设计
- JWT token认证
```

#### 8.2 权限控制原理
```
基于角色的访问控制(RBAC)：

1. 用户登录时，后端验证身份并生成JWT token
2. JWT token包含用户ID、角色等信息
3. 前端每次请求都携带token
4. 后端解析token获取用户角色
5. 根据角色权限返回相应数据
6. 前端根据权限显示不同界面元素

权限级别：
- admin: 完整CRUD权限，可管理所有数据
- teacher: 查看所有学生，编辑部分字段
- student: 只能查看同班同学基本信息和自己完整信息
```

#### 8.3 数据流转原理
```
完整的数据流转过程：

用户操作 → Vue组件方法 → API调用 → HTTP请求
→ SpringBoot Controller → Service业务逻辑 → Mapper数据访问
→ MySQL数据库 → 返回结果 → JSON响应 → 前端处理 → 界面更新

关键技术点：
- Vue3响应式系统自动更新界面
- Axios拦截器统一处理请求和响应
- MyBatis Plus简化数据库操作
- JWT token保证接口安全
- 分层架构保证代码可维护性
```

通过这个完整的指南，零基础的开发者可以：
1. 理解前后端分离架构的工作原理
2. 掌握Vue3和SpringBoot的核心技术
3. 学会JWT认证和权限控制的实现
4. 了解完整的项目开发流程
5. 能够独立搭建和运行整个系统

这个学生管理系统涵盖了现代Web开发的核心技术栈，是学习全栈开发的优秀实践项目。
```

#### 1.4 学生列表页面 (views/StudentList.vue)
```vue
<!-- StudentList.vue - 学生列表页面组件 -->
<template>
  <div class="student-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>学生管理</h2>
      <el-button
        v-if="hasPermission('student:create')"
        type="primary"
        @click="showAddDialog = true"
      >
        添加学生
      </el-button>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="search-area">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="姓名">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入学生姓名"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="searchForm.className" placeholder="请选择班级">
            <el-option label="全部" value="" />
            <el-option
              v-for="cls in classList"
              :key="cls.id"
              :label="cls.name"
              :value="cls.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 学生数据表格 -->
    <el-table
      :data="studentList"
      v-loading="loading"
      style="width: 100%"
    >
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="studentId" label="学号" width="150" />
      <el-table-column prop="className" label="班级" width="120" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />

      <!-- 根据权限显示敏感信息 -->
      <el-table-column
        v-if="hasPermission('student:view:sensitive')"
        prop="address"
        label="地址"
        width="200"
      />
      <el-table-column
        v-if="hasPermission('student:view:sensitive')"
        prop="idCard"
        label="身份证"
        width="180"
      />

      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            v-if="hasPermission('student:edit')"
            size="small"
            @click="handleEdit(row)"
          >
            编辑
          </el-button>
          <el-button
            v-if="hasPermission('student:delete')"
            size="small"
            type="danger"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <!-- 添加/编辑学生对话框 -->
    <StudentForm
      v-model:visible="showAddDialog"
      :student-data="currentStudent"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentList, deleteStudent } from '@/api/student'
import { getClassList } from '@/api/class'
import { hasPermission } from '@/utils/permission'
import StudentForm from '@/components/StudentForm.vue'

// 响应式数据定义
const loading = ref(false)
const studentList = ref([])
const classList = ref([])
const showAddDialog = ref(false)
const currentStudent = ref(null)

// 搜索表单数据
const searchForm = reactive({
  name: '',
  className: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取学生列表数据
const fetchStudentList = async () => {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      page: pagination.current,
      size: pagination.size
    }

    const result = await getStudentList(params)
    studentList.value = result.records
    pagination.total = result.total
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

// 获取班级列表
const fetchClassList = async () => {
  try {
    classList.value = await getClassList()
  } catch (error) {
    console.error('获取班级列表失败:', error)
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  fetchStudentList()
}

// 重置搜索
const handleReset = () => {
  Object.assign(searchForm, { name: '', className: '' })
  handleSearch()
}

// 编辑学生
const handleEdit = (row) => {
  currentStudent.value = { ...row }
  showAddDialog.value = true
}

// 删除学生
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除学生 ${row.name} 吗？`,
      '确认删除',
      { type: 'warning' }
    )

    await deleteStudent(row.id)
    ElMessage.success('删除成功')
    fetchStudentList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 表单提交成功处理
const handleFormSuccess = () => {
  showAddDialog.value = false
  currentStudent.value = null
  fetchStudentList()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  fetchStudentList()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchStudentList()
}

// 组件挂载时获取数据
onMounted(() => {
  fetchStudentList()
  fetchClassList()
})
</script>

<style scoped>
.student-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-area {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}
</style>
```
```
