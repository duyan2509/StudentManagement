package com.example.studentmanagement.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.ChatActivity;
import com.example.studentmanagement.Model.ChatMessage;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Model.User;
import com.example.studentmanagement.R;
import com.example.studentmanagement.Utils.AndroidUtil;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatAdapter.ChatModelViewHolder> {

    Context context;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessage model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.sender.setVisibility(View.GONE);
            holder.rightChatTextview.setText(model.getMessage());
        }else{
            FirebaseUtil.getUserById(model.getSenderId())
                    .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            User user = task.getResult().toObject(User.class);
                            holder.sender.setText(user.getName());
                        }
                    });
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.sender.setVisibility(View.VISIBLE);
            holder.leftChatTextview.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycle_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextview,rightChatTextview;
        TextView sender;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            sender = itemView.findViewById(R.id.sender);
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview);
        }
    }
}
