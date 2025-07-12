<template>
  <div class="login-container">
    <div class="login-form">
      <h2>学生管理系统</h2>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="el-icon-lock"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: 'admin',
  password: '123456'
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = () => {
  if (!loginFormRef.value) return
  
  loginFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      console.log('登录表单数据:', loginForm)
      
      // 确保发送正确的数据格式
      const data = await request({
        url: '/api/auth/login',
        method: 'post',
        data: {
          username: loginForm.username.trim(),
          password: loginForm.password.trim()
        }
      })
      
      console.log('登录成功:', data)
      
      // 保存token和用户信息
      if (data.token) {
        localStorage.setItem('token', data.token)
        localStorage.setItem('userInfo', JSON.stringify(data.user || {}))
        
        ElMessage.success('登录成功')
        router.push('/')
      } else {
        ElMessage.error('登录失败: 服务器返回数据格式不正确')
      }
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.login-form {
  width: 350px;
  padding: 30px;
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}
</style>
