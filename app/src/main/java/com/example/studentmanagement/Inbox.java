package com.example.studentmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.studentmanagement.Adapter.RecentChatAdapter;
import com.example.studentmanagement.Model.ChatRoom;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class Inbox extends Fragment {
    ImageView searchButon;
    RecyclerView recyclerView;
    RecentChatAdapter adapter;



    public Inbox() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        searchButon = view.findViewById(R.id.btSearch);
        recyclerView = view.findViewById(R.id.recyler_view);

        searchButon.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), SearchChatActivity.class));
        });

        setupRecyclerView();
        return view;
    }

    void setupRecyclerView(){
        Log.d("CHAT", "set up recycle view" + FirebaseUtil.currentUserId());
        Query query = FirebaseUtil.allChatroomCollectionReference()
                  .whereArrayContains("userIds", FirebaseUtil.currentUserId())
                  .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatRoom> options = new FirestoreRecyclerOptions.Builder<ChatRoom>()
                .setQuery(query,ChatRoom.class).build();

        adapter = new RecentChatAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        Log.d("CHAT3", "set up recycle view" + adapter);
        adapter.startListening();

    }
    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}