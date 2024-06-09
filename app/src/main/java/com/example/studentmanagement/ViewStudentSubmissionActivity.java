package com.example.studentmanagement;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.DocumentAdapter;
import com.example.studentmanagement.Adapter.SubmitItemAdapter;
import com.example.studentmanagement.Model.SubmitItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ViewStudentSubmissionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<SubmitItem> fileQueue = new ArrayList<>();
    private final List<StorageReference> filesAndFolders = new ArrayList<>();
    private String title, time, description, classID, assignmentID, studentID, classCode;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_student_submission);

        db = FirebaseFirestore.getInstance();

        classID = getIntent().getStringExtra("classID");
        classCode = getIntent().getStringExtra("classCode");
        assignmentID = getIntent().getStringExtra("assignmentID");
        studentID = getIntent().getStringExtra("studentId");
        title = getIntent().getStringExtra("title");
        time = getIntent().getStringExtra("time");
        description = getIntent().getStringExtra("description");
        Log.d("TAG", classID + classCode + assignmentID + studentID + time + title + description);

        TextView deadlineNameTextView = findViewById(R.id.deadline_name);
        TextView deadlineTimeTextView = findViewById(R.id.deadline_time);
        TextView deadlineDescriptionTextView = findViewById(R.id.deadline_description);
        deadlineNameTextView.setText(title);
        deadlineTimeTextView.setText(time);
        deadlineDescriptionTextView.setText(description);

        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DocumentAdapter(filesAndFolders, this, item -> {
            if (item.getName().contains(".")) {
                // Handle file click
                Toast.makeText(this, "File: " + item.getName(), Toast.LENGTH_SHORT).show();
            } else {
                // Handle folder click, load folder contents
                loadFolderContents(item);
            }
        });
        recyclerView.setAdapter(adapter);

        loadFolderContents(FirebaseStorage.getInstance().getReference(classCode).child("Assignment").child(studentID));
    }

    private void loadFolderContents(StorageReference reference) {
        Log.d("",reference.getName());
        reference.listAll().addOnSuccessListener(listResult -> {
            filesAndFolders.clear();
            filesAndFolders.addAll(listResult.getItems());
            filesAndFolders.addAll(listResult.getPrefixes());
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(this, "Failed to load folder contents", Toast.LENGTH_SHORT).show();
        });
        adapter.notifyDataSetChanged();
    }
}