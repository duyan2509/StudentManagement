package com.example.studentmanagement.Model;

public class UserCourse {
    String course_id;
    String role;
    String user_id;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    String user_name;

    public UserCourse() {
    }
    public UserCourse(String userId, String courseId, String role) {
        this.user_id = userId;
        this.course_id = courseId;
        this.role = role;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
