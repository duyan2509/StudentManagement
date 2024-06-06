package com.example.studentmanagement.Model;

import com.google.firebase.Timestamp;

public class AssignmentItem {
    private String title;
    private Timestamp dueDate;
    private boolean isLate;
    private boolean isSubmittedLate;

    public AssignmentItem(String title, Timestamp dueDate) {
        this.title = title;
        this.dueDate = dueDate;
        this.isLate = false;
        this.isSubmittedLate = false;
    }

    public String getTitle() {
        return title;
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