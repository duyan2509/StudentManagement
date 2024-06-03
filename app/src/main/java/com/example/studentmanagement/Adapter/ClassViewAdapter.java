package com.example.studentmanagement.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.ChatActivity;
import com.example.studentmanagement.ClassItem;
import com.example.studentmanagement.LectureDetailClassActivity;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.R;
import com.example.studentmanagement.StudentDetailClassActivity;
import com.example.studentmanagement.Utils.AndroidUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClassViewAdapter extends FirestoreRecyclerAdapter<Course, ClassViewAdapter.CourseViewHolder> {
    Context context;

    public ClassViewAdapter(@NonNull FirestoreRecyclerOptions<Course> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ClassViewAdapter.CourseViewHolder holder, int position, @NonNull Course model) {
        holder.courseCode.setText(model.getCode());
        holder.courseName.setText(model.getName());

        // Set click listener for item view
        holder.itemView.setOnClickListener(v -> navigateToDetailActivity(model));

        // Set click listener for view details image
        holder.viewDetails.setOnClickListener(v -> navigateToDetailActivity(model));
    }

    private void navigateToDetailActivity(Course model) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("user").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    String role = documentSnapshot.getString("role");
                    Log.d("TAG", "User role: " + role); // Log the user role
                    Intent intent;
                    if ("student".equals(role)) {
                        intent = new Intent(context, StudentDetailClassActivity.class);
                    } else {
                        intent = new Intent(context, LectureDetailClassActivity.class);
                    }
                    Log.d("TAG", "course_id: " +  model.getId());
                    intent.putExtra("classID", model.getId()); // Assuming Course class has a getId() method
                    context.startActivity(intent);
                } else {
                    Log.d("TAG", "Document does not exist");
                }
            } else {
                Log.d("TAG", "Failed to get document: ", task.getException());
            }
        });
    }

    @NonNull
    @Override
    public ClassViewAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_class_view_row,parent,false);
        return new ClassViewAdapter.CourseViewHolder(view);
    }

    class CourseViewHolder extends RecyclerView.ViewHolder{
        TextView courseCode;
        TextView courseName;
        ImageView viewDetails;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseCode = itemView.findViewById(R.id.course_code);
            courseName = itemView.findViewById(R.id.course_name);
            viewDetails = itemView.findViewById(R.id.view_details);
        }
    }
}