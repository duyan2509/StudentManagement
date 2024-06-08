package com.example.studentmanagement.Adapter;

import android.content.Context;
import android.content.Intent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Model.Assignment;
import com.example.studentmanagement.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Model.Assignment;
import com.example.studentmanagement.Model.AssignmentItem;
import com.example.studentmanagement.Model.MultiFilePickerDialog;
import com.example.studentmanagement.R;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AssignmentViewAdapter extends RecyclerView.Adapter<AssignmentViewAdapter.AssignmentViewHolder> {

    private final List<Assignment> assignmentList;
    private final Context context;
    private final FirebaseFirestore db;
    private final String classID; // Add classID as a member variable

    public AssignmentViewAdapter(List<Assignment> assignments, Context context, String classID) {
        this.assignmentList = assignments;
        this.context = context;
        this.classID = classID; // Initialize classID
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture_deadline, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentList.get(position);
        holder.titleTextView.setText(assignment.getTitle());
        holder.dueDateTextView.setText(formatTimestamp(assignment.getDue_date()));
        holder.btn_edit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditAssignmentActivity.class);
            intent.putExtra("assignmentId", assignment.getId()); // Pass assignment ID
            intent.putExtra("classID", assignment.getCourseId());
            intent.putExtra("title", assignment.getTitle());
            intent.putExtra("date", assignment.getDue_date());
            intent.putExtra("description", assignment.getDescription());// Pass course ID
            Log.d("TAG", "Put assignmentId " + assignment.getId());
            Log.d("TAG", "Put classID " + assignment.getCourseId());
            v.getContext().startActivity(intent);
        });
        holder.btn_remove.setOnClickListener(v -> removeAssignment(assignment.getId(), position));
    }

    @Override
    public int getItemCount() {
        return assignmentList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dueDateTextView;
        Button btn_edit, btn_remove;
        TextView descriptionTextView;

        public AssignmentViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.deadline_name);
            dueDateTextView = itemView.findViewById(R.id.deadline_time);
            btn_edit = itemView.findViewById(R.id.btn_edit);
            btn_remove = itemView.findViewById(R.id.btn_remove);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    private void removeAssignment(String assignmentId, int position) {
        Log.d("Tag", "Successfully call removeAssignment " + assignmentId);
        // db.collection("course").document(classID).collection("assignment").add(assignment)
        // .addOnSuccessListener(documentReference -> Log.d("Debug", "Assignment added
        // with ID: " + documentReference.getId()))
        // .addOnFailureListener(e -> Log.d("Debug", "Error adding assignment: " + e));
        // // Remove from Firestore
        db.collection("course").document(classID).collection("assignment").document(assignmentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Tag", "Successfully connect db");
                    // Remove from the list and notify the adapter
                    assignmentList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Assignment removed successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w("AssignmentAdapter", "Error deleting document", e);
                    Toast.makeText(context, "Failed to remove assignment", Toast.LENGTH_SHORT).show();
                });
    }
}
