package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class LectureDetailSubmissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Log.d("Activity: ", "Lecture Detail Submission Activity");
        setContentView(R.layout.activity_lecture_detail_submission);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            // Chuyển sang MyClassActivity
            Intent intent = new Intent(LectureDetailSubmissionActivity.this, LectureAssignmentActivity.class);
            startActivity(intent);
            finish();
        });

    }
}