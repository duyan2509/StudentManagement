package com.example.studentmanagement.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

    public static  void showToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
