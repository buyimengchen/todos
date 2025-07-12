package com.example.university_backend.controller;

import com.example.university_backend.entity.Student;
import com.example.university_backend.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents(HttpServletRequest request) {
        // 获取当前用户的角色和用户名
        String role = (String) request.getAttribute("role");
        String username = (String) request.getAttribute("username");
        Integer userId = (Integer) request.getAttribute("userId");

        System.out.println("获取学生列表 - 当前用户角色: " + role + ", 用户名: " + username + ", 用户ID: " + userId);

        List<Student> students = studentService.findAll();
        log.debug("查询到 {} 条学生记录", students.size());

        // 根据角色过滤数据
        if ("ADMIN".equals(role) || "TEACHER".equals(role)) {
            // 管理员和教师可以看到所有学生信息
            return ResponseEntity.ok(students);
        } else if ("STUDENT".equals(role)) {
            // 学生可以看到同班同学的基本信息，但不能看到敏感信息
            // 首先找到当前学生的班级ID
            Integer currentStudentClassId = null;
            for (Student student : students) {
                if (username.equals(student.getStudentNo())) {
                    currentStudentClassId = student.getClassId();
                    break;
                }
            }

            List<Student> filteredStudents = new ArrayList<>();
            if (currentStudentClassId != null) {
                for (Student student : students) {
                    // 只显示同班同学
                    if (currentStudentClassId.equals(student.getClassId())) {
                        Student filteredStudent = new Student();
                        filteredStudent.setId(student.getId());
                        filteredStudent.setStudentNo(student.getStudentNo());
                        filteredStudent.setName(student.getName());
                        filteredStudent.setGender(student.getGender());
                        filteredStudent.setPhone(student.getPhone());
                        filteredStudent.setEmail(student.getEmail());
                        filteredStudent.setClassId(student.getClassId());
                        filteredStudent.setEnrollmentDate(student.getEnrollmentDate());
                        filteredStudent.setStatus(student.getStatus());

                        // 如果是自己，可以看到完整信息（包括敏感信息）
                        if (username.equals(student.getStudentNo())) {
                            filteredStudent.setBirthDate(student.getBirthDate());
                            filteredStudent.setIdCard(student.getIdCard());
                            filteredStudent.setAddress(student.getAddress());
                            filteredStudent.setGraduationDate(student.getGraduationDate());
                            filteredStudent.setStudentType(student.getStudentType());
                            filteredStudent.setEnrollmentType(student.getEnrollmentType());
                            filteredStudent.setTotalCredits(student.getTotalCredits());
                            filteredStudent.setGpa(student.getGpa());
                            filteredStudent.setCreateTime(student.getCreateTime());
                            filteredStudent.setUpdateTime(student.getUpdateTime());
                        }
                        // 对于同学，不设置敏感信息（保持null）

                        filteredStudents.add(filteredStudent);
                    }
                }
            }
            return ResponseEntity.ok(filteredStudents);
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Integer id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        String username = (String) request.getAttribute("username");

        log.debug("获取学生详情，ID={}，当前用户角色={}，用户名={}", id, role, username);

        Student student = studentService.findById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }

        // 权限检查
        if ("ADMIN".equals(role) || "TEACHER".equals(role)) {
            // 管理员和教师可以查看所有学生详情
            return ResponseEntity.ok(student);
        } else if ("STUDENT".equals(role)) {
            // 学生只能查看自己的详情
            if (username.equals(student.getStudentNo())) {
                // 隐藏敏感信息
                Student filteredStudent = new Student();
                filteredStudent.setId(student.getId());
                filteredStudent.setStudentNo(student.getStudentNo());
                filteredStudent.setName(student.getName());
                filteredStudent.setGender(student.getGender());
                filteredStudent.setPhone(student.getPhone());
                filteredStudent.setEmail(student.getEmail());
                filteredStudent.setClassId(student.getClassId());
                filteredStudent.setEnrollmentDate(student.getEnrollmentDate());
                filteredStudent.setStatus(student.getStatus());
                return ResponseEntity.ok(filteredStudent);
            } else {
                return ResponseEntity.status(403).build(); // 403 Forbidden
            }
        } else {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> addStudent(@RequestBody Student student, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        log.debug("添加学生: {}，当前用户角色={}", student, role);

        // 只有管理员和教师可以添加学生
        if (!"ADMIN".equals(role) && !"TEACHER".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "权限不足，只有管理员和教师可以添加学生"));
        }

        int result = studentService.save(student);
        if (result > 0) {
            return ResponseEntity.ok(student);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Integer id, @RequestBody Student student, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        String username = (String) request.getAttribute("username");

        log.debug("更新学生，ID={}，数据={}，当前用户角色={}，用户名={}", id, student, role, username);

        // 获取原学生信息
        Student existingStudent = studentService.findById(id);
        if (existingStudent == null) {
            return ResponseEntity.notFound().build();
        }

        // 权限检查
        if ("ADMIN".equals(role) || "TEACHER".equals(role)) {
            // 管理员和教师可以更新所有学生信息
            student.setId(id);
            int result = studentService.update(student);
            if (result > 0) {
                return ResponseEntity.ok(student);
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else if ("STUDENT".equals(role)) {
            // 学生只能更新自己的信息，并且只能更新部分字段
            if (username.equals(existingStudent.getStudentNo())) {
                // 学生可以更新的字段：电话、邮箱、出生日期、身份证号、家庭住址
                existingStudent.setPhone(student.getPhone());
                existingStudent.setEmail(student.getEmail());
                existingStudent.setBirthDate(student.getBirthDate());
                existingStudent.setIdCard(student.getIdCard());
                existingStudent.setAddress(student.getAddress());
                existingStudent.setId(id);

                int result = studentService.update(existingStudent);
                if (result > 0) {
                    return ResponseEntity.ok(existingStudent);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                return ResponseEntity.status(403).body(Map.of("message", "权限不足，只能修改自己的信息"));
            }
        } else {
            return ResponseEntity.status(403).body(Map.of("message", "权限不足"));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");

        log.debug("删除学生，ID={}，当前用户角色={}", id, role);

        // 只有管理员可以删除学生
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body(Map.of("message", "权限不足，只有管理员可以删除学生"));
        }

        int result = studentService.deleteById(id);
        if (result > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam(required = false) String studentNo,
                                       @RequestParam(required = false) String name,
                                       HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        String username = (String) request.getAttribute("username");

        log.debug("搜索学生，学号={}，姓名={}，当前用户角色={}，用户名={}", studentNo, name, role, username);

        List<Student> students = studentService.queryStudents(studentNo, name);

        // 根据角色过滤数据
        if ("ADMIN".equals(role) || "TEACHER".equals(role)) {
            // 管理员和教师可以搜索所有学生
            return ResponseEntity.ok(students);
        } else if ("STUDENT".equals(role)) {
            // 学生只能搜索到自己的信息
            List<Student> filteredStudents = new ArrayList<>();
            for (Student student : students) {
                if (username.equals(student.getStudentNo())) {
                    // 隐藏敏感信息
                    Student filteredStudent = new Student();
                    filteredStudent.setId(student.getId());
                    filteredStudent.setStudentNo(student.getStudentNo());
                    filteredStudent.setName(student.getName());
                    filteredStudent.setGender(student.getGender());
                    filteredStudent.setPhone(student.getPhone());
                    filteredStudent.setEmail(student.getEmail());
                    filteredStudent.setClassId(student.getClassId());
                    filteredStudent.setEnrollmentDate(student.getEnrollmentDate());
                    filteredStudent.setStatus(student.getStatus());
                    filteredStudents.add(filteredStudent);
                    break;
                }
            }
            return ResponseEntity.ok(filteredStudents);
        } else {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
