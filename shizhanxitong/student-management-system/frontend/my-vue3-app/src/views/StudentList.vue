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
        <el-form :inline="true" :model="searchForm" style="margin-bottom: 16px;">
          <el-form-item label="学号">
            <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchStudents">查询</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
        <!-- 修改新增按钮，只有管理员和教师可见 -->
        <el-button 
          v-if="hasEditPermission" 
          type="primary" 
          @click="showAddDialogFunc" 
          style="margin-bottom: 16px;">新增学生</el-button>
        
        <!-- 修改表格样式，使其铺满屏幕 -->
        <el-table
          :data="students"
          style="width: 100%;"
          border
          stripe
          height="calc(100vh - 180px)">
          <el-table-column prop="id" label="序号" width="60" fixed/>
          <el-table-column prop="studentNo" label="学号" width="100" fixed/>
          <el-table-column prop="name" label="姓名" width="100" fixed/>
          <el-table-column prop="gender" label="性别" width="60"/>
          <!-- 出生日期：管理员和教师总是可见，学生可以看到自己的记录 -->
          <el-table-column prop="birthDate" label="出生日期" width="120">
            <template #default="scope">
              <span v-if="!isStudent || scope.row.studentNo === currentUser.username">{{ scope.row.birthDate || '-' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <!-- 身份证号：管理员和教师总是可见，学生只有自己的记录有值时才显示 -->
          <el-table-column prop="idCard" label="身份证号" width="180">
            <template #default="scope">
              <span v-if="!isStudent || scope.row.idCard">{{ scope.row.idCard || '-' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="手机号" width="120"/>
          <el-table-column prop="email" label="邮箱" width="180"/>
          <!-- 家庭住址：管理员和教师总是可见，学生只有自己的记录有值时才显示 -->
          <el-table-column prop="address" label="家庭住址" width="200">
            <template #default="scope">
              <span v-if="!isStudent || scope.row.address">{{ scope.row.address || '-' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="classId" label="班级ID" width="100"/>
          <el-table-column prop="enrollmentDate" label="入学日期" width="120"/>
          <!-- 毕业日期：管理员和教师总是可见，学生只有自己的记录有值时才显示 -->
          <el-table-column prop="graduationDate" label="毕业日期" width="120">
            <template #default="scope">
              <span v-if="!isStudent || scope.row.graduationDate">{{ scope.row.graduationDate || '-' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <!-- 学生类型：管理员和教师总是可见，学生只有自己的记录有值时才显示 -->
          <el-table-column prop="studentType" label="学生类型" width="100">
            <template #default="scope">
              <span v-if="!isStudent || scope.row.studentType">{{ scope.row.studentType || '-' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <!-- 入学类型：管理员和教师总是可见，学生只有自己的记录有值时才显示 -->
          <el-table-column prop="enrollmentType" label="入学类型" width="100">
            <template #default="scope">
              <span v-if="!isStudent || scope.row.enrollmentType">{{ scope.row.enrollmentType || '-' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
                {{ scope.row.status === 1 ? '正常' : '停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalCredits" label="总学分" width="80"/>
          <el-table-column prop="gpa" label="GPA" width="80"/>
          <!-- 创建时间：管理员和教师总是可见，学生只有自己的记录有值时才显示 -->
          <el-table-column prop="createTime" label="创建时间" width="160">
            <template #default="scope">
              <span v-if="!isStudent || scope.row.createTime">{{ scope.row.createTime || '-' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <!-- 更新时间：管理员和教师总是可见，学生只有自己的记录有值时才显示 -->
          <el-table-column prop="updateTime" label="更新时间" width="160">
            <template #default="scope">
              <span v-if="!isStudent || scope.row.updateTime">{{ scope.row.updateTime || '-' }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <!-- 管理员和教师可以编辑删除所有学生 -->
              <template v-if="hasEditPermission">
                <el-button
                  size="small"
                  type="primary"
                  @click="editStudent(scope.row)">编辑</el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteStudent(scope.row.id)"
                  v-if="isAdmin">删除</el-button>
              </template>
              <!-- 学生只能编辑自己的信息 -->
              <template v-else-if="isStudent && scope.row.studentNo === currentUser.username">
                <el-button
                  size="small"
                  type="primary"
                  @click="editStudent(scope.row)">编辑</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
        
        <el-dialog :title="getDialogTitle()" v-model="showAddDialog">
          <el-form :model="newStudent" label-width="100px" :rules="studentRules" ref="studentFormRef">
            <!-- 学号：管理员和教师可编辑，学生不可编辑 -->
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="newStudent.studentNo" :disabled="isStudent"></el-input>
            </el-form-item>
            <!-- 姓名：管理员和教师可编辑，学生不可编辑 -->
            <el-form-item label="姓名" prop="name">
              <el-input v-model="newStudent.name" :disabled="isStudent"></el-input>
            </el-form-item>
            <!-- 性别：管理员和教师可编辑，学生不可编辑 -->
            <el-form-item label="性别" prop="gender">
              <el-select v-model="newStudent.gender" placeholder="请选择" :disabled="isStudent">
                <el-option label="男" value="男"></el-option>
                <el-option label="女" value="女"></el-option>
              </el-select>
            </el-form-item>
            <!-- 出生日期：管理员和教师可编辑，学生编辑自己时可编辑 -->
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker v-model="newStudent.birthDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" :disabled="isStudent && !isEditingSelf"></el-date-picker>
            </el-form-item>
            <!-- 身份证号：管理员和教师可编辑，学生编辑自己时可编辑 -->
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="newStudent.idCard" :disabled="isStudent && !isEditingSelf"></el-input>
            </el-form-item>
            <!-- 手机号：所有人可编辑 -->
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="newStudent.phone"></el-input>
            </el-form-item>
            <!-- 邮箱：所有人可编辑 -->
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="newStudent.email"></el-input>
            </el-form-item>
            <!-- 家庭住址：管理员和教师可编辑，学生编辑自己时可编辑 -->
            <el-form-item label="家庭住址">
              <el-input v-model="newStudent.address" :disabled="isStudent && !isEditingSelf"></el-input>
            </el-form-item>
            <!-- 班级ID：管理员和教师可编辑，学生不可编辑 -->
            <el-form-item label="班级ID" prop="classId">
              <el-input v-model="newStudent.classId" :disabled="isStudent"></el-input>
            </el-form-item>
            <!-- 入学日期：管理员和教师可编辑，学生不可编辑 -->
            <el-form-item label="入学日期" prop="enrollmentDate">
              <el-date-picker v-model="newStudent.enrollmentDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" :disabled="isStudent"></el-date-picker>
            </el-form-item>
            <!-- 毕业日期：管理员和教师可见可编辑，学生不可见 -->
            <el-form-item v-if="!isStudent" label="毕业日期">
              <el-date-picker v-model="newStudent.graduationDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期"></el-date-picker>
            </el-form-item>
            <!-- 学生类型：管理员和教师可见可编辑，学生不可见 -->
            <el-form-item v-if="!isStudent" label="学生类型">
              <el-input v-model="newStudent.studentType"></el-input>
            </el-form-item>
            <!-- 入学类型：管理员和教师可见可编辑，学生不可见 -->
            <el-form-item v-if="!isStudent" label="入学类型">
              <el-input v-model="newStudent.enrollmentType"></el-input>
            </el-form-item>
            <!-- 状态：管理员和教师可编辑，学生不可编辑 -->
            <el-form-item label="状态" prop="status">
              <el-select v-model="newStudent.status" placeholder="请选择" :disabled="isStudent">
                <el-option label="在读" value="1"></el-option>
                <el-option label="休学" value="2"></el-option>
                <el-option label="毕业" value="3"></el-option>
              </el-select>
            </el-form-item>
            <!-- 总学分：管理员和教师可编辑，学生只读 -->
            <el-form-item label="总学分">
              <el-input v-model="newStudent.totalCredits" :disabled="isStudent"></el-input>
            </el-form-item>
            <!-- GPA：管理员和教师可编辑，学生只读 -->
            <el-form-item label="GPA">
              <el-input v-model="newStudent.gpa" :disabled="isStudent"></el-input>
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showAddDialog = false">取消</el-button>
            <el-button type="primary" @click="addOrUpdateStudent">确定</el-button>
          </template>
        </el-dialog>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import { Menu, House, Collection, Notebook, DataAnalysis, User, Avatar } from '@element-plus/icons-vue'

const router = useRouter()
const collapsed = ref(false)
const activeMenu = ref('student')

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

const isEdit = ref(false)
const editId = ref(null)

const students = ref([])
const showAddDialog = ref(false)
const newStudent = ref({
  studentNo: '',
  name: '',
  gender: '',
  birthDate: '',
  idCard: '',
  phone: '',
  email: '',
  address: '',
  classId: '',
  enrollmentDate: '',
  graduationDate: '',
  studentType: '',
  enrollmentType: '',
  status: '1',
  totalCredits: '',
  gpa: ''
})

const editStudent = (student) => {
  isEdit.value = true
  editId.value = student.id
  newStudent.value = { ...student } // 复制数据到表单
  showAddDialog.value = true
}

const formatDate = (date) => {
  if (!date) return null;
  if (typeof date === 'string') {
    // 如果已经是字符串格式，检查是否需要转换
    if (date.includes('T') || date.includes('Z')) {
      // 是ISO格式，需要转换
      const d = new Date(date);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
    }
    return date; // 已经是YYYY-MM-DD格式
  }
  // 是Date对象
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
};

const addOrUpdateStudent = async () => {
  studentFormRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.warning('请填写必填项');
      return;
    }
    
    try {
      // 创建一个新对象，避免直接修改表单数据
      const studentData = { ...newStudent.value };
      
      // 格式化日期字段
      if (studentData.birthDate) {
        studentData.birthDate = formatDate(studentData.birthDate);
      }
      if (studentData.enrollmentDate) {
        studentData.enrollmentDate = formatDate(studentData.enrollmentDate);
      }
      if (studentData.graduationDate) {
        studentData.graduationDate = formatDate(studentData.graduationDate);
      }
      
      // 确保数值字段是数值类型
      if (studentData.classId) {
        studentData.classId = Number(studentData.classId);
      }
      if (studentData.status) {
        studentData.status = Number(studentData.status);
      }
      if (studentData.totalCredits) {
        studentData.totalCredits = Number(studentData.totalCredits);
      }
      if (studentData.gpa) {
        studentData.gpa = Number(studentData.gpa);
      }
      
      console.log('提交的学生数据:', studentData);
      
      if (isEdit.value) {
        // 修改
        console.log('正在更新学生，ID:', editId.value);
        const res = await request.put(`/api/students/${editId.value}`, studentData);
        console.log('更新学生响应:', res);
        ElMessage.success('修改成功');
      } else {
        // 新增
        console.log('正在添加新学生');
        const res = await request.post('/api/students', studentData);
        console.log('添加学生响应:', res);
        ElMessage.success('添加成功');
      }
      
      showAddDialog.value = false;
      fetchStudents();
      
      // 重置表单
      newStudent.value = {
        studentNo: '',
        name: '',
        gender: '',
        birthDate: '',
        idCard: '',
        phone: '',
        email: '',
        address: '',
        classId: '',
        enrollmentDate: '',
        graduationDate: '',
        studentType: '',
        enrollmentType: '',
        status: '1',
        totalCredits: '',
        gpa: ''
      };
      
      isEdit.value = false;
      editId.value = null;
    } catch (error) {
      console.error('操作失败:', error);
      if (error.response) {
        console.error('错误状态码:', error.response.status);
        console.error('错误数据:', error.response.data);
      }
      ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message));
    }
  });
};

const showAddDialogFunc = () => {
  isEdit.value = false
  editId.value = null
  newStudent.value = {
    studentNo: '',
    name: '',
    gender: '',
    birthDate: '',
    idCard: '',
    phone: '',
    email: '',
    address: '',
    classId: '',
    enrollmentDate: '',
    graduationDate: '',
    studentType: '',
    enrollmentType: '',
    status: '1',
    totalCredits: '',
    gpa: ''
  }
  showAddDialog.value = true
}

const deleteStudent = async (id) => {
  try {
    console.log('正在删除学生，ID:', id);
    await request.delete(`/api/students/${id}`);
    ElMessage.success('删除成功');
    fetchStudents();
  } catch (error) {
    console.error('删除失败:', error);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误数据:', error.response.data);
    }
    ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message));
  }
}

const searchForm = ref({
  studentNo: '',
  name: ''
})

const fetchStudents = async () => {
  try {
    console.log('开始获取学生数据...')
    console.log('搜索条件:', searchForm.value)

    // 检查是否有查询条件
    const hasSearchCondition = searchForm.value.studentNo || searchForm.value.name;

    let res;
    if (hasSearchCondition) {
      // 有查询条件，使用搜索接口
      console.log('使用搜索接口...')
      res = await request.get('/api/students/search', {
        params: searchForm.value
      })
    } else {
      // 没有查询条件，获取所有学生
      console.log('获取所有学生...')
      res = await request.get('/api/students')
    }
    
    console.log('获取到的原始学生数据:', res)
    
    // 检查响应数据结构
    let studentData = res;
    
    // 如果响应是包装在data字段中的
    if (res && res.data && Array.isArray(res.data)) {
      studentData = res.data;
    } 
    // 如果响应本身就是数组
    else if (Array.isArray(res)) {
      studentData = res;
    }
    // 其他情况，可能是空数据或格式不对
    else {
      studentData = [];
      console.log('响应格式不符合预期:', res);
    }
    
    console.log('studentData.length:', studentData.length);
    console.log('studentData:', studentData);

    if (studentData.length > 0) {
      // 处理日期格式并确保所有字段都有值
      console.log('开始处理学生数据...');
      students.value = studentData.map(student => {
        // 创建一个包含所有可能字段的对象，初始值为空字符串
        const processedStudent = {
          id: student.id || '',
          studentNo: student.studentNo || '',
          name: student.name || '',
          gender: student.gender || '',
          birthDate: student.birthDate || '',
          idCard: student.idCard || '',
          phone: student.phone || '',
          email: student.email || '',
          address: student.address || '',
          classId: student.classId || '',
          enrollmentDate: '',
          graduationDate: '',
          studentType: student.studentType || '',
          enrollmentType: student.enrollmentType || '',
          status: student.status,
          totalCredits: student.totalCredits || '',
          gpa: student.gpa || '',
          createTime: '',
          updateTime: ''
        };
        
        // 处理日期字段 - 无论是否有值都要处理，确保格式正确
        processedStudent.birthDate = formatDateDisplay(student.birthDate);
        processedStudent.enrollmentDate = formatDateDisplay(student.enrollmentDate);
        processedStudent.graduationDate = formatDateDisplay(student.graduationDate);
        
        if (student.createTime) {
          processedStudent.createTime = formatDateTimeDisplay(student.createTime);
        }
        
        if (student.updateTime) {
          processedStudent.updateTime = formatDateTimeDisplay(student.updateTime);
        }
        
        return processedStudent;
      });

      console.log('处理后的学生数据:', students.value);
      console.log('students.value.length:', students.value.length);
      console.log('Vue响应式检查 - students是否为ref:', students.value instanceof Array);

      // 强制触发响应式更新
      console.log('强制触发响应式更新...');
      setTimeout(() => {
        console.log('延迟检查 - students.value:', students.value);
      }, 100);
    } else {
      students.value = [];
      ElMessage.info('没有找到符合条件的学生数据');
    }
  } catch (error) {
    console.error('获取学生数据失败:', error);
    if (error.response) {
      console.error('错误状态码:', error.response.status);
      console.error('错误数据:', error.response.data);
    }
    ElMessage.error('获取学生数据失败: ' + (error.response?.data?.message || error.message));
  }
};

const resetSearch = () => {
  searchForm.value = { studentNo: '', name: '' }
  fetchStudents()
}

// 获取当前用户信息
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

// 判断学生是否在编辑自己的信息
const isEditingSelf = computed(() => {
  if (!isStudent.value || !isEdit.value) return false
  // 通过学号判断是否是自己
  const currentUsername = currentUser.value?.username
  const editingStudentNo = newStudent.value.studentNo
  console.log('判断是否编辑自己:', { currentUsername, editingStudentNo, isEdit: isEdit.value })
  return currentUsername === editingStudentNo
})

// 计算属性：是否有编辑权限
const hasEditPermission = computed(() => {
  console.log('当前用户角色:', currentUser.value.role);
  return currentUser.value && 
         currentUser.value.role && 
         (currentUser.value.role.toUpperCase() === 'ADMIN' || 
          currentUser.value.role.toUpperCase() === 'TEACHER');
})

// 获取当前用户信息
const fetchCurrentUser = () => {
  const userInfo = localStorage.getItem('userInfo');
  if (userInfo) {
    try {
      currentUser.value = JSON.parse(userInfo);
      console.log('获取到的用户信息:', currentUser.value);
    } catch (error) {
      console.error('解析用户信息失败:', error);
    }
  } else {
    console.warn('localStorage中没有找到userInfo');
  }
}

// 获取对话框标题
const getDialogTitle = () => {
  if (isEdit.value) {
    if (isStudent.value) {
      return '编辑个人信息'
    } else {
      return '编辑学生'
    }
  } else {
    return '新增学生'
  }
}

onMounted(() => {
  console.log('StudentList组件已挂载');
  fetchCurrentUser();
  console.log('当前用户信息:', currentUser.value);
  console.log('是否有编辑权限:', hasEditPermission.value);
  fetchStudents();
})

// 格式化日期显示 (yyyy-MM-dd)
const formatDateDisplay = (dateStr) => {
  if (!dateStr) return '';
  
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr; // 如果不是有效日期，直接返回原字符串
    
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  } catch (e) {
    console.error('日期格式化错误:', e);
    return dateStr;
  }
};

// 格式化日期时间显示 (yyyy-MM-dd HH:mm:ss)
const formatDateTimeDisplay = (dateStr) => {
  if (!dateStr) return '';
  
  try {
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return dateStr; // 如果不是有效日期，直接返回原字符串
    
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`;
  } catch (e) {
    console.error('日期时间格式化错误:', e);
    return dateStr;
  }
};

// 在script部分添加以下代码
const studentFormRef = ref(null);

// 表单验证规则
const studentRules = {
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  birthDate: [
    // 移除了required规则，出生日期不再是必填项
  ],
  idCard: [
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  classId: [
    { required: true, message: '请输入班级ID', trigger: 'blur' }
  ],
  enrollmentDate: [
    { required: true, message: '请选择入学日期', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
};
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

.container {
  padding: 20px;
  height: 100%;
  width: 100%;
  box-sizing: border-box;
}

/* 确保表格内容可以完整显示 */
.el-table {
  width: 100% !important;
}

/* 调整表格行高 */
:deep(.el-table__row) {
  height: 50px;
}
</style>
