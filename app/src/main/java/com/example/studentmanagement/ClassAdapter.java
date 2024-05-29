package com.example.studentmanagement;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private final List<ClassItem> classItemList;

    public ClassAdapter(List<ClassItem> classItemList) {
        this.classItemList = classItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClassItem classItem = classItemList.get(position);
        holder.classCode.setText(classItem.getClassCode());
        holder.className.setText(classItem.getClassName());
        holder.classLecture.setText(classItem.getClassLecture());
        holder.classTime.setText(classItem.getClassTime());
    }

    @Override
    public int getItemCount() {
        return classItemList.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView classCode;
        public TextView className;
        public TextView classLecture;
        public TextView classTime;

        public ViewHolder(View view) {
            super(view);
            classCode = view.findViewById(R.id.class_code);
            className = view.findViewById(R.id.class_name);
            classLecture = view.findViewById(R.id.class_lecture);
            classTime = view.findViewById(R.id.class_time);
        }
    }
}
