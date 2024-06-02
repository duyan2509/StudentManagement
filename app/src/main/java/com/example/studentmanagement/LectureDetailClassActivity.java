package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.concurrent.atomic.AtomicReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LectureDetailClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lecture_detail_class);
        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        String code = intent.get().getStringExtra("code");
        String name = intent.get().getStringExtra("name");
        String lecture = intent.get().getStringExtra("lecture");
        String time = intent.get().getStringExtra("time");
        String code_text = code +"-"+name;
        // Hiển thị các dữ liệu  lên interface
        //
        TextView classNameTextView = findViewById(R.id.class_name);
        TextView classLectureTextView = findViewById(R.id.class_gv_data);
        TextView classTimeTextView = findViewById(R.id.class_time_data);

        //classCodeTextView.setText(code);
        classNameTextView.setText(code_text);
        classLectureTextView.setText(lecture);
        classTimeTextView.setText(time);
        checkAndCreateFolder(code);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        boolean LectureDetailClassFragment = getIntent().getBooleanExtra("show_fragment_lecture_detail_class_assignment", false);
        if (savedInstanceState == null) {
            Fragment initialFragment = LectureDetailClassFragment ? new LectureDetailClassAssignmentFragment(code) : new LectureDetailClassDocumentFragment(code);

            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, initialFragment).commitAllowingStateLoss();
        }
        //Xử lý button Back
        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {

            intent.set(new Intent(LectureDetailClassActivity.this, UserActivity.class));
            intent.get().putExtra("show_fragment_my_class", true);
            startActivity(intent.get());
            finish();
        });
        //Xử Lý Button Document;
        //Xử Lý Button Assignment;

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