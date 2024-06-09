package com.example.studentmanagement;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.DocumentAdapter;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class StudentDetailClassDocumentFragment extends Fragment {
    private final String class_code,class_id;
    private final List<StorageReference> filesAndFolders = new ArrayList<>();
    private DocumentAdapter adapter;
    ActivityResultLauncher<String> filePickerLauncher;
    StorageReference storageRef;
    public StudentDetailClassDocumentFragment(String ClassCode,String ClassID) {
        // Required empty public constructor
        this.class_code=ClassCode;
        this.class_id=ClassID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Fragment: ", "Student Detail Class Document Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.detail_class_student, container, false);

        Button Assignments = view.findViewById(R.id.Assignments);
        Assignments.setOnClickListener(v -> {
            if (getActivity() instanceof StudentDetailClassActivity) {
                ((StudentDetailClassActivity) getActivity()).loadFragment(new StudentDetailClassAssignmentFragment(class_code,class_id));
            }
        });
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
        Log.d ("DeBug Get: ",FirebaseStorage.getInstance().getReference(class_code).child("Document").toString());
        loadFolderContents(FirebaseStorage.getInstance().getReference(class_code).child("Document"));
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