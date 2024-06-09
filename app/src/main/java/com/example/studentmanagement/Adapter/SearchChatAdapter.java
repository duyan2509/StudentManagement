package com.example.studentmanagement.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.ChatActivity;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.R;
import com.example.studentmanagement.Utils.AndroidUtil;

import java.util.List;

public class SearchChatAdapter extends RecyclerView.Adapter<SearchChatAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private Context context;

    public SearchChatAdapter(List<Course> courseList, Context context) {
        this.courseList = courseList;
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course model = courseList.get(position);
        holder.nameText.setText(model.getCode() + " - " + model.getName());
        holder.infoText.setText("Semester " + model.getSemester() + " || Year: " + model.getAcademic_year());

        holder.itemView.setOnClickListener(v -> {
            // Navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passCourseModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_chat_recycle_row, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView infoText;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.user_name_text);
            infoText = itemView.findViewById(R.id.phone_text);
        }
    }

    public void updateCourses(List<Course> courses) {
        this.courseList = courses;
        notifyDataSetChanged();
    }
}
