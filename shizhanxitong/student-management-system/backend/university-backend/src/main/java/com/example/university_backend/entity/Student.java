package com.example.university_backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Student {
    private Integer id;
    private String studentNo;
    private String name;
    private String gender;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthDate;
    
    private String idCard;
    private String phone;
    private String email;
    private String address;
    private Integer classId;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date enrollmentDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date graduationDate;
    
    private String studentType;
    private String enrollmentType;
    private Integer status;
    private Double totalCredits;
    private Double gpa;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
