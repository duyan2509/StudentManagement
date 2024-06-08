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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.SubmissionAdapter;
import com.example.studentmanagement.Model.StudentSubmission;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class LectureDetailSubmissionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubmissionAdapter adapter;
    private FirebaseFirestore db;
    private String classID, assignmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail_submission);

        Log.d("Activity: ", "Lecture Detail Submission Activity");

        recyclerView = findViewById(R.id.submission_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        classID = getIntent().getStringExtra("classID");
        assignmentID = getIntent().getStringExtra("assignmentId");
        Log.d("Tag", classID + " - " + assignmentID);

        db = FirebaseFirestore.getInstance();
        loadStudentSubmissions();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void loadStudentSubmissions() {
        Log.d("TAG", "1");
        if((classID != null) && (assignmentID != null)){
            db.collection("course").document(classID)
                    .collection("assignment").document(assignmentID)
                    .collection("submission")
                    .get()
                    .addOnCompleteListener(task -> {
                        Log.d("TAG", "2");
                        if (task.isSuccessful()) {
                            Log.d("TAG", "3");
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if(documents.isEmpty())
                                Log.d("TAG", "3.5");
                            List<String> studentSubmittedID = new ArrayList<>();
                            for (DocumentSnapshot document : documents) {
                                String studentSubmissedID = document.getString("student_id");
                                Log.d("TAG", "3.7 " + studentSubmissedID);
                                if (studentSubmissedID != null && !studentSubmittedID.contains(studentSubmissedID)) {
                                    studentSubmittedID.add(studentSubmissedID);
                                }
                            }
                            Log.d("TAG", "4");
                            loadStudentData(studentSubmittedID);
                            Log.d("TAG", "13");
                        } else {
                            Log.d("SubmissionActivity", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void loadStudentData(List<String> studentSubmittedID) {
        Log.d("TAG", "5");
        List<String> studentNames = new ArrayList<>();
        List<String> studentCodes = new ArrayList<>();
        List<String> studentIDs = new ArrayList<>();

        db.collection("user_course")
                .whereEqualTo("course_id", classID)
                .whereEqualTo("role", "student")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String userID = document.getString("user_id");
                            if (userID != null && !studentIDs.contains(userID)) {
                                studentIDs.add(userID);
                            }
                        }
                        // Gọi phương thức lấy thông tin sinh viên sau khi lấy danh sách studentIDs thành công
                        retrieveStudentInfo(studentIDs, studentSubmittedID, studentNames, studentCodes);
                    } else {
                        Log.d("SubmissionActivity", "Error getting user_course documents: ", task.getException());
                    }
                });
    }

    private void retrieveStudentInfo(List<String> studentIDs, List<String> studentSubmittedID, List<String> studentNames, List<String> studentCodes) {
        for (String studentID : studentIDs) {
            db.collection("user").document(studentID)
                    .get()
                    .addOnCompleteListener(task -> {
                        Log.d("TAG", "6");
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d("TAG", "7");
                            if (document.exists()) {
                                Log.d("TAG", "8");
                                String studentName = document.getString("name");
                                String studentCode = document.getString("code");
                                studentNames.add(studentName != null ? studentName : "Unknown");
                                studentCodes.add(studentCode != null ? studentCode : "Unknown");
                                // Kiểm tra xem tất cả dữ liệu đã được lấy chưa
                                if (studentNames.size() == studentIDs.size() && studentCodes.size() == studentIDs.size()) {
                                    updateUI(studentIDs, studentSubmittedID, studentCodes, studentNames);
                                }
                            }
                            Log.d("TAG", "9");
                        } else {
                            Log.d("SubmissionActivity", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void updateUI(List<String> studentIDs, List<String> studentSubmittedID, List<String> studentCodes, List<String> studentNames) {
        Log.d("TAG", "10");
        List<StudentSubmission> submissions = new ArrayList<>();
        for (int i = 0; i < studentIDs.size(); i++) {
            String status = studentSubmittedID.contains(studentIDs.get(i)) ? "view assignment" : "no submission";
            submissions.add(new StudentSubmission(studentIDs.get(i), studentCodes.get(i), studentNames.get(i), status));
        }
        Log.d("TAG", "11");
        adapter = new SubmissionAdapter(submissions, this);
        recyclerView.setAdapter(adapter);
        Log.d("TAG", "12");
    }
}