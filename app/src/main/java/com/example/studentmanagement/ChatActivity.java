package com.example.studentmanagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.ChatAdapter;
import com.example.studentmanagement.Model.ChatMessage;
import com.example.studentmanagement.Model.ChatRoom;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Utils.AndroidUtil;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    Course course;
    String chatroomId;
    ChatRoom chatroom;
    ChatAdapter adapter;
    EditText messageInput;
    ImageButton sendMessageBtn;
    ImageButton backBtn;
    TextView courseInfo;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get course
        course = AndroidUtil.getCourseModelAsIntent(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(course.getId());

        messageInput = findViewById(R.id.chat_message_input);
        sendMessageBtn = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        courseInfo = findViewById(R.id.course_info);
        recyclerView = findViewById(R.id.chat_recycler_view);

        backBtn.setOnClickListener(v -> {
           onBackPressed();
        });
        courseInfo.setText(course.getCode() + " - " + course.getName());

        sendMessageBtn.setOnClickListener((v -> {
            String message = messageInput.getText().toString().trim();
            if(message.isEmpty())
                return;
            sendMessageToUsers(message);
        }));

        getOrCreateChatroomModel();
        setUpChatRecycleView();
    }

    void setUpChatRecycleView(){
        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query,ChatMessage.class).build();

        adapter = new ChatAdapter(options,getApplicationContext());
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    void sendMessageToUsers(String message){
        chatroom.setLastMessageTimestamp(Timestamp.now());
        chatroom.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatroom.setLastMessage(message);
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroom);

        ChatMessage chatMessageModel = new ChatMessage(message,FirebaseUtil.currentUserId(),Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageInput.setText("");
                            //sendNotification(message);
                        }
                    }
                });
    }

    void getOrCreateChatroomModel(){
        CollectionReference userCourseRef = FirebaseUtil.getAllUserCourses();
        List<String> userList = new ArrayList<>();

        userCourseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the course ID from the user_course document
                        Map<String, Object> data = document.getData();
                        String courseId = data.get("course_id").toString();

                        if (courseId.equals(course.getId())) {
                            userList.add(data.get("user_id").toString());
                        }
                    }

                    if (!userList.isEmpty()) {
                        Log.d("USER CHECK", "check " + userList);

                        // Create chat room if first time
                        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(task1 -> {
                            if(task.isSuccessful()){
                                chatroom = task1.getResult().toObject(ChatRoom.class);
                                if(chatroom==null){
                                    //first time chat
                                    chatroom = new ChatRoom(
                                            chatroomId,
                                            userList,
                                            Timestamp.now(),
                                            ""
                                    );
                                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroom);
                                }
                            }
                        });
                    } else {
                        // Handle case when no courses found
                        Log.d("TAG", "No courses found for the user");
                    }
                } else {
                    // Handle errors
                    Log.d("TAG", "Error getting user courses: ", task.getException());
                }
            }
        });
    }


}