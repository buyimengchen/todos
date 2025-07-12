package com.example.university_backend.service.impl;

import com.example.university_backend.entity.Student;
import com.example.university_backend.mapper.StudentMapper;
import com.example.university_backend.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<Student> findAll() {
        return studentMapper.findAll();
    }

    @Override
    public Student findById(Integer id) {
        return studentMapper.findById(id);
    }

    @Override
    public int save(Student student) {
        return studentMapper.insert(student);
    }

    @Override
    public int update(Student student) {
        return studentMapper.update(student);
    }

    @Override
    public int deleteById(Integer id) {
        return studentMapper.deleteById(id);
    }

    @Override
    public List<Student> queryStudents(String studentNo, String name) {
        return studentMapper.queryStudents(studentNo, name);
    }
}
