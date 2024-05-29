package com.example.studentmanagement.Adapter;

import static com.example.studentmanagement.Utils.CalendarUtils.getScheduleStr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Domain.CourseRepository;
import com.example.studentmanagement.Domain.UserCourseRepository;
import com.example.studentmanagement.Model.Assignment;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.R;
import com.example.studentmanagement.Utils.CalendarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    private final List<LocalDate> days;
    androidx.recyclerview.widget.RecyclerView  rcv_schedule;
    List<Course> courseList;
    ScheduleAdapter scheduleAdapter;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public CalendarAdapter(List<LocalDate> days )
    {
        this.days = days;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        return new CalendarAdapter.CalendarViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {
        LocalDate date = days.get(position);
        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));


        if(String.valueOf(date.getDayOfMonth()).equals(String.valueOf(CalendarUtils.selectedDate.getDayOfMonth()))) {
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.black));
            holder.dayOfMonth.setBackgroundColor(Color.WHITE);
            holder.bound.setCardBackgroundColor(Color.WHITE);
            /// LOAD COURSE IN DAY
            setScheduleView(CalendarUtils.selectedDate);
            /// LOAD DEADLINE IN DAY
            setDeadlineView(CalendarUtils.selectedDate);
        }
        else {
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.dayOfMonth.setBackgroundColor(Color.parseColor("#3364CE"));
            holder.bound.setCardBackgroundColor(Color.parseColor("#3364CE"));
        }

        if(String.valueOf(date.getDayOfMonth()).equals(String.valueOf(LocalDate.now().getDayOfMonth()))) {
            holder.dayOfMonth.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }


        holder.dayOfMonth.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                CalendarUtils.selectedDate = date;
                Log.i("schedule",getScheduleStr(date));
                notifyDataSetChanged();
            }
        });

    }
    public void setDeadlineView(LocalDate date) {
        Map<Assignment,String> assignmentStringMap=new HashMap<>();

        DeadlineAdapter deadlineAdapter=new DeadlineAdapter(assignmentStringMap);
        rcvDeadline.setAdapter(deadlineAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        rcvDeadline.setLayoutManager(linearLayoutManager);
        rcvDeadline.addItemDecoration(itemDecoration);
        UserCourseRepository userCourseRepository = new UserCourseRepository();

        userCourseRepository.getCourseByUserId(FirebaseAuth.getInstance().getUid()).addOnCompleteListener(new OnCompleteListener<List<Course>>() {
            @Override
            public void onComplete(@NonNull Task<List<Course>> task) {
                List<Course> courses = task.getResult();
                Log.i("amount courses",String.valueOf(courses.size()));
                for (Course course:courses)
                {
                    CourseRepository courseRepository = new CourseRepository();
                    courseRepository.getCodeByCourseId(course.getId()).addOnCompleteListener(new OnCompleteListener<Course>() {
                        @Override
                        public void onComplete(@NonNull Task<Course> task) {
                            Course course1 = task.getResult();
                            courseRepository.getAssignmentByCourseId(course.getId()).addOnCompleteListener(new OnCompleteListener<List<Assignment>>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onComplete(@NonNull Task<List<Assignment>> task) {
                                    List<Assignment> assignments=task.getResult();
                                    for(Assignment assignment:assignments){
                                        if(equal(assignment.getDue_date(),date)) {
                                            assignment.setCourseId(course1.getId());
                                            assignmentStringMap.put(assignment, course1.getCode());
                                        }
                                    }
                                    if(!assignmentStringMap.isEmpty()) {
                                        deadlineAdapter.updateDate(assignmentStringMap);
                                        rcvDeadline.setVisibility(View.VISIBLE);
                                        noDeadline.setVisibility(View.INVISIBLE);
                                        prgbDeadline.setVisibility(View.INVISIBLE);
                                    }
                                    else {
                                        rcvDeadline.setVisibility(View.INVISIBLE);
                                        noDeadline.setVisibility(View.VISIBLE);
                                        prgbDeadline.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setScheduleView(LocalDate date) {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            courseList = new ArrayList<>();
            scheduleAdapter = new ScheduleAdapter(courseList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
            rcv_schedule.setAdapter(scheduleAdapter);
            rcv_schedule.setLayoutManager(layoutManager);
            rcv_schedule.addItemDecoration(itemDecoration);
            getCourseId(FirebaseAuth.getInstance().getCurrentUser().getUid(), date);
        }
    }

    private void getCourseId(String uid, LocalDate date) {
        UserCourseRepository userCourseRepository = new UserCourseRepository();
        userCourseRepository.getCourseIdByUserId(uid).addOnCompleteListener(new OnCompleteListener<List<String>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<List<String>> task) {
                if(task.isSuccessful()){
                    List<String> courseIds = task.getResult();
                    getCourseInDay(courseIds,date);
                    getLecturerID(courseIds);
                }
                else {
                    Log.i("getUserCourse","not success");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("getUserCourse", Objects.requireNonNull(e.getMessage()));

            }
        });
    }

    private void getLecturerID(List<String> courseIds) {
        UserCourseRepository userCourseRepository = new UserCourseRepository();
        userCourseRepository.getLectureIdsBYCourseId(courseIds).addOnCompleteListener(new OnCompleteListener<Map<String, String>>() {
            @Override
            public void onComplete(@NonNull Task<Map<String, String>> task) {
                if(task.isSuccessful()){
                    Map<String, String> mapCourseLecturer = task.getResult();
                    if(mapCourseLecturer!=null && scheduleAdapter!=null)
                        scheduleAdapter.setLecturer(mapCourseLecturer);
                }
                else {
                    Log.i("getUserCourse","not success");
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCourseInDay(List<String> courseIds, LocalDate date) {
        CourseRepository courseRepository =new CourseRepository();
        courseList=new ArrayList<>();
        for(String courseId :courseIds){
            courseRepository.getCoursesByIds(courseId,getScheduleStr(date)).addOnCompleteListener(new OnCompleteListener<List<Course>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onComplete(@NonNull Task<List<Course>> task) {
                    if(task.isSuccessful()){
                        courseList.addAll(task.getResult());
                        sortCourse(courseList);
                        Log.i("courseList",String.valueOf(courseList.size()));
                        if (!courseList.isEmpty()) {
                            scheduleAdapter.updateData(courseList);
                            rcv_schedule.setVisibility(View.VISIBLE);
                            noCourse.setVisibility(View.INVISIBLE);
                            prgbSchedule.setVisibility(View.INVISIBLE);
                        }
                        else {
                            noCourse.setVisibility(View.VISIBLE);
                            rcvDeadline.setVisibility(View.INVISIBLE);
                            prgbSchedule.setVisibility(View.INVISIBLE);
                        }
                    }
                    else {
                        Log.i("getCourse","not success");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("getCoursesByIds", Objects.requireNonNull(e.getMessage()));
                }
            });
        }
    }
    void sortCourse(List<Course> course){
        int n= course.size();
        for(int i=0;i<n-1;i++)
            for(int j=i+1;j<n;j++)
            {
                if(course.get(i).getEnd()>=course.get(j).getStart())
                {
                    Collections.swap(course, i, j);
                }
            }

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    boolean equal (Timestamp due_date, LocalDate date) {
        LocalDate dueDateLocalDate = due_date.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return dueDateLocalDate.equals(date);
    }
    @Override
    public int getItemCount()
    {
        if(days!=null)
            return days.size();
        return 0;
    }

    public void setSchedule(androidx.recyclerview.widget.RecyclerView rcvSchedule) {
        this.rcv_schedule=rcvSchedule;
    }
    Context context;
    public void setContext(Context context) {
        this.context=context;
    }
    androidx.recyclerview.widget.RecyclerView rcvDeadline;

    public void setDeadline(androidx.recyclerview.widget.RecyclerView  rcvDeadline) {
        this.rcvDeadline=rcvDeadline;
    }
    TextView noCourse, noDeadline;
    ProgressBar prgbSchedule, prgbDeadline;
    public void setSubView(TextView noCourse, TextView noDeadline, ProgressBar prgbSchedule, ProgressBar prgbDeadline) {
        this.noCourse=noCourse;
        this.noDeadline=noDeadline;
        this.prgbDeadline=prgbDeadline;
        this.prgbSchedule=prgbSchedule;
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder{
        TextView dayOfMonth;
        androidx.constraintlayout.widget.ConstraintLayout parentView;
        androidx.cardview.widget.CardView bound;

        public CalendarViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            dayOfMonth=itemView.findViewById(R.id.cellDayText);
            parentView = itemView.findViewById(R.id.parentView);
            bound=itemView.findViewById(R.id.bound);

        }
    }


}
