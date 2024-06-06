package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentmanagement.Adapter.PersonAdapter;
import com.example.studentmanagement.Domain.UserCourseRepository;
import com.example.studentmanagement.Model.UserCourse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClassListActivity extends AppCompatActivity {
    String codeName, courseId;
    TextView code;
    androidx.appcompat.widget.SearchView searchPerson;
    androidx.recyclerview.widget.RecyclerView rcvPerson;
    ImageView btn_back;
    PersonAdapter personAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_class_list);

        Intent intent = getIntent();
        codeName = intent.getStringExtra("codeName");
        courseId = intent.getStringExtra("courseId");
        Log.i("codeName",codeName);
        Log.i("courseId",courseId);


        setWidget();
        code.setText(codeName);
        setClassListView();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void setClassListView() {
        if(courseId==null)
            return;

        UserCourseRepository userCourseRepository = new UserCourseRepository();
        userCourseRepository.getUserCoursesByCourseId(courseId).addOnCompleteListener(new OnCompleteListener<List<UserCourse>>() {
            @Override
            public void onComplete(@NonNull Task<List<UserCourse>> task) {
                List<UserCourse> userCourseList=new ArrayList<>();

                if(task.isSuccessful()){
                    userCourseList=task.getResult();
                    PersonAdapter personAdapter = new PersonAdapter(sortUserCourseList(userCourseList));
                    personAdapter.setContext(ClassListActivity.this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ClassListActivity.this);
                    rcvPerson.setAdapter(personAdapter);
                    rcvPerson.setLayoutManager(linearLayoutManager);

                    searchPerson.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            personAdapter.getFilter().filter(query);
                            return false;
                        };

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            personAdapter.getFilter().filter(newText);
                            return false;
                        }
                    });
                }
                else
                    Log.i("fetch user course by course id","fail");
            }
        });
    }
    List<UserCourse> sortUserCourseList(List<UserCourse> userCourseList){
        Comparator<UserCourse> comparator = (uc1, uc2) -> {
            if (uc1.getRole().equals("lecturer") && uc2.getRole().equals("student")) {
                return -1;
            } else if (uc1.getRole().equals("student") && uc2.getRole().equals("lecturer")) {
                return 1;
            } else {
                return 0;
            }
        };
        Collections.sort(userCourseList, comparator);

        return userCourseList;
    };

    private void setWidget() {
        code = findViewById(R.id.code);
        searchPerson=findViewById(R.id.searchPerson);
        rcvPerson = findViewById(R.id.rcvPerson);
        btn_back = findViewById(R.id.btn_back);
    }
}