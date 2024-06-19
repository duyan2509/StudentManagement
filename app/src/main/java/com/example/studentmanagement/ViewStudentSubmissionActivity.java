package com.example.studentmanagement;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    Button save;
    com.google.android.material.textfield.TextInputEditText score;
    LinearLayout layout_save_score;

    @SuppressLint("MissingInflatedId")
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
        layout_save_score=findViewById(R.id.layout_save_score);
        save = findViewById(R.id.save);
        score = findViewById(R.id.score);
        db=FirebaseFirestore.getInstance();
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

        loadFolderContents(FirebaseStorage.getInstance().getReference(classCode).child("Assignment").child(title).child("Submission").child(studentID));

        setSaveScoreView();
    }

    private void setSaveScoreView() {
        db.collection("course").document(classID).collection("assignment").document(assignmentID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){

                Timestamp dueDate = task.getResult().getTimestamp("due_date");
                Log.d("Debug","Go to 1:"+isTimestampAfterCurrent(dueDate));
                if((dueDate != null) && !isTimestampAfterCurrent(dueDate)) {
                    setLayoutSave();
                    layout_save_score.setVisibility(View.GONE);
                    Log.d("Debug","Go to 1");
                } else {
                    layout_save_score.setVisibility(View.GONE);
                    Log.d("Debug","Go to 23");
                }
            }
        });
    }
    String initialScoreText, submissionId;

    private void setLayoutSave() {
        save.setEnabled(false);
        db.collection("course").document(classID).collection("assignment").document(assignmentID).collection("submission")
                .whereEqualTo("student_id",studentID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                submissionId=document.getId();
                                Long grade = document.getLong("grade");
                                if (grade != null) {
                                    initialScoreText = String.valueOf(grade);
                                    score.setText(String.valueOf(grade));
                                }
                                else
                                    initialScoreText="";
                                setViewScore();
                                layout_save_score.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    private void setViewScore() {
        score.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                save.setEnabled(!charSequence.toString().equals(initialScoreText));
                String newScoreText = score.getText().toString();
                if(charSequence.toString().isEmpty())
                {
                    save.setEnabled(false);
                    return;
                }
                Long newScore;
                try {
                    newScore = Long.parseLong(newScoreText);
                } catch (NumberFormatException e) {
                    newScore = null;
                }
                if(newScore!=null&&(newScore<0||newScore>10))
                {
                    save.setEnabled(false);
                    score.setError("Grade must in range 0-10");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setEnabled(false);
                String newScoreText = score.getText().toString();
                Long newScore;
                try {
                    newScore = Long.parseLong(newScoreText);
                } catch (NumberFormatException e) {
                    newScore = null;
                }

                if (newScore != null) {
                    db.collection("course").document(classID).collection("assignment").document(assignmentID).collection("submission")
                            .document(submissionId)
                            .update("grade", newScore)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        save.setEnabled(false);
                                        initialScoreText = score.getText().toString();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(ViewStudentSubmissionActivity.this,"Invalid grade", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isTimestampAfterCurrent(Timestamp timestamp) {
        if (timestamp == null) {
            return false;
        }
        return Timestamp.now().compareTo(timestamp) > 0;
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