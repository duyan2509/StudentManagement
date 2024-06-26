package com.example.studentmanagement;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.studentmanagement.Adapter.AssignmentViewAdapter;
import com.example.studentmanagement.Model.Assignment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class LectureDetailClassAssignmentFragment extends Fragment {
    private final String code,id;
    private String classID;
    public LectureDetailClassAssignmentFragment(String ClassCode,String ClassID) {
        // Required empty public constructor
        this.code=ClassID;
        this.id=ClassCode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Fragment: ", "Lecture Detail Class Assignment Fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lecture_detail_class_assignment, container, false);

        LectureDetailClassActivity activity = (LectureDetailClassActivity) getActivity();

        // Lấy classCode từ Intent của Activity
        String classID = activity.getIntent().getStringExtra("classID");

        Button Document = view.findViewById(R.id.Document);
        Document.setOnClickListener(v -> {
            if (getActivity() instanceof LectureDetailClassActivity) {
                ((LectureDetailClassActivity) getActivity()).loadFragment(new LectureDetailClassDocumentFragment(id,code));
            }
        });

        Button btnAddAssignment = view.findViewById(R.id.btn_add_assignment);

        btnAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tham chiếu đến Activity chứa fragment
//                LectureDetailClassActivity activity = (LectureDetailClassActivity) getActivity();
//
//                // Lấy classCode từ Intent của Activity
//                String classID = activity.getIntent().getStringExtra("classID");

                // Chuyển đến AddAssignmentActivity và truyền classCode
                Intent intent = new Intent(activity, AddAssignmentActivity.class);
                intent.putExtra("classID", classID);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<Assignment> assignmentsList = new ArrayList<>();
        AssignmentViewAdapter adapter = new AssignmentViewAdapter(assignmentsList, getContext(), classID, id);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d("LectureDetailClassAssignmentFragment", code);
        db.collection("course").document(code).collection("assignment").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String id = document.getString("id");
                            String title = document.getString("title");
                            Timestamp dueDate = document.getTimestamp("due_date");
                            String description = document.getString("description");

                            Assignment assignment = new Assignment(classID, id, title, dueDate);
                            assignment.setDescription(description);
                            // Fetch and set additional fields as needed
                            assignmentsList.add(assignment);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w("LectureDetailClassAssignmentFragment", "Error getting documents.", task.getException());
                    }
                });
        //loadAssignments();
        //
        return view;
    }

    private void loadAssignments() {


    }
}