package com.example.studentmanagement.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Model.StudentSubmission;
import com.example.studentmanagement.R;

import java.util.List;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.StudentViewHolder> {
    private List<StudentSubmission> studentList;
    private Context context;

    public SubmissionAdapter(List<StudentSubmission> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail_submisstion, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentSubmission student = studentList.get(position);
        holder.studentIdTextView.setText(student.getStudentCode());
        holder.studentNameTextView.setText(student.getStudentName());
        holder.submitStatusTextView.setText(student.getStatus());
        if(student.getStatus().equals("view assignment"))
            holder.submitStatusTextView.setTextColor(Color.parseColor("#00FF00"));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentIdTextView;
        TextView studentNameTextView;
        TextView submitStatusTextView;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentIdTextView = itemView.findViewById(R.id.student_id);
            studentNameTextView = itemView.findViewById(R.id.student_name);
            submitStatusTextView = itemView.findViewById(R.id.submit_status);
        }
    }
}
