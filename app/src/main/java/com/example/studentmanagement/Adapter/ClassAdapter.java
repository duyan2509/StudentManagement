package com.example.studentmanagement.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.ClassItem;
import com.example.studentmanagement.OnItemClickListener;
import com.example.studentmanagement.R;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private static List<ClassItem> classItemList = null;
    private final OnItemClickListener onItemClickListener;

    public ClassAdapter(List<ClassItem> classItemList, OnItemClickListener onItemClickListener) {
        this.classItemList = classItemList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view, onItemClickListener);
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



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView classCode;
        public TextView className;
        public TextView classLecture;
        public TextView classTime;
        public OnItemClickListener onItemClickListener;

        public ViewHolder(View view, OnItemClickListener onItemClickListener) {
            super(view);
            classCode = view.findViewById(R.id.class_code);
            className = view.findViewById(R.id.class_name);
            classLecture = view.findViewById(R.id.class_lecture);
            classTime = view.findViewById(R.id.class_time);
            this.onItemClickListener = onItemClickListener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(classItemList.get(position));
                }
            }
        }
    }
}
