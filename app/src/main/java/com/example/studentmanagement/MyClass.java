package com.example.studentmanagement;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyClass#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyClass extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

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

        // Tạo danh sách các năm
        List<String> years = new ArrayList<>(Arrays.asList("Năm Học", "2021", "2022", "2023", "2024"));
        List<String> semesters = new ArrayList<>(Arrays.asList("Học Kỳ", "Học Kỳ I", "Học Kỳ II", "Học Kỳ Hè"));

        // Tạo một ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, years) {
            @Override
            public boolean isEnabled(int position) {
                // Vô hiệu hóa mục đầu tiên (hint)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Đặt màu cho mục đầu tiên
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

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
                if (position == 0) {
                    // Đặt màu cho mục đầu tiên
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
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
                if (position == 0) {
                    // Đặt màu cho mục đầu tiên
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        // Đặt layout cho các mục dropdown
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Gắn adapter vào Spinner
        spinnerNamHoc.setAdapter(yearAdapter);
        spinnerHocKy.setAdapter(semesterAdapter);

        // Xử lý sự kiện chọn mục từ Spinner
        spinnerNamHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    // Thực hiện hành động khi một năm được chọn
                    String selectedYear = (String) parent.getItemAtPosition(position);
                    Toast.makeText(parent.getContext(), "Selected: " + selectedYear, Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì cả
            }
        });

        spinnerHocKy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    // Thực hiện hành động khi một năm được chọn
                    String selectedYear = (String) parent.getItemAtPosition(position);
                    Toast.makeText(parent.getContext(), "Selected: " + selectedYear, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì cả
            }
        });

        RecyclerView recyclerView1 = view.findViewById(R.id.class_view1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView recyclerView2 = view.findViewById(R.id.class_view2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tạo dữ liệu mẫu
        List<ClassItem> classItemList1 = new ArrayList<>();
        classItemList1.add(new ClassItem("SE330.O22", "Lập Trình Java", "⭐ Lê Thanh Trọng", "2-5, Thứ năm"));
        classItemList1.add(new ClassItem("SE331.O22", "Cấu Trúc Dữ Liệu", "⭐ Nguyễn Văn A", "1-3, Thứ hai"));
        classItemList1.add(new ClassItem("SE332.O22", "Mạng Máy Tính", "⭐ Trần Văn B", "4-6, Thứ ba"));

        // Tạo và gắn Adapter
        ClassAdapter classAdapter1 = new ClassAdapter(classItemList1);
        recyclerView1.setAdapter(classAdapter1);

        List<ClassItem> classItemList2 = new ArrayList<>();
        classItemList2.add(new ClassItem("SE330.O22", "Cấu Trúc Dữ Liệu", "⭐ Lê Thanh Trọng", "2-5, Thứ năm"));
        classItemList2.add(new ClassItem("SE331.O22", "Hướng Đối Tượng", "⭐ Nguyễn Văn A", "1-3, Thứ hai"));
        classItemList2.add(new ClassItem("SE332.O22", "Lập Trình Java", "⭐ Trần Văn B", "4-6, Thứ ba"));

        // Tạo và gắn Adapter
        ClassAdapter classAdapter2 = new ClassAdapter(classItemList2);
        recyclerView2.setAdapter(classAdapter2);
    }
}
