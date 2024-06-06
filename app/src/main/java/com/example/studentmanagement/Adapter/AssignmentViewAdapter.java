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


import com.example.studentmanagement.Model.Assignment;
import com.example.studentmanagement.Model.AssignmentItem;
import com.example.studentmanagement.Model.MultiFilePickerDialog;
import com.example.studentmanagement.R;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentViewAdapter extends RecyclerView.Adapter<AssignmentViewAdapter.AssignmentViewHolder> {

    private List<Assignment> assignmentlist;
    private Context context;

    public AssignmentViewAdapter(List<Assignment> assignments, Context context) {
        this.assignmentlist = assignments;
        this.context = context;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lecture_deadline, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentlist.get(position);
        holder.titleTextView.setText(assignment.getTitle());
        holder.dueDateTextView.setText(formatTimestamp(assignment.getDue_date()));
    }

    @Override
    public int getItemCount() {
        return assignmentlist.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dueDateTextView;
        TextView descriptionTextView;

        public AssignmentViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.deadline_name);
            dueDateTextView = itemView.findViewById(R.id.deadline_time);
            //descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
