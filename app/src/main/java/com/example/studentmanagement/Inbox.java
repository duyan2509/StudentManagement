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
//import com.sun.xml.bind.v2.model.core.Adapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Inbox#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Inbox extends Fragment {
    ImageView searchButon;
    RecyclerView recyclerView;
    RecentChatAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Inbox() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Inbox newInstance(String param1, String param2) {
        Inbox fragment = new Inbox();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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