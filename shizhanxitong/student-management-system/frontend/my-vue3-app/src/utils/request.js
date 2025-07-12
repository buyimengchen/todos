import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080', // 确保这个URL是正确的
  timeout: 10000,
  withCredentials: true, // 允许跨域请求携带凭证
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    console.log('发送请求:', config.url, config.method, config.data || config.params);
    
    // 如果有token则添加到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    console.log('收到响应:', response.config.url, response.status, response.data);
    
    // 检查响应数据结构
    if (response.data && typeof response.data === 'object') {
      // 如果响应是标准格式（包含code, message, data字段）
      if ('code' in response.data && 'data' in response.data) {
        if (response.data.code === 200 || response.data.code === 0) {
          return response.data;
        } else {
          ElMessage.error(response.data.message || '请求失败');
          return Promise.reject(new Error(response.data.message || '请求失败'));
        }
      }
      // 如果响应直接就是数据
      return response.data;
    }
    
    return response.data;
  },
  error => {
    console.error('响应错误:', error);
    
    if (error.response) {
      console.error('错误详情:', error.response.status, error.response.data);
      
      // 根据状态码处理不同的错误
      switch (error.response.status) {
        case 400:
          ElMessage.error('请求参数错误: ' + (error.response.data.message || '未知错误'))
          break
        case 401:
          ElMessage.error('未授权，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          setTimeout(() => {
            window.location.href = '/login'
          }, 1500)
          break
        case 403:
          ElMessage.error('拒绝访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误: ' + (error.response.data?.message || '未知错误'))
          break
        default:
          ElMessage.error('未知错误')
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      ElMessage.error('网络错误，服务器未响应')
    } else {
      // 请求配置出错
      ElMessage.error('请求配置错误: ' + error.message)
    }
    
    return Promise.reject(error)
  }
)

export default service
