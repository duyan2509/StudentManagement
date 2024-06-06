package com.example.studentmanagement.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studentmanagement.ClassListActivity;
import com.example.studentmanagement.Domain.UserRepository;
import com.example.studentmanagement.Model.User;
import com.example.studentmanagement.Model.UserCourse;
import com.example.studentmanagement.ProfilePageActivity;
import com.example.studentmanagement.R;
import com.example.studentmanagement.StudentDetailClassActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> implements Filterable {
    public PersonAdapter(List<UserCourse> userCourseList) {
        this.userCourseList = userCourseList;
        this.oldUserCourseList= userCourseList;
        setName();
    }
    @SuppressLint("NotifyDataSetChanged")
    private void setName() {
        if (userCourseList.isEmpty()) {
            return;
        }

        final int totalTasks = userCourseList.size();
        final int[] completedTasks = {0};

        for (int i = 0; i < userCourseList.size(); i++) {
            UserRepository userRepository = new UserRepository();
            int finalI = i;
            userRepository.getUserById(userCourseList.get(i).getUser_id()).addOnCompleteListener(new OnCompleteListener<User>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onComplete(@NonNull Task<User> task) {
                    if (task.isSuccessful()) {
                        User user = task.getResult();
                        if (user != null && user.getName() != null) {
                            Log.i("set name", user.getName());
                            oldUserCourseList.get(finalI).setUser_name(user.getName());
                        } else {
                            oldUserCourseList.get(finalI).setUser_name("Chua thiet lap ten tren firestore");
                        }
                    }
                    synchronized (completedTasks) {
                        completedTasks[0]++;
                        if (completedTasks[0] == totalTasks) {
                            notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    Context context;


    List<UserCourse> userCourseList,oldUserCourseList;
    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_list_item, parent,false);
        return new PersonAdapter.PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        UserCourse userCourse = userCourseList.get(position);
        if(userCourse==null)
            return;

        if(Objects.equals(userCourse.getRole(), "lecturer"))
        {
            holder.lecturer.setVisibility(View.VISIBLE);
            holder.student_code.setVisibility(View.GONE);
        }
        else{
            holder.lecturer.setVisibility(View.GONE);
            holder.student_code.setVisibility(View.VISIBLE);
        }

        holder.bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProfilePageActivity.class);
                intent.putExtra("userId",userCourse.getUser_id());
                context.startActivity(intent);
            }
        });

        UserRepository userRepository=new UserRepository(context);
        userRepository.getUserById(userCourse.getUser_id()).addOnCompleteListener(new OnCompleteListener<User>() {
            @Override
            public void onComplete(@NonNull Task<User> task) {
                if(task.isSuccessful()){
                    User user = task.getResult();
                    if(user!=null){
                        if(user.getName()!=null)
                            holder.name.setText(user.getName());
                        else
                            holder.name.setText("chua dat ten tren firestore");

                        if(user.getProfile_image()!=null)
                            Glide.with(context)
                                    .load(user.getProfile_image())
                                    .placeholder(R.drawable.baseline_account_circle_24)
                                    .into(holder.profile_image);

                        if(Objects.equals(userCourse.getRole(), "student"))
                        {
                            if(user.getCode()!=null)
                                holder.student_code.setText(user.getCode());
                        }
                    }
                    else
                        Log.i("fetch user by id", "null");
                }
                else
                    Log.i("fetch user by id","fail");
            }
        });
    }

    @Override
    public int getItemCount() {
        if(userCourseList!=null)
            return  userCourseList.size();
        return 0;
    }



    public static class PersonViewHolder extends RecyclerView.ViewHolder{
        LinearLayout bound;
        ImageView profile_image;
        TextView student_code, lecturer, name;
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            bound=itemView.findViewById(R.id.bound);
            profile_image=itemView.findViewById(R.id.profile_image);
            student_code=itemView.findViewById(R.id.student_code);
            lecturer=itemView.findViewById(R.id.lecturer);
            name=itemView.findViewById(R.id.name);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String strSearch = charSequence.toString().trim();
                if(strSearch.isEmpty()){
                    userCourseList=oldUserCourseList;
                }
                else {
                    List<UserCourse> list = new ArrayList<>();
                    for(UserCourse userCourse:oldUserCourseList){
                        if(userCourse.getUser_name().toLowerCase().contains(strSearch.toLowerCase())){
                            list.add(userCourse);
                        }
                    }
                    userCourseList=list;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values=userCourseList;
                return filterResults;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                userCourseList= (List<UserCourse>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
