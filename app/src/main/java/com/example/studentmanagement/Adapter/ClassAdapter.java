package com.example.studentmanagement.Adapter;

import android.content.Context;
import android.content.Intent;
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

    private final List<ClassItem> classItemList;
    private final Context context;
    public ClassAdapter(List<ClassItem> classItemList,Context context) {

        this.classItemList = classItemList;
        this.context=context;
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
        holder.bind(classItem);
    }

    @Override
    public int getItemCount() {
        return classItemList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView classCode;
        public TextView className;
        public TextView classLecture;
        public TextView classTime;
        public OnItemClickListener onItemClickListener;

        public ViewHolder(@NonNull View itemview) {
            super(itemview);
            classCode = itemview.findViewById(R.id.class_code);
            className = itemview.findViewById(R.id.class_name);
            classLecture = itemview.findViewById(R.id.class_lecture);
            classTime = itemview.findViewById(R.id.class_time);
            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ClassItem classItem = classItemList.get(position);
                    Intent intent = new Intent(context, LectureDetailClassActivity.class);
                    intent.putExtra("code", classItem.getClassCode());
                    intent.putExtra("name", classItem.getClassName());
                    intent.putExtra("lecture", classItem.getClassLecture());
                    intent.putExtra("time", classItem.getClassTime());
                    context.startActivity(intent);
                }
            });
        }
        public void bind(ClassItem classItem) {
            classCode.setText(classItem.getClassCode());
            className.setText(classItem.getClassName());
            classLecture.setText(classItem.getClassLecture());
            classTime.setText(classItem.getClassTime());
        }
    }
}
