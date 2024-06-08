package com.example.studentmanagement.Adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

import com.example.studentmanagement.StudentAssignmentActivity;
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
                ColorStateList colorStateList = ColorStateList.valueOf(Color.RED);
                btnSubmit.setBackgroundTintList(colorStateList);
                btnSubmit.setText("Miss");
                btnSubmit.setTextColor(Color.WHITE);
            } else if (assignment.isSubmittedLate()) {
                ColorStateList colorStateList = ColorStateList.valueOf(Color.GREEN);
                btnSubmit.setBackgroundTintList(colorStateList);
                btnSubmit.setText("Done");
            } else {
                ColorStateList colorStateList = ColorStateList.valueOf(Color.BLUE);
                btnSubmit.setBackgroundTintList(colorStateList);
                btnSubmit.setText("Submit");
            }

            btnSubmit.setOnClickListener(v -> {
                if (btnSubmit.getText().toString().equalsIgnoreCase("Submit") ||
                        btnSubmit.getText().toString().equalsIgnoreCase("Done")) {
                    Intent intent = new Intent(context, StudentAssignmentActivity.class);
                    intent.putExtra("deadline_name", assignment.getTitle());
                    intent.putExtra("deadline_time", formatTimestamp(assignment.getDueDate()));
                    intent.putExtra("class_id", (assignment.getClassID()));
                    intent.putExtra("class_code", (assignment.getClass_code()));
                    context.startActivity(intent);
                } else {
                    handleSubmission(assignment);
                }
            });
        }

        private void handleSubmission(AssignmentItem assignment) {
            // Implement your submission logic here
            Toast.makeText(context, " Deadline đã quá hạn!", Toast.LENGTH_SHORT).show();
        }

        private String formatTimestamp(Timestamp timestamp) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }
}