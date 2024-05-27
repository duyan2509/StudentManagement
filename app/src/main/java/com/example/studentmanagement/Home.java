package com.example.studentmanagement;

import static com.example.studentmanagement.Utils.CalendarUtils.daysInWeekArray;
import static com.example.studentmanagement.Utils.CalendarUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.studentmanagement.Adapter.CalendarAdapter;
import com.example.studentmanagement.Adapter.DeadlineAdapter;
import com.example.studentmanagement.Domain.CourseRepository;
import com.example.studentmanagement.Domain.UserCourseRepository;
import com.example.studentmanagement.Model.Assignment;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Utils.CalendarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.units.qual.C;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeUser.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        CalendarUtils.selectedDate = LocalDate.now();
        setWeekView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private TextView monthYearText, seeAll, noCourse, noDeadline;
    private androidx.recyclerview.widget.RecyclerView calendarRecyclerView, rcv_schedule,rcv_deadline;
    ProgressBar prgbSchedule,prgbDeadline;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        initWidgets(view);
        CalendarUtils.selectedDate = LocalDate.now();
        setWeekView();
        seeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), Deadline.class);
                startActivity(i);
            }
        });
        return view;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        List<LocalDate> days = daysInWeekArray(CalendarUtils.selectedDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(days);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 6);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        calendarAdapter.setSchedule(rcv_schedule);
        calendarAdapter.setDeadline(rcv_deadline);
        calendarAdapter.setSubView(noCourse,noDeadline,prgbSchedule,prgbDeadline);
        calendarAdapter.setContext(getContext());
    }

    private void initWidgets(View view) {
        monthYearText=view.findViewById(R.id.monthYearTV);
        calendarRecyclerView=view.findViewById(R.id.rcv_calender);
        rcv_schedule = view.findViewById(R.id.rcv_schedule);
        seeAll = view.findViewById(R.id.seeAll);
        rcv_deadline =view.findViewById(R.id.rcv_deadline);
        noCourse=view.findViewById(R.id.noCourse);
        noDeadline=view.findViewById(R.id.noDeadline);
        prgbDeadline=view.findViewById(R.id.prgbDeadline);
        prgbSchedule=view.findViewById(R.id.prgbSchedule);
    }

}