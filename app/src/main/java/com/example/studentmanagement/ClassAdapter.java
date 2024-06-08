package com.example.studentmanagement;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private final List<ClassItem> classItemList;
    private final Context context;
    //    private String userRole;
    public ClassAdapter(List<ClassItem> classItemList,Context context) {

        this.classItemList = classItemList;
        this.context=context;
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
        holder.bind(classItem);
    }

    @Override
    public int getItemCount() {
        return classItemList.size();
    }

    private void fetchUserRole() {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView classCode;
        public TextView className;
        public TextView classLecture;
        public TextView classTime;

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
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("user").document(userId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                String userRole = documentSnapshot.getString("role");
                                Log.d("TAG", "Successfully get userRole:  " + userRole);
                                Intent intent;
                                if ("student".equals(userRole)) {
                                    intent = new Intent(context, StudentDetailClassActivity.class);
                                } else if ("lecture".equals(userRole)) {
                                    intent = new Intent(context, LectureDetailClassActivity.class);
                                } else {
                                    Log.d("TAG", "Invalid user role:  " + userRole);
                                    return;
                                }
                                intent.putExtra("classID", classItem.getClassID());
                                intent.putExtra("code", classItem.getClassCode());
                                intent.putExtra("name", classItem.getClassName());
                                intent.putExtra("lecture", classItem.getClassLecture());
                                intent.putExtra("time", classItem.getClassTime());
                                context.startActivity(intent);
                            }
                        }
                    });
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