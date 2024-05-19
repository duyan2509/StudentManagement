package com.example.studentmanagement.Model;

import com.google.firebase.Timestamp;

import java.util.List;

public class Course {
    String id;
    String code;
    String name;
    int start;
    int end;
    String schedule;
    int semester;
    String academic_year;
    String room;

    public Course(String id, String code, String name, int start, int end, String schedule, int semester, String academic_year, String room) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.start = start;
        this.end = end;
        this.schedule = schedule;
        this.semester = semester;
        this.academic_year = academic_year;
        this.room = room;
    }

    public Course() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
