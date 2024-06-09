package com.example.studentmanagement.Model;

public class StudentSubmission {
    private String studentID;
    private String studentCode;
    private String studentName;

    private String status;
    public StudentSubmission(String studentID, String studentCode, String studentName) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentCode = studentCode;
    }

    public StudentSubmission(String studentID, String studentCode, String studentName, String status) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentCode = studentCode;
        this.status = status;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getStudentName() {
        return studentName;
    }
    public String getStudentCode() {
        return studentCode;
    }
    public String getStatus() {
        return status;
    }
}
