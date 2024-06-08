package com.example.studentmanagement.Model;

import com.example.studentmanagement.Utils.RoleUtil;

public class User {
    private String id;
    private String email;

    private String name;
    private String role;

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    String profile_image;

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    String code ;


    public String getRole() {
        return role;
    }

    public User(){}

    public User(String id, String email, String name, String role) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public void setRole() {
        this.role = RoleUtil.getRole();
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPassword() {
        return getPassword();
    }

    public void setPassword(String newPassword) {

    }
}