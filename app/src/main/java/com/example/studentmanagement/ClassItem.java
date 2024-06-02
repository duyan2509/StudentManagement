package com.example.studentmanagement;

public class ClassItem {
    private final String classID;
    private final String classCode;
    private final String className;
    private final String classLecture;
    private final String classTime;

    public ClassItem(String classID, String classCode, String className, String classLecture, String classTime) {
        this.classID = classID;
        this.classCode = classCode;
        this.className = className;
        this.classLecture = classLecture;
        this.classTime = classTime;
    }

    public String getClassID() {
        return classID;
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
