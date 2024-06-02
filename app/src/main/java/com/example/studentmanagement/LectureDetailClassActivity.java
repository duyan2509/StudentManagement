package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.studentmanagement.Adapter.ClassAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class LectureDetailClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lecture_detail_class);

        // Lấy ID của lớp học từ Intent
        String classID = getIntent().getStringExtra("classID");

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

        // Thực hiện truy vấn để lấy dữ liệu của lớp học từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("course").document(classID);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Lấy dữ liệu từ DocumentSnapshot
                    String classCodeAndName = documentSnapshot.getString("code") + " - " + documentSnapshot.getString("name");
                    String classLecture = documentSnapshot.getString("lecture");
                    String classTime = Objects.requireNonNull(documentSnapshot.getLong("start")).toString() +"-"+Objects.requireNonNull(documentSnapshot.getLong("end")).toString()+", " + documentSnapshot.getString("schedule");

                    TextView classCodeView = findViewById(R.id.class_code_and_name);
                    TextView classLectureView = findViewById(R.id.class_lecture);
                    TextView classTimeView = findViewById(R.id.class_time);

                    classCodeView.setText(classCodeAndName);
                    classLectureView.setText(classLecture);
                    classTimeView.setText(classTime);
                } else {
                    Log.d("LectureDetailClassActivity", "Không tìm thấy lớp học với ID: " + classID);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LectureDetailClassActivity", "Lỗi khi lấy dữ liệu lớp học: " + e.getMessage());
            }
        });

//        TextView classCodeView = findViewById(R.id.class_code_and_name);
//        TextView classLectureView = findViewById(R.id.class_lecture);
//        TextView classTimeView = findViewById(R.id.class_time);
//
//        classCodeView.setText(classCodeAndName);
//        classLectureView.setText(classLecture);
//        classTimeView.setText(classTime);

    }
    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.detail_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}