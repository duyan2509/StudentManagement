package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class StudentDetailClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_student_detail_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean StudentDetailClassAssignmentFragment = getIntent().getBooleanExtra("show_fragment_student_detail_class_assignment", false);
        if (savedInstanceState == null) {
            Fragment initialFragment = StudentDetailClassAssignmentFragment ? new StudentDetailClassAssignmentFragment() : new StudentDetailClassAssignmentFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, initialFragment).commitAllowingStateLoss();
        }

        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang MyClassActivity
                Intent intent = new Intent(StudentDetailClassActivity.this, UserActivity.class);
                intent.putExtra("show_fragment_my_class", true);
                startActivity(intent);
                finish();
            }
        });
    }
}