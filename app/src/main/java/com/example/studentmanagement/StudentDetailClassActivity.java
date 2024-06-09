package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Utils.AndroidUtil;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class StudentDetailClassActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private static final int STUDENT_ASSIGNMENT_REQUEST_CODE = 1001;
    String codeName;
    ImageButton chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_detail_class);

        db = FirebaseFirestore.getInstance();
        String classID = getIntent().getStringExtra("classID");
        //String classID = getIntent().getStringExtra("classID");
        chat = findViewById(R.id.chat_button);
        chat.setOnClickListener(v -> {
            onChatClick(classID);
        });
        Log.d("TAG", "classID: " +  classID);
        Log.d("TAG", "Student detail class " + classID + " Activity");

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
                        TextView classStudentView = findViewById(R.id.class_lecture);
                        TextView classTimeView = findViewById(R.id.class_time);

                        classCodeView.setText(classCodeAndName);
                        classStudentView.setText(classLecture);
                        classTimeView.setText(classTime);

                        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
                        String classCode = documentSnapshot.getString("code");
                        checkAndCreateFolder(classCode);
                        boolean StudentDetailClassFragment = getIntent().getBooleanExtra("show_fragment_student_detail_class_assignment", false);
//                        Log.d("fragment", getIntent().getStringExtra("show_fragment_student_detail_class_assignment"));
                        if (savedInstanceState == null) {
                            Fragment initialFragment =StudentDetailClassFragment?new StudentDetailClassAssignmentFragment(classCode,documentSnapshot.getId()):new StudentDetailClassDocumentFragment(classCode,documentSnapshot.getId());

                            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, initialFragment).commitAllowingStateLoss();
                        }
                    } else {
                        Log.d("StudentDetailClassActivity", "Không tìm thấy lớp học với ID: " + classID);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("StudentDetailClassActivity", "Lỗi khi lấy dữ liệu lớp học: " + e.getMessage());
                }
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Xử lý button Back
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent(StudentDetailClassActivity.this, UserActivity.class);
            intent.putExtra("show_fragment_my_class", true);
            startActivity(intent);
//            finish();
        });
        //Xử Lý Button Document;
        //Xử Lý Button Assignment;
        Button btn_danh_sach_lop = findViewById(R.id.btn_danh_sach_lop);
        btn_danh_sach_lop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentDetailClassActivity.this, ClassListActivity.class);
                intent.putExtra("courseId", classID);
                intent.putExtra("codeName",codeName);
                startActivity(intent);
            }
        });
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
                    TextView classStudentView = findViewById(R.id.class_lecture);
                    TextView classTimeView = findViewById(R.id.class_time);

                    classCodeView.setText(classCodeAndName);
                    classStudentView.setText(classLecture);
                    classTimeView.setText(classTime);
                } else {
                    Log.d("StudentDetailClassActivity", "Không tìm thấy lớp học với ID: " + classID);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("StudentDetailClassActivity", "Lỗi khi lấy dữ liệu lớp học: " + e.getMessage());
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
    public void onChatClick(String courseId){
        DocumentReference courseRef = FirebaseUtil.getCourseById(courseId);
        courseRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String code = documentSnapshot.getString("code");
                String name = documentSnapshot.getString("name");

                Course course = new Course(courseId, code, name);
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                AndroidUtil.passCourseModelAsIntent(intent, course);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("StudentDetailClassActivity", "Lỗi khi lấy dữ liệu lớp học: " + e.getMessage());
            }
        });
    }

}
//helu