package com.example.studentmanagement.Adapter;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studentmanagement.Model.AssignmentItem;
import com.example.studentmanagement.Model.MultiFilePickerDialog;
import com.example.studentmanagement.R;

import com.example.studentmanagement.StudentAssignmentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private List<AssignmentItem> assignments;
    private Context context;

    public AssignmentAdapter(List<AssignmentItem> assignments, Context context) {
        this.assignments = assignments;
        this.context = context;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_deadline, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        AssignmentItem assignment = assignments.get(position);
        holder.bind(assignment);
    }

    @Override
    public int getItemCount() {
        return assignments.size();
    }

    class AssignmentViewHolder extends RecyclerView.ViewHolder {

        TextView title, dueDate, status, grade;
        Button btnSubmit;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.deadline_name);
            dueDate = itemView.findViewById(R.id.deadline_time);
            grade=itemView.findViewById(R.id.grade);
            btnSubmit = itemView.findViewById(R.id.btn_submit);
        }

        public void bind(AssignmentItem assignment) {
            title.setText(assignment.getTitle());
            dueDate.setText(formatTimestamp(assignment.getDueDate()));

            // Set the initial status and button text
            if (assignment.isLate()) {
                ColorStateList colorStateList = ColorStateList.valueOf(Color.RED);
                btnSubmit.setBackgroundTintList(colorStateList);

                btnSubmit.setText("Miss");
                btnSubmit.setTextColor(Color.WHITE);
            } else if (assignment.isSubmittedLate()) {
                ColorStateList colorStateList = ColorStateList.valueOf(Color.GREEN);
                btnSubmit.setBackgroundTintList(colorStateList);
                btnSubmit.setText("Done");
                setGrade(assignment);
            } else {
                ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
                btnSubmit.setBackgroundTintList(colorStateList);
                btnSubmit.setText("Submit");
            }

            btnSubmit.setOnClickListener(v -> {
                    boolean isLate = Timestamp.now().compareTo(assignment.getDueDate()) > 0;
                    Intent intent = new Intent(context, StudentAssignmentActivity.class);
                    intent.putExtra("deadline_name", assignment.getTitle());
                    intent.putExtra("deadline_time", formatTimestamp(assignment.getDueDate()));
                    intent.putExtra("class_id", (assignment.getClassID()));
                    intent.putExtra("class_code", (assignment.getClass_code()));
                    intent.putExtra("Get_Late",isLate);
                    if(btnSubmit.getText().equals("Done"))
                        intent.putExtra("Get_Type","View");
                    else
                        intent.putExtra("Get_Type","Add");
                    context.startActivity(intent);

            });
        }

        private void setGrade(AssignmentItem assignment) {
            FirebaseFirestore.getInstance().collection("course").document(assignment.getClassID()).collection("assignment").document(assignment.getId())
                    .collection("submission")
                    .whereEqualTo("student_id", FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                grade.setVisibility(View.GONE);
                                if(documentSnapshot!=null){
                                    Long score = documentSnapshot.getLong("grade");
                                    if(score!=null){
                                        grade.setText("Grade: "+String.valueOf(score));
                                    }
                                    else
                                        grade.setText("Waiting for grade.");
                                    grade.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    });
        }

        private void handleSubmission(AssignmentItem assignment) {
            // Implement your submission logic here
            Toast.makeText(context, " Deadline đã quá hạn!", Toast.LENGTH_SHORT).show();
        }

        private String formatTimestamp(Timestamp timestamp) {
            Date date = timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }
}
