package com.example.studentmanagement;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
public class LectureDetailClassAssignmentFragment extends Fragment {

    public LectureDetailClassAssignmentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lecture_detail_class_assignment, container, false);

        Button Document = view.findViewById(R.id.Document);
        Document.setOnClickListener(v -> {
            if (getActivity() instanceof LectureDetailClassActivity) {
                ((LectureDetailClassActivity) getActivity()).loadFragment(new LectureDetailClassDocumentFragment());
            }
        });

        Button btnAddAssignment = view.findViewById(R.id.btn_add_assignment);
        btnAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến AddAssignmentActivity khi button được click
                Intent intent = new Intent(getActivity(), AddAssignmentActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}