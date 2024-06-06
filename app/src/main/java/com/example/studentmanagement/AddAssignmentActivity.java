package com.example.studentmanagement;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.AdapterView;
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
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.AccessController;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class AddAssignmentActivity extends AppCompatActivity {

    Button btnSelectDate, btnSelectTime, btnAttachFile, btnAddAssignment;
    EditText etSelectedDate, etSelectedTime,etDescription,title;
    private ActivityResultLauncher<String> filePickerLauncher;
    private FirebaseFirestore db;
    private Uri selectedFileUri;
    private String selectedClassCode;
    private String SelectDate,SelectTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_assignment);

        Log.d("Activity: ", "Add Assignment Activity");

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
        title = findViewById(R.id.AssignmentTitle);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        etSelectedDate = findViewById(R.id.etSelectedDate);
        etSelectedTime = findViewById(R.id.etSelectedTime);
        btnAttachFile = findViewById(R.id.btn_attach_file);
        btnAddAssignment = findViewById(R.id.btn_add_assignment);
        etDescription = findViewById(R.id.deadline_description);
        //initial Date And Time
        etSelectedDate.setText("");
        etSelectedTime.setText("");
        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AddAssignmentActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        SelectDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        etSelectedDate.setText(SelectDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddAssignmentActivity.this,
                    (view, hourOfDay, minute1) -> {
                        SelectTime = hourOfDay + ":" + minute1;
                        etSelectedTime.setText(SelectTime);
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
                                    selectedFileUri = uri;
                                    selectedClassCode = classCode;
                                    Log.d("AddAssignmentActivity", "Tệp đã được chọn và lưu tạm thời.");
                                } else {
                                    Log.d("AddAssignmentActivity", "classCode is null");
                                }
                            } else {
                                Log.d("AddAssignmentActivity", "Không tìm thấy lớp học với ID: " + classID);
                            }
                        }).addOnFailureListener(e -> {
                            Log.d("AddAssignmentActivity", "Lỗi khi lấy dữ liệu lớp học: " + e.getMessage());
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
        btnAddAssignment.setOnClickListener(v -> {
<<<<<<< HEAD

            Log.d("Get Date and Time",SelectDate + " - " + SelectTime);
=======
            Log.d("Get Date and Time",SelectDate+" - " + SelectTime);
>>>>>>> vu
            Log.d("GetUri and ClassCode",selectedFileUri+" - " + selectedClassCode);
            if (selectedFileUri != null && selectedClassCode != null && Check(SelectDate) && Check(selectedClassCode)) {

                //Upload To FireStore and Storage
                uploadFileToFirebase(selectedFileUri, selectedClassCode);
                String classID = getIntent().getStringExtra("classID");
                saveAssignmentDetailsToFirestore(selectedClassCode, SelectDate, SelectTime,classID);
                Intent intent = new Intent(AddAssignmentActivity.this, LectureDetailClassActivity.class);

                intent.putExtra("show_fragment_lecture_detail_class_assignment", true);
                intent.putExtra("classID", classID);
                startActivity(intent);
                finish();

            } else {
                Log.d("AddAssignmentActivity", "Chưa chọn tệp hoặc mã lớp học không hợp lệ.");

            }
        });

    }

    private void saveAssignmentDetailsToFirestore(String selectedClassCode, String selectDate, String selectTime,String classID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //String classID = getIntent().getStringExtra("classID");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Timestamp dueDateTimestamp = null;
        try {
            Date parsedDate = dateFormat.parse(selectDate + " " + selectTime);
            assert parsedDate != null;
            dueDateTimestamp = new Timestamp(parsedDate);
        } catch (ParseException e) {
            Log.e("Error", "Error parsing date and time: " + e);
        }

        // Create a new assignment object
        Map<String, Object> assignment = new HashMap<>();
        assignment.put("title", title.getText().toString()); // Thay đổi tiêu đề bài tập của bạn tại đây
        assignment.put("due_date", dueDateTimestamp); // Sử dụng Timestamp đã tạo
        assignment.put("description", etDescription.getText().toString());

        assert classID != null;
        db.collection("course").document(classID).collection("assignment").add(assignment)
                .addOnSuccessListener(documentReference -> Log.d("Debug", "Assignment added with ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.d("Debug", "Error adding assignment: " + e));
    }

    private boolean Check(String select) {
        return select != null && !select.isEmpty();
    }

    private void uploadFileToFirebase(Uri fileUri, String code) {
        String fileName = getFileName(fileUri);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(code);
        StorageReference fileRef = storageRef.child("Assignment/Date:"+convertstring(SelectDate)+"/Time:"+convertstring(SelectTime)+"/" + fileName);

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

    private String convertstring(String input) {
        if (input == null) {
            return null;
        }
        String out1 = input.replace("/", "_");
        return out1.replace(":", "_");
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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
