package com.example.studentmanagement;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.DocumentAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class LectureDetailClassDocumentFragment extends Fragment {
    private final String code;
    private final List<StorageReference> filesAndFolders = new ArrayList<>();
    private DocumentAdapter adapter;
    public LectureDetailClassDocumentFragment(String classCode) {
        // Required empty public constructor
        this.code=classCode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Fragment: ", "Lecture Detail Class Document Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.detail_class_lecture, container, false);
        ViewGroup container1 = view.findViewById(R.id.container1);
        Fragment plusButtonFragment = new PlusButton(code);
        getChildFragmentManager().beginTransaction()
                .add(container1.getId(), plusButtonFragment)
                .commit();

        Button Assignments = view.findViewById(R.id.Assignments);
        Assignments.setOnClickListener(v -> {
            if (getActivity() instanceof LectureDetailClassActivity) {
                ((LectureDetailClassActivity) getActivity()).loadFragment(new LectureDetailClassAssignmentFragment(code));
            }
        });
        //Show Document
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DocumentAdapter(filesAndFolders, getContext(), item -> {
            if (item.getName().contains(".")) {
                // Handle file click
                Toast.makeText(getContext(), "File: " + item.getName(), Toast.LENGTH_SHORT).show();
            } else {
                // Handle folder click, load folder contents
                loadFolderContents(item);
            }
        });
        recyclerView.setAdapter(adapter);

        loadFolderContents(FirebaseStorage.getInstance().getReference(code).child("Document/"));
        return view;
    }

    private void loadFolderContents(StorageReference reference) {
        reference.listAll().addOnSuccessListener(listResult -> {
            filesAndFolders.clear();
            filesAndFolders.addAll(listResult.getItems());
            filesAndFolders.addAll(listResult.getPrefixes());
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            // Handle any errors
            Toast.makeText(getContext(), "Failed to load folder contents", Toast.LENGTH_SHORT).show();
        });
    }

}