import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'

console.log('开始初始化应用...')

// 检查token是否有效（这里简单实现，实际应该验证token）
const checkToken = () => {
  const token = localStorage.getItem('token')
  if (token) {
    try {
      // 这里可以添加token验证逻辑
      console.log('已检查token，继续使用现有token')
    } catch (e) {
      localStorage.removeItem('token')
      console.log('token无效，已清除')
    }
  }
}

// 应用启动时检查token
checkToken()

try {
  const app = createApp(App)
  
  console.log('应用创建成功，正在添加插件...')
  
  app.use(router)
  app.use(ElementPlus, {
    locale: zhCn
  })
  
  console.log('插件添加成功，正在挂载应用...')
  
  app.mount('#app')
  
  console.log('应用挂载成功')
  
  // 全局错误处理
  app.config.errorHandler = (err, vm, info) => {
    console.error('Vue 错误:', err)
    console.error('错误信息:', info)
  }
} catch (error) {
  console.error('应用初始化失败:', error)
}



