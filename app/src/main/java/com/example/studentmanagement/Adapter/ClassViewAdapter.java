package com.example.studentmanagement.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.ChatActivity;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.R;
import com.example.studentmanagement.Utils.AndroidUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ClassViewAdapter extends FirestoreRecyclerAdapter<Course, ClassViewAdapter.CourseViewHolder> {
    Context context;

    public ClassViewAdapter(@NonNull FirestoreRecyclerOptions<Course> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ClassViewAdapter.CourseViewHolder holder, int position, @NonNull Course model) {
        holder.courseCode.setText(model.getCode());
        holder.courseName.setText(model.getName());

        holder.itemView.setOnClickListener(v -> {
            //navigate to class activity

        });
    }

    @NonNull
    @Override
    public ClassViewAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_class_view_row,parent,false);
        return new ClassViewAdapter.CourseViewHolder(view);
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{
        TextView courseCode;
        TextView courseName;
        ImageView viewDetails;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCode = itemView.findViewById(R.id.course_code);
            courseName = itemView.findViewById(R.id.course_name);
            viewDetails = itemView.findViewById(R.id.view_details);
        }
    }
}