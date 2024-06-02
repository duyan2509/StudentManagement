package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LectureDetailClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lecture_detail_class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean LectureDetailClassFragment = getIntent().getBooleanExtra("show_fragment_lecture_detail_class_assignment", false);
        if (savedInstanceState == null) {
            Fragment initialFragment = LectureDetailClassFragment ? new LectureDetailClassAssignmentFragment() : new LectureDetailClassDocumentFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, initialFragment).commitAllowingStateLoss();
        }
        //Xử lý button Back
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {

            Intent intent = new Intent(LectureDetailClassActivity.this, UserActivity.class);
            intent.putExtra("show_fragment_my_class", true);
            startActivity(intent);
            finish();
        });
        //Xử Lý Button Document;
        //Xử Lý Button Assignment;

        String classCodeAndName = getIntent().getStringExtra("classCodeAndName");
        String classLecture = getIntent().getStringExtra("classLecture");
        String classTime = getIntent().getStringExtra("classTime");

        TextView classCodeView = findViewById(R.id.class_code_and_name);
        TextView classLectureView = findViewById(R.id.class_lecture);
        TextView classTimeView = findViewById(R.id.class_time);

        classCodeView.setText(classCodeAndName);
        classLectureView.setText(classLecture);
        classTimeView.setText(classTime);

    }
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.detail_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}