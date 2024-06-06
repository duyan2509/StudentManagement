package com.example.studentmanagement.Domain;

import android.util.Log;

import com.example.studentmanagement.Model.Assignment;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Model.Submission;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CourseRepository {
    private FirebaseFirestore db;
    private static CollectionReference course;

    public CourseRepository() {
        db = FirebaseFirestore.getInstance();
        course=db.collection("course");
    }
    public  Task<List<Course>> getCoursesByIds(String id, String schedule) {
        return course.whereEqualTo("id", id)
                .whereEqualTo("schedule", schedule)
                .get()
                .continueWith(task -> {
                    List<Course> courses = new ArrayList<>();
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Course course = new Course();
                                course.setAcademic_year(document.getString("academic_year"));
                                course.setCode(document.getString("code"));
                                course.setId(document.getId());
                                course.setName(document.getString("name"));
                                course.setRoom(document.getString("room"));
                                course.setSchedule(document.getString("schedule"));
                                course.setSemester(document.getLong("semester"));
                                course.setStart((long)document.getLong("start"));
                                course.setEnd((long)document.getLong("end"));
                                courses.add(course);
                                Log.i("course",course.toString());
                            }
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.i("getCoursesByIds", Objects.requireNonNull(exception.getMessage()));
                        }
                    }
                    return courses;
                });
    }
    public Task<Course> getCourseByCourseId(String id) {
        return course.document(id)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        Course course1=new Course();
                        if (document != null && document.exists()) {
                            course1.setId(document.getId());
                            course1.setCode(document.getString("code"));
                            course1.setName(document.getString("name"));
                            return course1;
                        } else {
                            throw new Exception("Document does not exist");
                        }
                    } else {
                        throw Objects.requireNonNull(task.getException());
                    }
                });
    }

public Task<List<Assignment>> getAssignmentByCourseId(String id) {
    return course.document(id)
            .collection("assignment")
            .get()
            .continueWithTask(task -> {
                List<Task<Void>> submissionTasks = new ArrayList<>();
                List<Assignment> assignments = new ArrayList<>();

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot assignmentDoc : task.getResult()) {
                        Assignment assignment = new Assignment();
                        assignment.setTitle(assignmentDoc.getString("title"));
                        assignment.setDue_date(assignmentDoc.getTimestamp("due_date"));

                        Task<Void> submissionTask = assignmentDoc.getReference()
                                .collection("submission")
                                .get()
                                .continueWith(subTask -> {
                                    List<Submission> submissions = new ArrayList<>();
                                    if (subTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot submissionDoc : subTask.getResult()) {
                                            Submission submission = new Submission();
                                            submission.setStudent_id(submissionDoc.getString("student_id"));
                                            Log.i("submission",submission.getStudent_id());
                                            submissions.add(submission);
                                        }
                                    } else {
                                        Log.w("Firestore", "Error getting submissions.", subTask.getException());
                                    }
                                    assignment.setSubmissions(submissions);
                                    return null;
                                });

                        submissionTasks.add(submissionTask);
                        assignments.add(assignment);
                    }
                } else {
                    Log.w("Firestore", "Error getting assignments.", task.getException());
                }

                return Tasks.whenAllComplete(submissionTasks).continueWith(completeTask -> assignments);
            });
}

}
