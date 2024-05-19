package com.example.studentmanagement.Utils;

import android.content.Intent;

import com.example.studentmanagement.Model.Course;

public class AndroidUtil {
    public static void passCourseModelAsIntent(Intent intent, Course course){
        intent.putExtra("code", course.getCode());
        intent.putExtra("name", course.getName());
        intent.putExtra("id", course.getId());
    }

    public static Course getCourseModelAsIntent(Intent intent){
        Course courseModel = new Course();
        courseModel.setCode(intent.getStringExtra("code"));
        courseModel.setName(intent.getStringExtra("name"));
        courseModel.setId(intent.getStringExtra("id"));
        //courseModel.setFcmToken(intent.getStringExtra("fcmToken"));
        return courseModel;
    }
}
