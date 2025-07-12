package com.example.university_backend.service;

import com.example.university_backend.entity.Student;
import java.util.List;

public interface StudentService {
    List<Student> findAll();
    
    Student findById(Integer id);
    
    int save(Student student);
    
    int update(Student student);
    
    int deleteById(Integer id);
    
    List<Student> queryStudents(String studentNo, String name);
}
