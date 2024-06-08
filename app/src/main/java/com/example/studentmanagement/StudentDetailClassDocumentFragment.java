package com.example.studentmanagement;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import com.example.studentmanagement.Adapter.DocumentAdapter;
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
        ViewGroup container1 = view.findViewById(R.id.List_container);
        Fragment plusButtonFragment = new PlusButton(class_code,filePickerLauncher,storageRef,adapter);
        getChildFragmentManager().beginTransaction()
                .add(container1.getId(), plusButtonFragment)
                .commit();

        Button Assignments = view.findViewById(R.id.Assignments);
        Assignments.setOnClickListener(v -> {
            if (getActivity() instanceof StudentDetailClassActivity) {
                ((StudentDetailClassActivity) getActivity()).loadFragment(new StudentDetailClassAssignmentFragment(class_code,class_id));
            }
        });
        return view;
    }

}