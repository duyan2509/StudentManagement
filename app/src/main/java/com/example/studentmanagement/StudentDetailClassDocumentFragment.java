package com.example.studentmanagement;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.fragment.app.Fragment;


public class StudentDetailClassDocumentFragment extends Fragment {
    private final String code;

    public StudentDetailClassDocumentFragment(String classCode) {
        // Required empty public constructor
        this.code=classCode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.detail_class_student, container, false);
        ViewGroup container1 = view.findViewById(R.id.List_container);
        Fragment plusButtonFragment = new PlusButton(code);
        getChildFragmentManager().beginTransaction()
                .add(container1.getId(), plusButtonFragment)
                .commit();

        Button Assignments = view.findViewById(R.id.Assignments);
        Assignments.setOnClickListener(v -> {
            if (getActivity() instanceof StudentDetailClassActivity) {
                ((StudentDetailClassActivity) getActivity()).loadFragment(new StudentDetailClassAssignmentFragment(code));
            }
        });
        return view;
    }

}