package com.example.studentmanagement;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.SubmissionAdapter;
import com.example.studentmanagement.Model.StudentSubmission;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.ArrayList;

public class LectureDetailSubmissionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubmissionAdapter adapter;
    private FirebaseFirestore db;
    private String classID, assignmentID, classCode;
    private EditText searchEditText;
    private TextView tvTitle;
    private String title, time, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_detail_submission);

        Log.d("Activity: ", "Lecture Detail Submission Activity");

        recyclerView = findViewById(R.id.submission_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        classID = getIntent().getStringExtra("classID");
        classCode = getIntent().getStringExtra("classCode");
        assignmentID = getIntent().getStringExtra("assignmentId");

        title = getIntent().getStringExtra("title");
        time = getIntent().getStringExtra("date");
        description = getIntent().getStringExtra("description");

        Log.d("Tag", classID + " - " + assignmentID);

        searchEditText = findViewById(R.id.searchEditText);
        tvTitle = findViewById(R.id.title);
        tvTitle.setText(title);

        db = FirebaseFirestore.getInstance();
        loadStudentSubmissions();

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Thêm TextWatcher để lắng nghe sự thay đổi văn bản
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì ở đây
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gọi phương thức filter của adapter với văn bản tìm kiếm
                if (adapter != null) {
                    adapter.filter(s.toString());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì ở đây
            }
        });
    }

    private void loadStudentSubmissions() {
        Log.d("TAG", "1");
        if ((classID != null) && (assignmentID != null)) {
            db.collection("course").document(classID)
                    .collection("assignment").document(assignmentID)
                    .collection("submission")
                    .get()
                    .addOnCompleteListener(task -> {
                        Log.d("TAG", "2");
                        if (task.isSuccessful()) {
                            Log.d("TAG", "3");
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            if (documents.isEmpty())
                                Log.d("TAG", "3.5");
                            List<String> studentSubmittedIDs = new ArrayList<>();
                            for (DocumentSnapshot document : documents) {
                                String studentSubmittedID = document.getString("student_id");
                                Log.d("TAG", "3.7 " + studentSubmittedID);
                                if (studentSubmittedID != null && !studentSubmittedIDs.contains(studentSubmittedID)) {
                                    studentSubmittedIDs.add(studentSubmittedID);
                                }
                            }
                            Log.d("TAG", "4");
                            loadStudentData(studentSubmittedIDs);
                            Log.d("TAG", "13");
                        } else {
                            Log.d("SubmissionActivity", "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void loadStudentData(List<String> studentSubmittedIDs) {
        Log.d("TAG", "5");
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
                        updateUI(studentIDs, studentSubmittedIDs);
                    } else {
                        Log.d("SubmissionActivity", "Error getting user_course documents: ", task.getException());
                    }
                });
    }

    private void updateUI(@NonNull List<String> studentIDs, List<String> studentSubmittedIDs) {
        Log.d("TAG", "10");
        List<StudentSubmission> submissions = new ArrayList<>();
        for (int i = 0; i < studentIDs.size(); i++) {
            String status = studentSubmittedIDs.contains(studentIDs.get(i)) ? "view assignment" : "no submission";
            db.collection("user").document(studentIDs.get(i)).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            submissions.add(new StudentSubmission(document.getId(), document.getString("code"), document.getString("name"), status));
                            Log.d("TAG", "11");
                            adapter = new SubmissionAdapter(submissions, this, title, time, description, classID, assignmentID, classCode);
                            recyclerView.setAdapter(adapter);
                            Log.d("TAG", "12");
                        } else {
                            Log.d("SubmissionActivity", "Error getting user documents: ", task.getException());
                        }
                    });
        }
    }
}
