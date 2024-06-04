package com.example.studentmanagement;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
public class LectureDetailClassAssignmentFragment extends Fragment {
    private final String code;
    public LectureDetailClassAssignmentFragment(String ClassCode) {
        // Required empty public constructor
        this.code=ClassCode;
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

        Button Document = view.findViewById(R.id.Document);
        Document.setOnClickListener(v -> {
            if (getActivity() instanceof LectureDetailClassActivity) {
                ((LectureDetailClassActivity) getActivity()).loadFragment(new LectureDetailClassDocumentFragment(code));
            }
        });

        Button btnAddAssignment = view.findViewById(R.id.btn_add_assignment);

        btnAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy tham chiếu đến Activity chứa fragment
                LectureDetailClassActivity activity = (LectureDetailClassActivity) getActivity();

                // Lấy classCode từ Intent của Activity
                String classID = activity.getIntent().getStringExtra("classID");

                // Chuyển đến AddAssignmentActivity và truyền classCode
                Intent intent = new Intent(activity, AddAssignmentActivity.class);
                intent.putExtra("classID", classID);
                startActivity(intent);
            }
        });

        return view;
    }
}