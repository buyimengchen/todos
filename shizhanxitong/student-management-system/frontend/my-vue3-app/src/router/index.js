import { createRouter, createWebHistory } from 'vue-router'
import StudentList from '../views/StudentList.vue'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'
import User from '../views/User.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/student',
    name: 'StudentList',
    component: StudentList,
    meta: { requiresAuth: true }
  },
  {
    path: '/user',
    name: 'User',
    component: User,
    meta: { requiresAuth: true }
  },
  // 默认重定向到登录页
  {
    path: '/:pathMatch(.*)*',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !token) {
    // 需要登录但没有token，重定向到登录页
    next('/login')
  } else {
    next()
  }
})

export default router
