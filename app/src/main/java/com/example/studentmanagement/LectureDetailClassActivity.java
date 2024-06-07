package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import java.util.concurrent.atomic.AtomicReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LectureDetailClassActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    String codeName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lecture_detail_class);

        Log.d("Activity: ", "Lecture Detail Class Activity");

        db = FirebaseFirestore.getInstance();
        String classID = getIntent().getStringExtra("classID");
        Log.d("TAG", "classID: " +  classID);

        if (classID != null) {
            DocumentReference docRef = db.collection("course").document(classID);
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String classCodeAndName = documentSnapshot.getString("code") + " - " + documentSnapshot.getString("name");
                        String classLecture = documentSnapshot.getString("lecture");
                        String classTime = Objects.requireNonNull(documentSnapshot.getLong("start")).toString() + "-" + Objects.requireNonNull(documentSnapshot.getLong("end")).toString() + ", " + documentSnapshot.getString("schedule");
                        codeName=classCodeAndName;
                        TextView classCodeView = findViewById(R.id.class_code_and_name);
                        TextView classLectureView = findViewById(R.id.class_lecture);
                        TextView classTimeView = findViewById(R.id.class_time);

                        classCodeView.setText(classCodeAndName);
                        classLectureView.setText(classLecture);
                        classTimeView.setText(classTime);

                        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
                        String classCode = documentSnapshot.getString("code");
                        checkAndCreateFolder(classCode);
                        boolean LectureDetailClassFragment = getIntent().getBooleanExtra("show_fragment_lecture_detail_class_assignment", false);
                        if (savedInstanceState == null) {
                            Fragment initialFragment = LectureDetailClassFragment ? new LectureDetailClassAssignmentFragment(documentSnapshot.getId()) : new LectureDetailClassDocumentFragment(documentSnapshot.getId());

                            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, initialFragment).commitAllowingStateLoss();
                        }
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
        }

        // Lấy ID của lớp học từ Intent
//        String classID = getIntent().getStringExtra("classID");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Xử lý button Back
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
            intent.set(new Intent(LectureDetailClassActivity.this, UserActivity.class));
            intent.get().putExtra("show_fragment_my_class", true);
            startActivity(intent.get());
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

        Button btn_danh_sach_lop = findViewById(R.id.btn_danh_sach_lop);
        btn_danh_sach_lop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LectureDetailClassActivity.this, ClassListActivity.class);
                intent.putExtra("courseId", classID);
                intent.putExtra("codeName",codeName);
                startActivity(intent);
            }
        });
    }

    private void checkAndCreateFolder(String code) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    boolean folderExists = false;
                    for (StorageReference item : listResult.getPrefixes()) {
                        if (item.getName().equals(code)) {
                            folderExists = true;
                            break;
                        }
                    }

                    if (!folderExists) {
                        // Thư mục chưa tồn tại, tiến hành tạo thư mục mới
                        StorageReference classFolderRef = storageRef.child(code+"/"+code);
                        classFolderRef.putBytes(new byte[]{})
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Thư mục đã được tạo thành công
                                    Log.d("DEBUG: ", "Create Folder Done: " + code);
                                })
                                .addOnFailureListener(e -> {
                                    // Xảy ra lỗi khi tạo thư mục mới
                                    Log.d("DEBUG: ", "Create Folder Failed: " + code);
                                });
                    } else {
                        // Thư mục đã tồn tại
                        Log.d("DEBUG: ", "Folder " + code + " already exists");
                    }
                })
                .addOnFailureListener(exception -> {
                    // Xảy ra lỗi khi duyệt thư mục
                    Log.d("DEBUG: ", "Error Listing Folders ");
                });
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.detail_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}