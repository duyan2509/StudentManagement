package com.example.studentmanagement.Model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Assignment {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AttachedFile> getAttached_files() {
        return attached_files;
    }

    public void setAttached_files(List<AttachedFile> attached_files) {
        this.attached_files = attached_files;
    }

    String id;
    Timestamp due_date;
    String title;
    String description;
    List<Submission> submissions;
    List<AttachedFile> attached_files;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    String courseId;

    public Timestamp getDue_date() {
        return due_date;
    }

    public void setDue_date(Timestamp due_date) {
        this.due_date = due_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }

    public List<AttachedFile> getAttached_file() {
        return attached_files;
    }

    public void setAttached_file(List<AttachedFile> attached_file) {
        this.attached_files = attached_file;
    }

    public Assignment() {
    }
}
