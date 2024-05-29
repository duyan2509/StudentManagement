package com.example.studentmanagement.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Domain.UserCourseRepository;
import com.example.studentmanagement.Domain.UserRepository;
import com.example.studentmanagement.Model.Assignment;
import com.example.studentmanagement.Model.Submission;
import com.example.studentmanagement.R;
import com.example.studentmanagement.Utils.RoleUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DeadlineAdapter extends RecyclerView.Adapter<DeadlineAdapter.DeadlineViewHolder>{
    Map<Assignment,String> assignmentStringMap;
    List<Assignment> assignments;

    public DeadlineAdapter(Map<Assignment, String> assignmentStringMap) {
        this.assignmentStringMap = assignmentStringMap;
        this.assignments = new ArrayList<>(assignmentStringMap.keySet());
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateDate(Map<Assignment, String> assignmentStringMap){
        this.assignmentStringMap = assignmentStringMap;
        this.assignments.clear();
        this.assignments.addAll(assignmentStringMap.keySet());
        sortByDueDate();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeadlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deadline_item, parent,false);
        return new DeadlineViewHolder(view);
    }
    public String formatTimestamp(Timestamp timestamp) {
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public void onBindViewHolder(@NonNull DeadlineViewHolder holder, int position) {
        Assignment assignment = assignments.get(position);
        if(assignment==null) return;
        holder.code.setText(assignmentStringMap.get(assignment));
        holder.title.setText(assignment.getTitle());
        holder.deadline.setText(formatTimestamp(assignment.getDue_date()));
        UserRepository userRepository=new UserRepository();
        userRepository.getRole().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                String role=task.getResult();
                if(Objects.equals(role, "student")){
                    Log.i("adapter",String.valueOf(assignment.getSubmissions().size()));
                    boolean checkSubmit=checkStudentSubmit(assignment.getSubmissions());
                    if (!checkSubmit)
                    {
                        holder.submit.setVisibility(View.INVISIBLE);
                        holder.not_submit.setVisibility(View.VISIBLE);
                        holder.not_submit.setText("not yet submitted");
                    }
                    else {
                        holder.submit.setVisibility(View.VISIBLE);
                        holder.not_submit.setVisibility(View.INVISIBLE);
                        holder.submit.setText("submitted");
                    }
                }
                else if(Objects.equals(role, "lecturer"))
                {
                    UserCourseRepository userCourseRepository=new UserCourseRepository();
                    userCourseRepository.getStudentCountByCourseId(assignment.getCourseId()).addOnCompleteListener(new OnCompleteListener<Integer>() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task<Integer> task) {
                            if (task.isSuccessful()) {
                                int studentCount = task.getResult();
                                int submissionCount = assignment.getSubmissions().size();
                                if (studentCount>submissionCount)
                                {
                                    holder.submit.setVisibility(View.INVISIBLE);
                                    holder.not_submit.setVisibility(View.VISIBLE);
                                    holder.not_submit.setText(String.valueOf(submissionCount) +"/"+String.valueOf(studentCount)+" submissons");
                                }
                                else {
                                    holder.submit.setVisibility(View.VISIBLE);
                                    holder.not_submit.setVisibility(View.INVISIBLE);
                                    holder.submit.setText(String.valueOf(submissionCount) +"/"+String.valueOf(studentCount)+" submissons");
                                }
                                Log.d("StudentCount", "Number of students in the course: " + studentCount);
                            } else {
                                Log.w("StudentCount", "Error getting student count.", task.getException());
                            }
                        }
                    });
                }
            }
        });


        //red
        if(nowGreater(assignment.getDue_date()))
            holder.detail.setBackgroundColor(Color.parseColor("#FF0000"));
        else
            holder.detail.setBackgroundColor(Color.parseColor("#3364CE"));


    }

    private boolean checkStudentSubmit(List<Submission> submissions) {
        for(Submission submission:submissions){
            if(submission.getStudent_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                return true;
        }
        return false;
    }

    void sortByDueDate(){
        assignments.sort(new Comparator<Assignment>() {
            @Override
            public int compare(Assignment a1, Assignment a2) {
                return a2.getDue_date().compareTo(a1.getDue_date());
            }
        });
    }
    boolean nowGreater (Timestamp due_date) {
        boolean res = true;
        Date dueDate = due_date.toDate();
        Date currentDate = new Date();
        if (dueDate.after(currentDate)) {
            res= false;
        }
        return res;
    }

    @Override
    public int getItemCount(){
        if(assignments!=null)
            return assignments.size();
        return 0;
    }

    public static class DeadlineViewHolder extends RecyclerView.ViewHolder{
        TextView code, title, deadline, submit, not_submit;
        Button detail;

        public DeadlineViewHolder(@NonNull View itemView) {
            super(itemView);
            code = itemView.findViewById(R.id.code);
            title = itemView.findViewById(R.id.title);
            deadline = itemView.findViewById(R.id.deadline);
            submit = itemView.findViewById(R.id.submit);
            not_submit = itemView.findViewById(R.id.notSubmit);
            detail = itemView.findViewById(R.id.detail);
        }
    }
}
