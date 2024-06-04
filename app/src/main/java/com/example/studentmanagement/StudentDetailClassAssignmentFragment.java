package com.example.studentmanagement;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
public class StudentDetailClassAssignmentFragment extends Fragment {

    private final String code;

    public StudentDetailClassAssignmentFragment(String ClassCode) {
        // Required empty public constructor
        this.code=ClassCode;
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
        return view;
    }
}