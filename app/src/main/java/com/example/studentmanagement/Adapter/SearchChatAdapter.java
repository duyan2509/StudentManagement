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
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchChatAdapter extends FirestoreRecyclerAdapter<Course, SearchChatAdapter.CourseViewHolder> {

    Context context;

    public SearchChatAdapter(@NonNull FirestoreRecyclerOptions<Course> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull CourseViewHolder holder, int position, @NonNull Course model) {
        holder.nameText.setText(model.getCode() + " - " + model.getName());
        holder.infoText.setText("Semester " + model.getSemester() + " || Year: " + model.getAcademic_year());

        holder.itemView.setOnClickListener(v -> {
            //navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passCourseModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_chat_recycle_row,parent,false);
        return new CourseViewHolder(view);
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{
        TextView nameText;
        TextView infoText;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.user_name_text);
            infoText = itemView.findViewById(R.id.phone_text);
        }
    }
}