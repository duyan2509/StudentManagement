package com.example.studentmanagement.Model;

import java.util.List;

public class Course {
    String academic_year;
    String code;
    String id;
    String name;
    String room;
    String schedule;
    long start;

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    List<Assignment> assignments;


    public Course() {
    }

    public Long getSemester() {
        return semester;
    }

    public void setSemester(Long semester) {
        this.semester = semester;
    }

    private Long semester;

    public String getAcademic_year() {
        return academic_year;
    }

    @Override
    public String toString() {
        return "Course{" +
                "academic_year='" + academic_year + '\'' +
                ", code='" + code + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", room='" + room + '\'' +
                ", schedule='" + schedule + '\'' +
                ", start=" + start +
                ", semester='" + semester + '\'' +
                ", end=" + end +
                '}';
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    private long end;


}
