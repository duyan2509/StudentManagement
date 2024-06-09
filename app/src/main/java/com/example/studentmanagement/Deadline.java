package com.example.studentmanagement;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.DeadlineAdapter;
import com.example.studentmanagement.Domain.CourseRepository;
import com.example.studentmanagement.Domain.UserCourseRepository;
import com.example.studentmanagement.Model.Assignment;
import com.example.studentmanagement.Model.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deadline extends AppCompatActivity {
    ImageView btBack;
    private androidx.recyclerview.widget.RecyclerView rcv_deadline;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deadline);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btBack = findViewById(R.id.btBack);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rcv_deadline=findViewById(R.id.rcv_deadline);
        setDeadlineView();
    }
    private void setDeadlineView() {
        Map<Assignment,String> assignmentStringMap=new HashMap<>();

        DeadlineAdapter deadlineAdapter=new DeadlineAdapter(assignmentStringMap);
        rcv_deadline.setAdapter(deadlineAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcv_deadline.setLayoutManager(linearLayoutManager);
        rcv_deadline.addItemDecoration(itemDecoration);
        UserCourseRepository userCourseRepository = new UserCourseRepository();

        userCourseRepository.getCourseByUserId(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(new OnCompleteListener<List<Course>>() {
            @Override
            public void onComplete(@NonNull Task<List<Course>> task) {
                List<Course> courses = task.getResult();
                Log.i("amount courses",String.valueOf(courses.size()));
                for (Course course:courses)
                {
                    CourseRepository courseRepository = new CourseRepository();
                    courseRepository.getCourseByCourseId(course.getId()).addOnCompleteListener(new OnCompleteListener<Course>() {
                        @Override
                        public void onComplete(@NonNull Task<Course> task) {
                            Course course1 = task.getResult();
                            courseRepository.getAssignmentByCourseId(course.getId()).addOnCompleteListener(new OnCompleteListener<List<Assignment>>() {
                                @Override
                                public void onComplete(@NonNull Task<List<Assignment>> task) {
                                    List<Assignment> assignments=task.getResult();
                                    for(Assignment assignment:assignments){
                                        Log.d("AssignmentData", "ID: " + assignment.getId() + ", Description: " + assignment.getDescription() + ", Title: " + assignment.getTitle() + ", Due Date: " + assignment.getDue_date());
                                        assignment.setCourseId(course1.getId());
                                        assignmentStringMap.put(assignment,course1.getCode());
                                    }
                                    deadlineAdapter.updateDate(assignmentStringMap);
                                }
                            });
                        }
                    });

                }
            }
        });
    }
}