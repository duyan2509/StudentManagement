package com.example.studentmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LectureAssignmentActivity extends AppCompatActivity {

    private String title;
    private String time;
    private String description;
    String classID;
    String assignmentID;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lecture_assignment);
        Log.d("Activity: ", "Lecture Assignment Activity");

        classID = getIntent().getStringExtra("classID");
        assignmentID = getIntent().getStringExtra("assignmentId");

        TextView tvTitle = findViewById(R.id.deadline_name);
        TextView tvDescription = findViewById(R.id.deadline_description);
        TextView tvTime = findViewById(R.id.deadline_time);

        title = getIntent().getStringExtra("title");
        time = getIntent().getStringExtra("date");
        description = getIntent().getStringExtra("description");

        tvTitle.setText(title);
        tvTime.setText(time);
        tvDescription.setText(description);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            finish();
        });

        // Thêm sự kiện khi ấn vào textview_see_detail
        TextView textViewSeeDetail = findViewById(R.id.textview_see_detail);
        textViewSeeDetail.setOnClickListener(v -> {
            Intent intent = new Intent(LectureAssignmentActivity.this, LectureDetailSubmissionActivity.class);
            intent.putExtra("assignmentId", assignmentID);
            intent.putExtra("classID", classID);
            startActivity(intent);
        });


        textViewSeeDetail.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_HOVER_ENTER:
                case MotionEvent.ACTION_DOWN:
                    textViewSeeDetail.setPaintFlags(textViewSeeDetail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                case MotionEvent.ACTION_UP:
                    textViewSeeDetail.setPaintFlags(textViewSeeDetail.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                    break;
            }
            return false;
        });
    }

    private String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}