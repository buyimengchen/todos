package com.example.university_backend.mapper;

import com.example.university_backend.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {
    
    List<Student> findAll();
    
    Student findById(@Param("id") Integer id);
    
    int insert(Student student);
    
    int update(Student student);
    
    int deleteById(@Param("id") Integer id);
    
    List<Student> queryStudents(@Param("studentNo") String studentNo, @Param("name") String name);
}
