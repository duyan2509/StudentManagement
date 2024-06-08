package com.example.studentmanagement;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.studentmanagement.Adapter.AssignmentAdapter;

import com.example.studentmanagement.Model.AssignmentItem;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class StudentDetailClassAssignmentFragment extends Fragment {

    private AssignmentAdapter adapter;
    private List<AssignmentItem> assignments_list;
    private final String code;

    public StudentDetailClassAssignmentFragment(String code) {
        // Required empty public constructor
        this.code = code;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_student_detail_class_assignment, container, false);

        Button Document = view.findViewById(R.id.Document);
        Document.setOnClickListener(v -> {
            if (getActivity() instanceof StudentDetailClassActivity) {
                ((StudentDetailClassActivity) getActivity()).loadFragment(new StudentDetailClassDocumentFragment(code));
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        Timestamp currentDate = Timestamp.now();

        assignments_list = new ArrayList<>();
        adapter = new AssignmentAdapter(assignments_list, getContext());
        recyclerView.setAdapter(adapter);

        db.collection("course").document(code).collection("assignment").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Log.d("Check collection:", code);
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String title = document.getString("title");
                    Timestamp Time_Assignment = document.getTimestamp("due_date");

                    Task<QuerySnapshot> querySnapshotTask = db.collection("course").document(code)
                            .collection("assignment")
                            .document(document.getId()).collection("submission").get()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {

                                    boolean assignmentAdded = false;

                                    for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                        String student_id = document1.getString("student_id");
                                        if (Objects.equals(student_id, currentUserId)) {
                                            DocumentReference submissionRef = document1.getReference();
                                            submissionRef.get().addOnCompleteListener(submissionTask -> {
                                                if (submissionTask.isSuccessful()) {
                                                    DocumentSnapshot submissionDoc = submissionTask.getResult();
                                                    if (submissionDoc.exists()) {
                                                        Log.d("Debug:Time", "Check DoneDeadline");
                                                        AssignmentItem assignment = new AssignmentItem(title,
                                                                Time_Assignment);
                                                        String id = submissionDoc.getString("student_id");

                                                        Timestamp Time_submitted = submissionDoc
                                                                .getTimestamp("submitted_at");
                                                        assert Time_submitted != null;
                                                        Log.d("Debug: Get ID and Time",
                                                                id + " ; " + formatTimestamp(Time_Assignment) + " ; "
                                                                        + formatTimestamp(Time_submitted) + ";"
                                                                        + Time_submitted.compareTo(Time_Assignment));

                                                        assignment.setSubmittedLate(true);
                                                        assignments_list.add(assignment);
                                                        adapter.notifyDataSetChanged();
                                                        printAssignmentList(assignments_list);

                                                    } else {
                                                        Log.d("Debug:Time", "Check CurrentTime");
                                                        AssignmentItem assignment = new AssignmentItem(title,
                                                                Time_Assignment);
                                                        if (currentDate.compareTo(Time_Assignment) < 0) {
                                                            assignment.setLate(false);
                                                            assignment.setSubmittedLate(false);
                                                        } else {
                                                            assignment.setLate(true);
                                                            assignment.setSubmittedLate(false);
                                                        }
                                                        // Log.d("Debug:Time", formatTimestamp(currentDate) + " ; " +
                                                        // formatTimestamp(Time_Assignment) + ";" +
                                                        // currentDate.compareTo(Time_Assignment));
                                                        assignments_list.add(assignment);
                                                        adapter.notifyDataSetChanged();
                                                        printAssignmentList(assignments_list);
                                                    }
                                                } else {
                                                    Log.e("Error", "Error getting submission document: ",
                                                            submissionTask.getException());
                                                }
                                            });
                                            assignmentAdded = true;
                                        }
                                    }

                                    if (!assignmentAdded) {
                                        AssignmentItem assignment = new AssignmentItem(title, Time_Assignment);
                                        if (currentDate.compareTo(Time_Assignment) < 0) {
                                            assignment.setLate(false);
                                            assignment.setSubmittedLate(false);
                                        } else {
                                            assignment.setLate(true);
                                            assignment.setSubmittedLate(false);
                                        }
                                        assignments_list.add(assignment);
                                        adapter.notifyDataSetChanged();
                                        printAssignmentList(assignments_list);
                                    }

                                }
                            });
                }
            }
        });

        return view;
    }

    private void printAssignmentList(List<AssignmentItem> assignmentsList) {
        for (AssignmentItem item : assignmentsList) {
            Log.d("AssignmentItem", "Title: " + item.getTitle() +
                    ", Due Date: " + formatTimestamp(item.getDueDate()) +
                    ", Is Late: " + item.isLate() +
                    ", Is Submitted Late: " + item.isSubmittedLate());
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }
}