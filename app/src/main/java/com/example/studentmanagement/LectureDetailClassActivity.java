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

        boolean LectureDetailClassAssignmentFragment = getIntent().getBooleanExtra("show_fragment_lecture_detail_class_assignment", false);
        if (savedInstanceState == null) {
            Fragment initialFragment = LectureDetailClassAssignmentFragment ? new LectureDetailClassAssignmentFragment() : new LectureDetailClassAssignmentFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, initialFragment).commitAllowingStateLoss();
        }

        // Hiển thị fragment LectureDetailClassAssignmentFragment
//        if (savedInstanceState == null) {
//            LectureDetailClassAssignmentFragment fragment = new LectureDetailClassAssignmentFragment();
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace(R.id.detail_container, fragment);
//            fragmentTransaction.commit();
//        }

        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang MyClassActivity
                Intent intent = new Intent(LectureDetailClassActivity.this, UserActivity.class);
                intent.putExtra("show_fragment_my_class", true);
                startActivity(intent);
                finish();
            }
        });
    }
}