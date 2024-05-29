package com.example.studentmanagement.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Domain.UserRepository;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    List<Course> mCourse;

    public ScheduleAdapter(List<Course> mCourse) {
        this.mCourse = mCourse;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_item, parent,false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Course course=mCourse.get(position);
        String codeStr = course.getCode() + " - " + course.getName();
        String infoStr = "Period " + String.valueOf(course.getStart()) + "-" + String.valueOf(course.getEnd()) + ", " + "room " + course.getRoom();
        holder.code.setText(codeStr);
        holder.infoClass.setText(infoStr);
        if(lectureIds.get(course.getId())!=null)
        {
            UserRepository userRepository = new UserRepository();
            userRepository.getNameById(lectureIds.get(course.getId())).addOnCompleteListener(new OnCompleteListener<String>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if(task.isSuccessful()){
                        String name = task.getResult();
                        if(name!=null)
                            holder.lecturer.setText(" "+name);
                    }
                    else {
                        Log.i("getUserCourse","not success");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mCourse!=null)
            return mCourse.size();
        return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Course> courseList) {
        this.mCourse=new ArrayList<>(courseList);
        notifyDataSetChanged();
        Log.i("size",String.valueOf(getItemCount()));

    }
    Map<String, String> lectureIds = new HashMap<>();
    @SuppressLint("NotifyDataSetChanged")
    public void setLecturer(Map<String, String> mapCourseLecturer) {
        this.lectureIds=mapCourseLecturer;
        notifyDataSetChanged();
    }


    public static class ScheduleViewHolder extends RecyclerView.ViewHolder{
        private TextView infoClass, lecturer, code;
        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            infoClass = itemView.findViewById(R.id.infoClass);
            lecturer = itemView.findViewById(R.id.lecturer);
            code = itemView.findViewById(R.id.code);
        }
    }
}
