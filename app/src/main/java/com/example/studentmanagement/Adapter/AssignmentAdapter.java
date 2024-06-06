package com.example.studentmanagement.Adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studentmanagement.Model.AssignmentItem;
import com.example.studentmanagement.Model.MultiFilePickerDialog;
import com.example.studentmanagement.R;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<AssignmentItem> assignments;
    private Context context;

    public AssignmentAdapter(List<AssignmentItem> assignments, Context context) {
        this.assignments = assignments;
        this.context = context;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_deadline, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentItem assignment = assignments.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    class AssignmentViewHolder extends RecyclerView.ViewHolder {

        TextView title, dueDate, status;
        Button btnSubmit;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.deadline_name);
            dueDate = itemView.findViewById(R.id.deadline_time);

            btnSubmit = itemView.findViewById(R.id.btn_submit);
        }

        public void bind(AssignmentItem assignment) {
            title.setText(assignment.getTitle());
            dueDate.setText(formatTimestamp(assignment.getDueDate()));

            // Set the initial status and button text
            if (assignment.isLate()) {

                btnSubmit.setText("Miss");
            } else if (assignment.isSubmittedLate()) {

                btnSubmit.setText("Done");
            } else {

                btnSubmit.setText("Submit");
            }

            // Set button click listener
            //                String buttonText = btnSubmit.getText().toString();
//                if ("Miss".equals(buttonText)) {
//                    // Do nothing if the status is "Miss"
//                    Toast.makeText(context, "Cannot submit assignment", Toast.LENGTH_SHORT).show();
//                } else {
//
//                    // Handle other cases
//                    handleSubmission(assignment);
//                }
//            btnSubmit.setOnClickListener(v -> {
//                if (btnSubmit.getText().toString().equals("Submit")) {
//                    // Hiển thị dialog cho phép chọn file
//                    MultiFilePickerDialog filePickerDialog = new MultiFilePickerDialog(context, filePickerLauncher, new MultiFilePickerDialog.OnFilesSelectedListener() {
//                        @Override
//                        public void onFilesSelected(List<Uri> selectedFiles) {
//                            // Xử lý các file được chọn ở đây, ví dụ: upload lên Firebase Storage
//                            for (Uri fileUri : selectedFiles) {
//                                // Thực hiện upload fileUri lên Firebase Storage
//                                uploadFileToFirebase(fileUri, assignment.getTitle());
//                            }
//                        }
//                    });
//                    filePickerDialog.show();
//                }
//            });
        }

        private void handleSubmission(AssignmentItem assignment) {
            // Implement your submission logic here
            Toast.makeText(context, "Submitting assignment: " + assignment.getTitle(), Toast.LENGTH_SHORT).show();
        }

        private String formatTimestamp(Timestamp timestamp) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }

//    private void uploadFileToFirebase(Uri fileUri, AssignmentItem assignment) {
//        String fileName = getFileName(fileUri);
//        StorageReference storageRef = FirebasesStorage.getReference(assignment.getCode());
//        StorageReference fileRef = storageRef.child("Assignment/" + fileName);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "upload_channel")
//                .setSmallIcon(R.drawable.ic_upload)
//                .setContentTitle("Uploading File")
//                .setContentText("Upload in progress")
//                .setPriority(NotificationCompat.PRIORITY_LOW);
//
//        int notificationId = 1;
//
//        fileRef.putFile(fileUri)
//                .addOnProgressListener(taskSnapshot -> {
//                    // Update progress if needed
//                })
//                .addOnSuccessListener(taskSnapshot -> {
//                    Log.d("Debug", "Add Assignment Done");
//                    notificationBuilder.setContentText("Upload complete")
//                            .setProgress(0, 0, false);
//                    notificationManager.notify(notificationId, notificationBuilder.build());
//
//                    // Update Firestore
//                    db.collection("course").document(assignment.getCode())
//                            .collection("assignment").document(assignment.getId())
//                            .collection("submission").add(new Submission(currentUserId, Timestamp.now()))
//                            .addOnSuccessListener(documentReference -> {
//                                Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
//                                assignment.setSubmitted(true);
//                                notifyDataSetChanged();
//                            })
//                            .addOnFailureListener(e -> Log.w("Firestore", "Error adding document", e));
//                })
//                .addOnFailureListener(e -> {
//                    Log.d("Debug", "Add Assignment Failed");
//                    notificationBuilder.setContentText("Upload failed")
//                            .setProgress(0, 0, false);
//                    notificationManager.notify(notificationId, notificationBuilder.build());
//                });
//    }

}
