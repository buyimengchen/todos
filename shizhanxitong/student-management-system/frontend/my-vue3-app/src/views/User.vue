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
      <!-- 顶部用户信息栏 -->
      <div class="top-header">
        <div class="user-info">
          <span>欢迎，{{ currentUser.realName || currentUser.username || '用户' }}</span>
        </div>
        <el-button type="danger" @click="handleLogout" size="small">
          退出登录
        </el-button>
      </div>

      <!-- 用户管理内容 -->
      <div class="container">
        <div class="header">
          <h2>用户管理</h2>
          <!-- 只有管理员可以新增用户 -->
          <el-button v-if="hasManagePermission" type="primary" @click="addUser">新增用户</el-button>
        </div>

        <!-- 用户表格 -->
        <el-table :data="displayUsers" style="width: 100%" border>
          <el-table-column prop="id" label="序号" width="80" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="realName" label="姓名" width="120" />
          <el-table-column prop="email" label="邮箱" width="180" />
          <el-table-column prop="phone" label="电话" width="120" />
          <el-table-column label="角色" width="100">
            <template #default="scope">
              <el-tag :type="getRoleType(scope.row.role)">{{ getRoleLabel(scope.row.role) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 操作列：管理员可以编辑删除所有用户，学生和教师只能编辑自己 -->
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <!-- 管理员可以编辑删除所有用户 -->
              <template v-if="hasManagePermission">
                <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
                <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
              </template>
              <!-- 学生和教师只能编辑自己的信息 -->
              <template v-else-if="scope.row.id === currentUser.id">
                <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
        
        <!-- 用户表单对话框 -->
        <el-dialog
          :title="isEditUser ? '编辑用户' : '新增用户'"
          v-model="showUserDialog"
          width="500px"
        >
          <el-form
            ref="userFormRef"
            :model="userForm"
            :rules="userRules"
            label-width="100px"
          >
            <el-form-item label="用户名" prop="username">
              <el-input v-model="userForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码" prop="password" v-if="!isEditUser">
              <el-input v-model="userForm.password" type="password" placeholder="请输入密码" />
            </el-form-item>
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="userForm.realName" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="电话" prop="phone">
              <el-input v-model="userForm.phone" placeholder="请输入电话" />
            </el-form-item>
            <!-- 角色：只有管理员可以修改 -->
            <el-form-item label="角色" prop="role">
              <el-select v-model="userForm.role" placeholder="请选择角色" :disabled="!isAdmin">
                <el-option label="管理员" value="ADMIN" />
                <el-option label="教师" value="TEACHER" />
                <el-option label="学生" value="STUDENT" />
              </el-select>
            </el-form-item>
            <!-- 状态：只有管理员可以修改 -->
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="userForm.status" :disabled="!isAdmin">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="showUserDialog = false">取消</el-button>
              <el-button type="primary" @click="saveUser">确定</el-button>
            </span>
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { Menu, House, Collection, Notebook, DataAnalysis, User, Avatar } from '@element-plus/icons-vue'
import request from '../utils/request'

// 添加导航相关变量
const router = useRouter()
const collapsed = ref(false)
const activeMenu = ref('user')

// 当前用户信息
const currentUser = ref({})

// 计算属性：用户角色
const userRole = computed(() => {
  return currentUser.value.role || ''
})

// 计算属性：是否为管理员
const isAdmin = computed(() => {
  return userRole.value.toUpperCase() === 'ADMIN'
})

// 计算属性：是否为教师
const isTeacher = computed(() => {
  return userRole.value.toUpperCase() === 'TEACHER'
})

// 计算属性：是否为学生
const isStudent = computed(() => {
  return userRole.value.toUpperCase() === 'STUDENT'
})

// 计算属性：是否有管理权限（只有管理员可以管理所有用户）
const hasManagePermission = computed(() => {
  return isAdmin.value
})

// 获取当前用户信息
const getCurrentUser = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    try {
      currentUser.value = JSON.parse(userInfo)
      console.log('当前用户信息:', currentUser.value)
      console.log('用户角色:', userRole.value)
      console.log('是否有管理权限:', hasManagePermission.value)
    } catch (error) {
      console.error('解析用户信息失败:', error)
      currentUser.value = {}
    }
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除本地存储的用户信息和token
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')

    ElMessage.success('退出登录成功')

    // 跳转到登录页面
    router.push('/login')
  }).catch(() => {
    // 取消退出
  })
}

// 添加导航处理函数
const handleSelect = (key) => {
  if (key === 'student') {
    router.push('/student')
  } else if (key === 'home') {
    router.push('/')
  } else if (key === 'user') {
    router.push('/user')
  } else if (key === 'class') {
    router.push('/class')
  } else if (key === 'course') {
    router.push('/course')
  } else if (key === 'score') {
    router.push('/score')
  } else {
    router.push('/')
  }
}

// 用户列表（后端已经根据权限返回相应的数据）
const users = ref([])

// 计算属性：显示用户列表（后端已处理权限，直接返回即可）
const displayUsers = computed(() => {
  return users.value
})

// 是否显示用户对话框
const showUserDialog = ref(false)
// 是否是编辑用户
const isEditUser = ref(false)
// 编辑用户ID
const editUserId = ref(null)
// 用户表单引用
const userFormRef = ref(null)
// 用户表单数据
const userForm = ref({
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  role: 'STUDENT',
  status: 1
})

// 表单验证规则（动态规则，根据用户权限调整）
const userRules = computed(() => {
  const baseRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
    ],
    realName: [
      { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    email: [
      // 移除required规则，只保留email格式验证
      { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ]
  }

  // 只有管理员需要验证角色和状态字段
  if (isAdmin.value) {
    baseRules.role = [
      { required: true, message: '请选择角色', trigger: 'change' }
    ]
    baseRules.status = [
      { required: true, message: '请选择状态', trigger: 'change' }
    ]
  }

  return baseRules
})

// 获取角色标签
const getRoleLabel = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return roleMap[role] || role
}

// 获取角色标签类型
const getRoleType = (role) => {
  const typeMap = {
    'ADMIN': 'danger',
    'TEACHER': 'warning',
    'STUDENT': 'success'
  }
  return typeMap[role] || ''
}

// 获取用户列表
const getUserList = async () => {
  try {
    console.log('开始获取用户数据...')
    console.log('当前用户权限:', hasManagePermission.value)

    // 后端已经根据权限返回相应的用户列表，前端直接调用即可
    const res = await request.get('/api/users')
    console.log('获取到的用户数据:', res)

    // 检查响应数据结构
    let userData = res;

    // 如果响应是包装在data字段中的
    if (res && res.data && Array.isArray(res.data)) {
      userData = res.data;
    }
    // 如果响应本身就是数组
    else if (Array.isArray(res)) {
      userData = res;
    }
    // 其他情况，可能是空数据或格式不对
    else {
      userData = [];
      console.log('响应格式不符合预期:', res);
    }

    users.value = userData;
    console.log('处理后的用户数据:', users.value);
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败: ' + (error.response?.data?.message || error.message))
  }
}

// 添加用户
const addUser = () => {
  isEditUser.value = false
  editUserId.value = null
  userForm.value = {
    username: '',
    password: '',
    realName: '',
    email: '',
    phone: '',
    role: 'STUDENT',
    status: 1
  }
  showUserDialog.value = true
  
  // 确保对话框显示后再聚焦第一个输入框
  nextTick(() => {
    if (userFormRef.value) {
      userFormRef.value.$el.querySelector('input').focus()
    }
  })
}

// 编辑用户
const editUser = (user) => {
  isEditUser.value = true
  editUserId.value = user.id
  // 创建一个深拷贝，避免直接修改原始对象
  userForm.value = JSON.parse(JSON.stringify(user))
  // 编辑时不需要密码字段
  delete userForm.value.password
  showUserDialog.value = true
}

// 保存用户
const saveUser = () => {
  if (!userFormRef.value) return
  
  userFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      // 准备要发送的数据，确保格式正确
      const userData = { ...userForm.value };
      
      // 确保status是数字类型
      if (userData.status !== undefined) {
        userData.status = Number(userData.status);
      }
      
      console.log('准备发送的用户数据:', userData);
      
      if (isEditUser.value) {
        // 编辑用户
        const res = await request.put(`/api/users/${editUserId.value}`, userData);
        console.log('更新用户响应:', res);
        
        ElMessage.success('更新用户成功');
        showUserDialog.value = false;
        
        // 重新获取用户列表
        getUserList();
      } else {
        // 新增用户
        const res = await request.post('/api/users', userData);
        console.log('添加用户响应:', res);
        
        ElMessage.success('新增用户成功');
        showUserDialog.value = false;
        
        // 重新获取用户列表
        getUserList();
      }
    } catch (error) {
      console.error('保存用户失败:', error);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
      ElMessage.error('保存用户失败: ' + (error.response?.data?.message || error.message));
    }
  });
}

// 删除用户
const deleteUser = (id) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request.delete(`/api/users/${id}`)
      
      // 从本地列表中移除
      const index = users.value.findIndex(u => u.id === id)
      if (index !== -1) {
        users.value.splice(index, 1)
      }
      
      ElMessage.success('删除用户成功')
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败: ' + (error.response?.data?.message || error.message))
    }
  }).catch(() => {
    // 取消删除
  })
}

// 组件挂载时获取用户列表
onMounted(async () => {
  console.log('User组件已挂载')
  getCurrentUser()  // 获取当前用户信息

  // 等待一下确保currentUser已经设置
  await nextTick()

  // 然后获取用户列表
  getUserList()
})
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
}

.main-content.collapsed {
  margin-left: 64px;
}

.top-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: #fff;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 20px;
}

.user-info {
  font-size: 14px;
  color: #666;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.container {
  padding: 20px;
}
</style>





