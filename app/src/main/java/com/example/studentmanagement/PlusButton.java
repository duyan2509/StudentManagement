package com.example.studentmanagement;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class PlusButton extends Fragment {

    public PlusButton() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plus_button, container, false);
        ImageButton plus_button = view.findViewById(R.id.plus_button);
        plus_button.setOnClickListener(v -> {
            // Load CancelFragment with transition animation
            if (getActivity() != null) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();


                transaction.replace(R.id.container1, new FileFolderCancelButton());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_plus_button, container, false);
        return view;
    }
}