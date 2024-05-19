package com.example.studentmanagement.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.ChatActivity;
import com.example.studentmanagement.Model.ChatRoom;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.R;
import com.example.studentmanagement.Utils.AndroidUtil;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatAdapter extends FirestoreRecyclerAdapter<ChatRoom, RecentChatAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatAdapter(@NonNull FirestoreRecyclerOptions<ChatRoom> options,Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatRoom model) {
        FirebaseUtil.getCourseById(model.getChatroomId())
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                        Course course = task.getResult().toObject(Course.class);

                        holder.courseNameText.setText(course.getName());
                        if(lastMessageSentByMe)
                            holder.lastMessageText.setText("You: "+ model.getLastMessage());
                        else
                            holder.lastMessageText.setText(model.getLastMessage());
                        holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                        holder.itemView.setOnClickListener(v -> {
                            //navigate to chat activity
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passCourseModelAsIntent(intent, course);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        });

                    }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycle_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView courseNameText;
        TextView lastMessageText;
        TextView lastMessageTime;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            courseNameText = itemView.findViewById(R.id.course_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
        }
    }
}