package com.example.studentmanagement.Domain;

import android.util.Log;

import com.example.studentmanagement.Model.Course;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserCourseRepository {
    private FirebaseFirestore db;
    private static CollectionReference user_course;

    public UserCourseRepository() {
        db = FirebaseFirestore.getInstance();
        user_course = db.collection("user_course");
    }

    public Task<List<String>> getCourseIdByUserId(String userId) {
        return user_course
                .whereEqualTo("user_id", userId)
                .get()
                .continueWith(task -> {
                    List<String> courses = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String courseId = document.getString("course_id");
                                if (courseId != null) {
                                    courses.add(courseId);
                                }
                            }
                        }
                    }
                    return courses;
                });
    }
    public Task<List<Course>> getCourseByUserId(String userId) {
        return user_course
                .whereEqualTo("user_id", userId)
                .get()
                .continueWith(task -> {
                    List<Course> courses = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Course course = new Course();
                                course.setId(document.getString("course_id"));
                                course.setCode(document.getString("code"));
                                courses.add(course);
                            }
                        }
                    }
                    return courses;
                });
    }

    public Task<Map<String, String>> getLectureIdsBYCourseId(List<String> courseIds) {
        return user_course
                .whereEqualTo("role", "lecturer")
                .get()
                .continueWith(task -> {
                    Map<String, String> lectureIds = new HashMap<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String courseId = document.getString("course_id");
                                if(courseIds.contains(courseId)) {
                                    String lectureId = document.getString("user_id");
                                    lectureIds.put(courseId, lectureId);
                                }
                            }
                        }
                        else Log.i("user_course","null");
                    }
                    return lectureIds;
                });
    }
    public Task<Integer> getStudentCountByCourseId(String courseId) {
        return user_course
                .whereEqualTo("course_id", courseId)
                .whereEqualTo("role", "student")
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            return querySnapshot.size();
                        }
                    }
                    return 0;
                });
    }

}
