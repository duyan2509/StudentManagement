package com.example.studentmanagement.Model;

import com.google.firebase.Timestamp;

public class AssignmentItem {
    private String title,Class_id,Class_code;
    private Timestamp dueDate;
    private boolean isLate;
    private boolean isSubmittedLate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public AssignmentItem(String title, Timestamp dueDate,String id,String code, String assignmentId) {
        this.title = title;
        this.dueDate = dueDate;
        this.Class_id=id;
        this.Class_code=code;
        this.isLate = false;
        this.isSubmittedLate = false;
        this.id =assignmentId;
    }

    public String getTitle() {
        return title;
    }
    public String getClassID() {
        return Class_id;
    }

    public String getClass_code() {
        return Class_code;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getDueDate() {
        return dueDate;
    }

    public void setDueDate(Timestamp dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isLate() {
        return isLate;
    }

    public void setLate(boolean late) {
        isLate = late;
    }

    public boolean isSubmittedLate() {
        return isSubmittedLate;
    }

    public void setSubmittedLate(boolean submittedLate) {
        isSubmittedLate = submittedLate;
    }
}