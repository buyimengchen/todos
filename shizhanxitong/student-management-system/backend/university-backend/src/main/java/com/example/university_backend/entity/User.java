package com.example.university_backend.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String realName;  // 直接使用与数据库匹配的字段名
    private String email;
    private String phone;
    private String role;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
