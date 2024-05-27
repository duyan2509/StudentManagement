package com.example.studentmanagement.Model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Submission {
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    String student_id;
    Timestamp submitted_at;
    List<AttachedFile> attached_file;

    public Submission() {
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public Timestamp getSubmitted_at() {
        return submitted_at;
    }

    public void setSubmitted_at(Timestamp submitted_at) {
        this.submitted_at = submitted_at;
    }

    public List<AttachedFile> getAttached_file() {
        return attached_file;
    }

    public void setAttached_file(List<AttachedFile> attached_file) {
        this.attached_file = attached_file;
    }
}
