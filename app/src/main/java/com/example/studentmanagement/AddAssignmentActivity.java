package com.example.studentmanagement;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;

public class AddAssignmentActivity extends AppCompatActivity {

    Button btnSelectDate, btnSelectTime, btnAttachFile;
    EditText etSelectedDate, etSelectedTime;
    private ActivityResultLauncher<String> filePickerLauncher;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_assignment);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "upload_channel";
            String channelName = "File Uploads";
            String channelDescription = "Notifications for file uploads";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        etSelectedDate = findViewById(R.id.etSelectedDate);
        etSelectedTime = findViewById(R.id.etSelectedTime);
        btnAttachFile = findViewById(R.id.btn_attach_file);

        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddAssignmentActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        etSelectedDate.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddAssignmentActivity.this,
                    (view, hourOfDay, minute1) -> {
                        String selectedTime = hourOfDay + ":" + minute1;
                        etSelectedTime.setText(selectedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        Button btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            String classID = getIntent().getStringExtra("classID");
            Intent intent = new Intent(AddAssignmentActivity.this, LectureDetailClassActivity.class);
            intent.putExtra("show_fragment_lecture_detail_class_assignment", true);
            intent.putExtra("classID", classID);
            startActivity(intent);
            finish();
        });

        db = FirebaseFirestore.getInstance();

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String classID = getIntent().getStringExtra("classID");
                        db.collection("course").document(classID).get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String classCode = documentSnapshot.getString("code");
                                if (classCode != null) {
                                    uploadFileToFirebase(uri, classCode);
                                } else {
                                    Log.d("LectureDetailClassActivity", "classCode is null");
                                }
                            } else {
                                Log.d("LectureDetailClassActivity", "Không tìm thấy lớp học với ID: " + classID);
                            }
                        }).addOnFailureListener(e -> {
                            Log.d("LectureDetailClassActivity", "Lỗi khi lấy dữ liệu lớp học: " + e.getMessage());
                        });
                    }
                });

        btnAttachFile.setOnClickListener(v -> {
            String classID = getIntent().getStringExtra("classID");
            Log.d("Debug", "ClassID = " + classID);
            if (classID != null) {
                filePickerLauncher.launch("*/*");
            } else {
                Log.d("LectureDetailClassActivity", "classID is null");
            }
        });
    }

    private void uploadFileToFirebase(Uri fileUri, String code) {
        String fileName = getFileName(fileUri);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(code);
        StorageReference fileRef = storageRef.child("Assignment/" + fileName);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "upload_channel")
                .setSmallIcon(R.drawable.ic_upload)
                .setContentTitle("Uploading File")
                .setContentText("Upload in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        int notificationId = 1;

        fileRef.putFile(fileUri)
                .addOnProgressListener(taskSnapshot -> {
                    // Update progress if needed
                })
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("Debug", "Add Assignment Done");
                    notificationBuilder.setContentText("Upload complete")
                            .setProgress(0, 0, false);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                })
                .addOnFailureListener(e -> {
                    Log.d("Debug", "Add Assignment False");
                    notificationBuilder.setContentText("Upload failed")
                            .setProgress(0, 0, false);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                });
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            assert uri != null;
            result = uri.getPath();
            assert result != null;
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
