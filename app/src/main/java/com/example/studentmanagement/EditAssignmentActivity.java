package com.example.studentmanagement;

import android.annotation.SuppressLint;
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
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EditAssignmentActivity extends AppCompatActivity {

    EditText etDescription, etSelectedDate, etSelectedTime, title;
    Button btnSelectDate, btnSelectTime, btnAttachFile, btnDone, btnBack;
    private ActivityResultLauncher<String> filePickerLauncher;
    private Uri selectedFileUri;
    private String assignmentId, classID, selectedClassCode, selectDate, selectTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_assignment);

        Log.d("TAG", "EditAssignmentActivity");

        etDescription = findViewById(R.id.etDescription);
        etSelectedDate = findViewById(R.id.etSelectedDate);
        etSelectedTime = findViewById(R.id.etSelectedTime);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnAttachFile = findViewById(R.id.btnAttachFile);
        btnDone = findViewById(R.id.btnDone);
        btnBack = findViewById(R.id.btn_back);
        title = findViewById(R.id.title);

        assignmentId = getIntent().getStringExtra("assignmentId");
        classID = getIntent().getStringExtra("classID");

        title.setText(getIntent().getStringExtra("title"));
        etSelectedDate.setText(getIntent().getStringExtra("date"));
        etSelectedTime.setText(getIntent().getStringExtra("date"));
        etDescription.setText(getIntent().getStringExtra("description"));

        Log.d("TAG", title.getText().toString() + etSelectedDate.getText() + etSelectedTime.getText() + etDescription.getText());

        btnBack.setOnClickListener(v -> finish());

        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditAssignmentActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        selectDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        etSelectedDate.setText(selectDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnSelectTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(EditAssignmentActivity.this,
                    (view, hourOfDay, minute1) -> {
                        selectTime = hourOfDay + ":" + minute1;
                        etSelectedTime.setText(selectTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedFileUri = uri;
                        Log.d("EditAssignmentActivity", "File selected: " + uri.toString());
                    }
                });

        btnAttachFile.setOnClickListener(v -> filePickerLauncher.launch("*/*"));

        btnDone.setOnClickListener(v -> {
            if (validateInputs()) {
                Log.d("Tag", "ready call updateDeadlineInFirestore for " + classID);
                updateDeadline();
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

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
    }

    private boolean validateInputs() {
        return !etDescription.getText().toString().trim().isEmpty() &&
                !etSelectedDate.getText().toString().trim().isEmpty() &&
                !etSelectedTime.getText().toString().trim().isEmpty();
    }

    private void updateDeadline() {
        Log.d("Tag", "1");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("Tag", "2");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Log.d("Tag", "Successfully connect db");
        Timestamp dueDateTimestamp = null;
        try {
            Date parsedDate = dateFormat.parse(selectDate + " " + selectTime);
            if (parsedDate != null) {
                dueDateTimestamp = new Timestamp(parsedDate);
            }
        } catch (ParseException e) {
            Log.e("EditAssignmentActivity", "Error parsing date and time: ", e);
        }
        Log.d("Tag", "Successfully check time " + dueDateTimestamp);
        if (dueDateTimestamp != null) {
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("due_date", dueDateTimestamp);
            if (!etDescription.getText().toString().isEmpty())
                updatedData.put("description", etDescription.getText().toString());
            if (!title.getText().toString().isEmpty())
                updatedData.put("title", title.getText().toString());
            if (!updatedData.isEmpty()){
                Log.d("Tag", "Ready to update to " + assignmentId);
                db.collection("course").document(classID)
                        .collection("assignment").document(assignmentId)
                        .update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Tag", "Successfully update to " + assignmentId);
                            if (selectedFileUri != null) {
                                uploadNewFileToFirebase(selectedFileUri, classID);
                            } else {
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("assignmentUpdated", true);
                                setResult(RESULT_OK, resultIntent);
                                Toast.makeText(EditAssignmentActivity.this, "Assignment updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditAssignmentActivity.this, "Failed to update assignment", Toast.LENGTH_SHORT).show();
                            Log.e("EditAssignmentActivity", "Error updating assignment", e);
                        });
            }
            else Log.e("EditAssignmentActivity", "No change");

        }
    }

    private void uploadNewFileToFirebase(Uri fileUri, String code) {
        String fileName = getFileName(fileUri);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference(code);
        StorageReference fileRef = storageRef.child("Assignment/"+title.getText().toString()+"/AttachFile/" + fileName);

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
                    Log.d("EditAssignmentActivity", "File uploaded successfully");
                    notificationBuilder.setContentText("Upload complete")
                            .setProgress(0, 0, false);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                    Toast.makeText(EditAssignmentActivity.this, "Assignment updated successfully", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("assignmentUpdated", true);
                    setResult(RESULT_OK, resultIntent);
                    Toast.makeText(EditAssignmentActivity.this, "Assignment updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("EditAssignmentActivity", "File upload failed", e);
                    notificationBuilder.setContentText("Upload failed")
                            .setProgress(0, 0, false);
                    notificationManager.notify(notificationId, notificationBuilder.build());
                });
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (Objects.equals(uri.getScheme(), "content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private String convertString(String str) {
        return str.replaceAll("[/: ]", "_");
    }
}
