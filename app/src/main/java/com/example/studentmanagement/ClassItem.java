package com.example.studentmanagement;

public class ClassItem {
    private String classCode;
    private String className;
    private String classLecture;
    private String classTime;

    public ClassItem(String classCode, String className, String classLecture, String classTime) {
        this.classCode = classCode;
        this.className = className;
        this.classLecture = classLecture;
        this.classTime = classTime;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getClassName() {
        return className;
    }

    public String getClassLecture() {
        return classLecture;
    }

    public String getClassTime() {
        return classTime;
    }
}
