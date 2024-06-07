package com.example.studentmanagement;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;


public class LectureDetailClassDocumentFragment extends Fragment {
    private final String code;

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

        
       // Log.d("Get StorageReference", ID);
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        List<DocumentItem> documentList = new ArrayList<>();
//        DocumentViewAdapter adapter = new DocumentViewAdapter(documentList, getContext());
//        String ID = getCodeID();
//        recyclerView.setAdapter(adapter);
        //ReCyclerView
        //FirebaseStorage  storage = FirebaseStorage.getInstance();
        //StorageReference storageRef = storage.getReference(ID).child("Document/");
       // Log.d("Get StorageReference", storageRef.toString());
//        storageRef.listAll().addOnSuccessListener(listResult -> {
//            for (StorageReference prefix : listResult.getPrefixes()) {
//                // This is a folder
//                DocumentItem folderItem = new DocumentItem(prefix.getName(), true);
//                documentList.add(folderItem);
//            }
//
//            for (StorageReference item : listResult.getItems()) {
//                // This is a file
//                DocumentItem fileItem = new DocumentItem(item.getName(), false);
//                documentList.add(fileItem);
//            }
//
//            adapter.notifyDataSetChanged();
//        }).addOnFailureListener(e -> {
//            // Handle any errors
//            Log.e("LectureDetailClassDocumentFragment", "Error listing documents", e);
//        });


        
        //loadDocuments();
        return view;
    }

    private void loadDocuments() {

    }



}