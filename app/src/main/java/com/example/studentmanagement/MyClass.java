package com.example.studentmanagement;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;



public class MyClass extends Fragment{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private List<ClassItem> classItemList;
    private List<ClassItem> filteredClassItemList;
    private ClassAdapter classAdapter;
    private String role;

    public String selectedYear;
    public String selectedSemester;
    public String query;
    public MyClass() {
        // Required empty public constructor
    }

    public static MyClass newInstance(String param1, String param2) {
        MyClass fragment = new MyClass();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (getArguments() != null) {
            role = getArguments().getString("role");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_class, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinnerNamHoc = view.findViewById(R.id.spinner_nam_hoc);
        Spinner spinnerHocKy = view.findViewById(R.id.spinner_hoc_ky);
        EditText searchEditText = view.findViewById(R.id.searchEditText);
        Button btnSearch = view.findViewById(R.id.btn_search);

        // Tạo danh sách các năm
        List<String> years = new ArrayList<>(Arrays.asList("Năm Học", "2021", "2022", "2023", "2024"));
        List<String> semesters = new ArrayList<>(Arrays.asList("Học Kỳ", "Học Kỳ I", "Học Kỳ II", "Học Kỳ Hè"));

        // Initialize the classItemList and adapter
        classItemList = new ArrayList<>();
        filteredClassItemList = new ArrayList<>();
        classAdapter = new ClassAdapter(classItemList, getContext());

        RecyclerView recyclerView = view.findViewById(R.id.class_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Fetch all classes initially (or you can fetch based on a default year if needed)
        if(selectedYear == null){
            fetchClassesForYear(null); // Hoặc cung cấp một năm mặc định
            recyclerView.setAdapter(classAdapter);
        }

        // Tạo ArrayAdapter
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, years) {
            @Override
            public boolean isEnabled(int position) {
                // Vô hiệu hóa mục đầu tiên (hint)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK); // Đặt màu cho mục đầu tiên
                return view;
            }
        };

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, semesters) {
            @Override
            public boolean isEnabled(int position) {
                // Vô hiệu hóa mục đầu tiên (hint)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK); // Đặt màu cho mục đầu tiên
                return view;
            }
        };

        // Đặt layout cho các mục dropdown
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gắn adapter vào Spinner
        spinnerNamHoc.setAdapter(yearAdapter);
        spinnerHocKy.setAdapter(semesterAdapter);

        // Xử lý sự kiện  mục từ Spinner
        spinnerNamHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    // Thực hiện hành động khi một năm được chọn
                    selectedYear = (String) parent.getItemAtPosition(position);
                    Toast.makeText(parent.getContext(), "Selected: " + selectedYear, Toast.LENGTH_SHORT).show();
                    classItemList.clear();
                    fetchClassesForYear(selectedYear); // Lấy dữ liệu lớp cho năm được chọn
                    classAdapter = new ClassAdapter(classItemList, getContext());
                    recyclerView.setAdapter(classAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ko làm gì cả
            }
        });

        spinnerHocKy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedSemester = (String) parent.getItemAtPosition(position);
                    if(selectedYear != null){
                        Toast.makeText(parent.getContext(), "Selected: " + selectedSemester, Toast.LENGTH_SHORT).show();
                        classItemList.clear();
                        fetchClassesForYearAndSemester(selectedYear, selectedSemester);
                        classAdapter = new ClassAdapter(classItemList, getContext());
                        recyclerView.setAdapter(classAdapter);
                    }
                    else {
                        Toast.makeText(parent.getContext(), "Must select year first! ", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // K làm gì cả
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterClasses(charSequence.toString());
                classAdapter = new ClassAdapter(filteredClassItemList, getContext());
                recyclerView.setAdapter(classAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });

        // Handle search button click
        btnSearch.setOnClickListener(v -> {
            String query = searchEditText.getText().toString();
            filterClasses(query);
            classAdapter = new ClassAdapter(filteredClassItemList, getContext());
            recyclerView.setAdapter(classAdapter);
        });
    }

    private void fetchClassesForYear(String year) {
        // Initialize Firebase Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String Uid=FirebaseAuth.getInstance().getUid();

        database.collection("user_course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        classItemList.clear();
                        assert Uid != null;
                        Log.d("Get User ID: ", Uid);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String user_id = document.getString("user_id");
                            String course_id = document.getString("course_id");

                            if (Objects.equals(user_id, Uid)) {
                                assert course_id != null;
                                DocumentReference docRef = database.collection("course").document(course_id);

                                docRef.get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String courseYear = documentSnapshot.getString("academic_year"); // Giả sử "academic_year" là một field trong tài liệu "course"
                                        if(selectedSemester == null){
                                            if (year == null || Objects.equals(courseYear, year)) {
                                                String id = documentSnapshot.getId();
                                                String code = documentSnapshot.getString("code");
                                                String name = documentSnapshot.getString("name");
                                                String lecture = documentSnapshot.getString("lecture");
                                                String time = Objects.requireNonNull(documentSnapshot.getLong("start")).toString() +
                                                        "-" + Objects.requireNonNull(documentSnapshot.getLong("end")).toString() +
                                                        ", " + documentSnapshot.getString("schedule");

                                                classItemList.add(new ClassItem(id, code, name, lecture, time));
                                                classAdapter.notifyDataSetChanged(); // Thông báo adapter dữ liệu đã thay đổi
                                                Log.d("DEBUG: ", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                            }
                                        }

                                        else {
                                            long courseSemester = documentSnapshot.getLong("semester");
                                            int intSemester = 0;
                                            switch (Objects.requireNonNull(selectedSemester)) {
                                                case "Học Kỳ I":
                                                    intSemester = 1;
                                                    break;
                                                case "Học Kỳ II":
                                                    intSemester = 2;
                                                    break;
                                                case "Học Kỳ Hè":
                                                    intSemester = 3;
                                                    break;
                                                default:
                                                    intSemester = 0;
                                                    break;
                                            }

                                            if ((year == null || Objects.equals(courseYear, year)) &&
                                                    (intSemester == 0 || (intSemester == courseSemester))) {
                                                String id = documentSnapshot.getId();
                                                String code = documentSnapshot.getString("code");
                                                String name = documentSnapshot.getString("name");
                                                String lecture = documentSnapshot.getString("lecture");
                                                String time = Objects.requireNonNull(documentSnapshot.getLong("start")).toString() +
                                                        "-" + Objects.requireNonNull(documentSnapshot.getLong("end")).toString() +
                                                        ", " + documentSnapshot.getString("schedule");

                                                classItemList.add(new ClassItem(id, code, name, lecture, time));
                                                classAdapter.notifyDataSetChanged();
                                                Log.d("DEBUG: ", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                            }
                                        }

                                    }
                                }).addOnFailureListener(e -> Log.d("DEBUG: ", "Error getting document: ", e));
                            }
                        }
                    } else {
                        Log.d("DEBUG: ", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void fetchClassesForYearAndSemester(String year, String semester) {
        // Initialize Firebase Firestore
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String Uid = FirebaseAuth.getInstance().getUid();

        database.collection("user_course")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        assert Uid != null;
                        Log.d("Get User ID: ", Uid);

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String user_id = document.getString("user_id");
                            String course_id = document.getString("course_id");

                            if (Objects.equals(user_id, Uid)) {
                                assert course_id != null;
                                DocumentReference docRef = database.collection("course").document(course_id);

                                docRef.get().addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String courseYear = documentSnapshot.getString("academic_year");
                                        long courseSemester = documentSnapshot.getLong("semester");
                                        int intSemester = 0;
                                        switch (Objects.requireNonNull(semester)) {
                                            case "Học Kỳ I":
                                                intSemester = 1;
                                                break;
                                            case "Học Kỳ II":
                                                intSemester = 2;
                                                break;
                                            case "Học Kỳ Hè":
                                                intSemester = 3;
                                                break;
                                            default:
                                                intSemester = 0;
                                                break;
                                        }

                                        if ((year == null || Objects.equals(courseYear, year)) &&
                                                (intSemester == 0 || (intSemester == courseSemester))) {
                                            String id = documentSnapshot.getId();
                                            String code = documentSnapshot.getString("code");
                                            String name = documentSnapshot.getString("name");
                                            String lecture = documentSnapshot.getString("lecture");
                                            String time = Objects.requireNonNull(documentSnapshot.getLong("start")).toString() +
                                                    "-" + Objects.requireNonNull(documentSnapshot.getLong("end")).toString() +
                                                    ", " + documentSnapshot.getString("schedule");

                                            classItemList.add(new ClassItem(id, code, name, lecture, time));
                                            classAdapter.notifyDataSetChanged();
                                            Log.d("DEBUG: ", documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                        }
                                    }
                                }).addOnFailureListener(e -> Log.d("DEBUG: ", "Error getting document: ", e));
                            }
                        }
                    } else {
                        Log.d("DEBUG: ", "Error getting documents: ", task.getException());
                    }
                });
    }
    private void filterClasses(String query) {
        this.query = query;
        filteredClassItemList.clear();
        if (query.isEmpty()) {
            filteredClassItemList.addAll(classItemList);
        } else {
            for (ClassItem item : classItemList) {
                if (item.getClassCode().toLowerCase().contains(query.toLowerCase()) ||
                        item.getClassName().toLowerCase().contains(query.toLowerCase())) {
                    filteredClassItemList.add(item);
                }
            }
        }
        classAdapter.notifyDataSetChanged();
    }
}

