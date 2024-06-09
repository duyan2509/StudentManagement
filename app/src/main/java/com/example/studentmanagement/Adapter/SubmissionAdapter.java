package com.example.studentmanagement.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Model.StudentSubmission;
import com.example.studentmanagement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.StudentViewHolder> {
    private List<StudentSubmission> studentList;
    private List<StudentSubmission> studentListFull; // Danh sách đầy đủ để lưu trữ dữ liệu gốc
    private Context context;

    public SubmissionAdapter(List<StudentSubmission> studentList, Context context) {
        this.studentList = studentList;
        this.studentListFull = new ArrayList<>(studentList); // Khởi tạo danh sách đầy đủ
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
        if (student.getStatus().equals("view assignment"))
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

    // Thêm phương thức tìm kiếm
    public void filter(String text) {
        Log.d("TAG", "start filter with " + text);
        studentList.clear();
        Log.d("TAG", "clear studentList " + studentListFull.get(1).getStudentID());
        if (text.isEmpty()) {
            Log.d("TAG", "studentList is empty");
            studentList.addAll(studentListFull);
        } else {
            Log.d("TAG", "studentList has data");
            text = text.toLowerCase(Locale.getDefault());
            Log.d("TAG", "text.toLowerCase");
            for (StudentSubmission student : studentListFull) {
                Log.d("TAG", "begin filter " + student.getStudentName() + student.getStudentCode());
                if(!student.getStudentName().isEmpty() && !student.getStudentCode().isEmpty()) {
                    Log.d("TAG", "inner if " + student.getStudentName() + student.getStudentCode());
                    if (student.getStudentName().toLowerCase(Locale.getDefault()).contains(text) ||
                            student.getStudentCode().toLowerCase(Locale.getDefault()).contains(text)) {
                        Log.d("TAG", "ready add student " + student.getStudentID());
                    studentList.add(student);
                        Log.d("TAG", "add student " + student.getStudentID());
                    }
                }
            }
            Log.d("TAG", "complete add studentList");
        }
//        notifyDataSetChanged();
    }
}
