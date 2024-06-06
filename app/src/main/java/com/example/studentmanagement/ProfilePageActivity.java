package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.studentmanagement.Adapter.CourseRowAdapter;
import com.example.studentmanagement.Domain.CourseRepository;
import com.example.studentmanagement.Domain.UserCourseRepository;
import com.example.studentmanagement.Domain.UserRepository;
import com.example.studentmanagement.Model.User;
import com.example.studentmanagement.Model.UserCourse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class ProfilePageActivity extends AppCompatActivity {
    ImageView btBack,profilePic;
    androidx.recyclerview.widget.RecyclerView rcvClass;
    TextView role, email, name;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");


        setWidgets();
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(userId!=null) {
            setProfile();
            setClassList();
        }
    }

    private void setClassList() {
        if(userId==null)
            return;
        UserCourseRepository userCourseRepository=new UserCourseRepository();
        userCourseRepository.getUserCoursesByUserId(userId).addOnCompleteListener(new OnCompleteListener<List<UserCourse>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserCourse>> task) {
                if(task.isSuccessful()){
                    List<UserCourse> userCourseList = new ArrayList<>(task.getResult());
                    CourseRowAdapter courseRowAdapter= new CourseRowAdapter(userCourseList);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ProfilePageActivity.this);
                    rcvClass.setLayoutManager(linearLayoutManager);
                    rcvClass.setAdapter(courseRowAdapter);
                }
            }
        });

    }

    private void setProfile() {
        UserRepository userRepository=new UserRepository();
        userRepository.getUserById(userId).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                if(task.isSuccessful())
                {
                    User user=task.getResult();
                    if(user.getProfile_image()!=null)
                        Glide.with(ProfilePageActivity.this)
                                .load(user.getProfile_image())
                                .placeholder(R.drawable.baseline_account_circle_24)
                                .into(profilePic);
                    role.setText(user.getRole());
                    email.setText(user.getEmail());
                }
            }
        });
    }

    private void setWidgets() {
        btBack=findViewById(R.id.btBack);
        profilePic=findViewById(R.id.profilePic);
        rcvClass=findViewById(R.id.rcvClass);
        role=findViewById(R.id.role);
        email=findViewById(R.id.email);
        name=findViewById(R.id.name);
    }
}