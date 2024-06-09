package com.example.studentmanagement;

import static android.view.View.GONE;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.DocumentAdapter;
import com.example.studentmanagement.Adapter.SubmitItemAdapter;
import com.example.studentmanagement.Model.SubmitItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import com.google.firebase.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class StudentAssignmentActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_FILE_PICKER = 1;
    private static final int FILE_PICKER_REQUEST_CODE = 123;
    private RecyclerView recyclerView,recyclerView1;
    private SubmitItemAdapter adapter;
    private  DocumentAdapter adapter1;
    private final List<StorageReference> filesAndFolders = new ArrayList<>();
    private List<SubmitItem> fileQueue = new ArrayList<>();
    private String class_code;
    private String File_path;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Log.d("Activity: ", "Student Assignment Activity");

        setContentView(R.layout.activity_student_assignment);
        String deadlineName = getIntent().getStringExtra("deadline_name");
        String deadlineTime = getIntent().getStringExtra("deadline_time");
        String class_id = getIntent().getStringExtra("class_code");
        String code = getIntent().getStringExtra("class_id");
        String Get_Late=getIntent().getStringExtra("Get_Late");
        String Get_Type=getIntent().getStringExtra("Get_Type");
        Log.d("Description", class_id);
        TextView deadlineNameTextView = findViewById(R.id.deadline_name);
        TextView deadlineTimeTextView = findViewById(R.id.deadline_time);
        TextView deadlineDescriptionTextView = findViewById(R.id.deadline_description);
        deadlineNameTextView.setText(deadlineName);
        deadlineTimeTextView.setText(deadlineTime);

// Thiết lập lấy dữ liệu từ Firestore và cập nhật description

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Check Class ID", class_id);
        Log.d("Check Class ID", code);

        db.collection("course").document(class_id).collection("assignment")
                .whereEqualTo("title", deadlineName) // Tìm document có trường 'title' giống với 'deadlineName'
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            String description = snapshot.getString("description");
                            if (description != null) {
                                Log.d("Description", description);
                                deadlineDescriptionTextView.setText(description);
                                // Dừng vòng lặp sau khi tìm thấy mô tả đầu tiên
                                break;
                            }
                        }
                        // Nếu không tìm thấy mô tả, hiển thị thông báo
                        if (deadlineDescriptionTextView.getText().toString().isEmpty()) {
                            Log.d("Description", "Không có mô tả");
                            deadlineDescriptionTextView.setText("");
                        }
                    } else {
                        Log.d("Firestore", "Lỗi khi lấy document: ", task.getException());
                        deadlineDescriptionTextView.setText("Không có mô tả");
                    }
                });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v->{
            finish();
        });
        Button btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(v -> {
            // Mở picker file để chọn tập tin
            openFilePicker();
        });

        Button btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(v -> {
            // Upload các tập tin trong hàng đợi lên storage
            uploadSelectedFiles(class_id,code,deadlineName);
            Intent intent = new Intent(StudentAssignmentActivity.this, StudentDetailClassActivity.class);

            intent.putExtra("show_fragment_lecture_detail_class_assignment", true);
            intent.putExtra("classID", class_id);
            startActivity(intent);
            finish();finish();
        });
        if(Objects.equals(Get_Late, "Miss"))
        {
            btn_add.setVisibility(GONE);
            btn_save.setVisibility(GONE);
        }
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new SubmitItemAdapter(fileQueue, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView1 = findViewById(R.id.recyclerView1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView1.setLayoutManager(layoutManager);
        adapter1 = new DocumentAdapter(filesAndFolders, this, item -> {
            if (item.getName().contains(".")) {
                // Handle file click
                Toast.makeText(this, "File: " + item.getName(), Toast.LENGTH_SHORT).show();
            } else {
                // Handle folder click, load folder contents
                loadFolderContents(item);
            }
        });
        recyclerView1.setAdapter(adapter1);
        // Lấy đối tượng FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();

// Lấy người dùng hiện tại
        FirebaseUser currentUser = auth.getCurrentUser();
        assert currentUser != null;
        Log.d("Get UID",currentUser.getUid() + code+class_id);
        loadFolderContents(FirebaseStorage.getInstance().getReference(class_id).child("Assignment").child(currentUser.getUid()));
    }

    private void loadFolderContents(StorageReference reference) {
        Log.d("",reference.getName());
        reference.listAll().addOnSuccessListener(listResult -> {
            filesAndFolders.clear();
            filesAndFolders.addAll(listResult.getItems());
            filesAndFolders.addAll(listResult.getPrefixes());
            adapter1.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(this, "Failed to load folder contents", Toast.LENGTH_SHORT).show();
        });
        adapter1.notifyDataSetChanged();
    }


    private void openFilePicker() {
        // Mở trình chọn tập tin
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Cho phép chọn nhiều tập tin
        startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);
    }

    private void uploadSelectedFiles(String class_id,String class_code,String deadlineName) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        Log.d("Check User ID", userId+class_code);
        addFileInfoToFirestore(class_id,class_code, userId, deadlineName);
        
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(class_code+"/Assignment/"+userId);
        Log.d("Check User ID", storageRef.toString());
        for (SubmitItem submitItem : fileQueue) {
            Uri fileUri = submitItem.getFileUri();
            String fileName = submitItem.getFileName();

            // Tạo tham chiếu tới storage để lưu file
            StorageReference fileRef = storageRef.child(fileName);

            // Upload file lên storage
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Upload thành công
                        Log.d("Upload", "Upload successfully"+fileUri.toString());
                        addFileInfo(class_id,class_code, userId, deadlineName,submitItem);
                    })
                    .addOnFailureListener(e -> {
                        // Upload thất bại
                        Log.e("Upload", "Upload failed: " + e.getMessage());
                    });
        }
    }

    private void addFileInfo(String class_id, String classCode, String userId, String deadlineName,SubmitItem submitItem) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("student_id", userId);
        fileInfo.put("name", submitItem.getFileName());
        fileInfo.put("file_path", submitItem.getFileName());
        db.collection("course").document(class_id).collection("assignment")
                .whereEqualTo("title", deadlineName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("course").document(class_id)
                                    .collection("assignment").document(document.getId())
                                    .collection("submission").whereEqualTo("student_id", userId)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful()){
                                            for(QueryDocumentSnapshot document1: task1.getResult()){
                                                db.collection("course").document(class_id)
                                                        .collection("assignment").document(document.getId())
                                                        .collection("submission").document(document1.getId())
                                                        .collection("attached_file").add(fileInfo)
                                                        .addOnSuccessListener(documentReference ->
                                                                Log.d("Firestore", "File uploaded and added to Firestore"))
                                                        .addOnFailureListener(e ->
                                                                Log.e("Firestore", "Failed to add file info to Firestore: " + e.getMessage()));
                                            }
                                        }
                                    });
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void addFileInfoToFirestore(String class_id,String class_code, String userId, String deadlineName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> fileInfo = new HashMap<>();
        fileInfo.put("student_id", userId);
        fileInfo.put("submitted_at", FieldValue.serverTimestamp());
        db.collection("course").document(class_id).collection("assignment")
                .whereEqualTo("title", deadlineName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("course").document(class_id)
                                    .collection("assignment").document(document.getId())
                                    .collection("submission").add(fileInfo)
                                    .addOnSuccessListener(documentReference ->
                                            Log.d("Firestore", "File uploaded and added to Firestore"))
                                    .addOnFailureListener(e ->
                                            Log.e("Firestore", "Failed to add file info to Firestore: " + e.getMessage()));
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == RESULT_OK) {
            if (data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    // Nếu người dùng đã chọn nhiều tập tin
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri fileUri = clipData.getItemAt(i).getUri();
                        String fileName = getFileName(fileUri);
                        long fileSize = 10;

                        SubmitItem submitItem = new SubmitItem(fileName, fileSize, fileUri);
                        adapter.add(submitItem);
                    }
                } else {
                    // Nếu người dùng chỉ chọn một tập tin
                    Uri fileUri = data.getData();
                    String fileName = getFileName(fileUri);
                    long fileSize = 10;

                    SubmitItem submitItem = new SubmitItem(fileName, fileSize, fileUri);
                    adapter.add(submitItem);
                }

                // Sau khi chọn xong, cập nhật RecyclerView
                adapter.notifyDataSetChanged();
            }
        }
    }



    private String getFileName(Uri uri) {
        // Lấy tên file từ Uri
        String fileName = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return fileName;
    }

}
