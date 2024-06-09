package com.example.studentmanagement;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.SearchChatAdapter;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchChatActivity extends AppCompatActivity {
    private ImageButton backButton;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchChatAdapter adapter;
    private List<Course> allCourses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_chat);

        backButton = findViewById(R.id.back_btn);
        searchView = findViewById(R.id.sv_item);
        recyclerView = findViewById(R.id.search_course_recycler_view);

        backButton.setOnClickListener(v -> {
            onBackPressed();
        });

        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCourses(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCourses(newText);
                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchChatAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        loadAllCourses();
    }

    private void loadAllCourses() {
        CollectionReference userCourseRef = FirebaseUtil.getAllUserCourses();
        CollectionReference courseRef = FirebaseUtil.getAllCourse();
        List<String> courseList = new ArrayList<>();

        userCourseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> data = document.getData();
                        String user_id = data.get("user_id").toString();

                        if (user_id.equals(FirebaseUtil.currentUserId())) {
                            courseList.add(data.get("course_id").toString());
                        }
                    }

                    if (!courseList.isEmpty()) {
                        courseRef.whereIn("id", courseList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Course course = document.toObject(Course.class);
                                        allCourses.add(course);
                                    }
                                    // Initialize the adapter with all courses
                                    adapter.updateCourses(allCourses);
                                } else {
                                    Log.d("TAG", "Error getting courses: ", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d("TAG", "No courses found for the user");
                    }
                } else {
                    Log.d("TAG", "Error getting user courses: ", task.getException());
                }
            }
        });
    }

    private void filterCourses(String searchTerm) {
        List<Course> filteredList = new ArrayList<>();
        for (Course course : allCourses) {
            if (course.getCode().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    course.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                filteredList.add(course);
            }
        }
        adapter.updateCourses(filteredList);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter = null;
        }
        finish();
    }
}
