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
    long end;
    Long semester;
    List<Assignment> assignments;

    public Course() {
    }

    public Course(String id, String code, String name){
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Course(String id, String code, String name, long start, long end, String schedule, Long semester, String academic_year, String room) {
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

    public Long getSemester() {
        return semester;
    }

    public void setSemester(Long semester) {
        this.semester = semester;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
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
                ", semester=" + semester +
                ", end=" + end +
                '}';
    }
}
