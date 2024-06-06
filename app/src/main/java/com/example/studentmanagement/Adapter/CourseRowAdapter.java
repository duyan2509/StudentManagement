package com.example.studentmanagement.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Domain.CourseRepository;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Model.UserCourse;
import com.example.studentmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class CourseRowAdapter extends RecyclerView.Adapter<CourseRowAdapter.CourseRowViewHolder> {
    public CourseRowAdapter(List<UserCourse> userCourseList) {
        this.userCourseList = userCourseList;
    }

    List<UserCourse> userCourseList;
    @NonNull
    @Override
    public CourseRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_class_view_row, parent,false);
        return new CourseRowAdapter.CourseRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseRowViewHolder holder, int position) {
        UserCourse userCourse=userCourseList.get(position);
        if(userCourse==null)
            return;
        holder.view_details.setVisibility(View.GONE);
        CourseRepository courseRepository=new CourseRepository();
        courseRepository.getCourseByCourseId(userCourse.getCourse_id()).addOnCompleteListener(new OnCompleteListener<Course>() {
            @Override
            public void onComplete(@NonNull Task<Course> task) {
                if(task.isSuccessful()){
                    Course course = task.getResult();
                    if(course!=null){
                        holder.course_name.setText(course.getName());
                        holder.course_code.setText(course.getCode());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(userCourseList!=null)
            return  userCourseList.size();
        return 0;
    }

    public static class CourseRowViewHolder extends RecyclerView.ViewHolder{
        ImageView view_details;
        TextView course_name,course_code;
        public CourseRowViewHolder(@NonNull View itemView) {
            super(itemView);
            view_details=itemView.findViewById(R.id.view_details);
            course_code=itemView.findViewById(R.id.course_code);
            course_name=itemView.findViewById(R.id.course_name);
        }
    }
}
