package com.example.studentmanagement.Model;

import com.example.studentmanagement.Utils.RoleUtil;

public class User {
    private String id;
    private String email;
    private String role;

    public String getRole() {
        return role;
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
}
