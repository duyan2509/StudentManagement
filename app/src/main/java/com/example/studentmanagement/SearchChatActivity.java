package com.example.studentmanagement;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studentmanagement.Adapter.SearchChatAdapter;
import com.example.studentmanagement.Model.Course;
import com.example.studentmanagement.Utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SearchChatActivity extends AppCompatActivity {
    private ImageButton backButton;
    private SearchView searchView;
    private RecyclerView recyclerView;
    SearchChatAdapter adapter;

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
                setUpSearchRecycleView(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    void setUpSearchRecycleView(String searchTerm) {
        CollectionReference courseRef = FirebaseUtil.getAllCourse();
        CollectionReference userCourseRef = FirebaseUtil.getAllUserCourses();
        List<String> courseList = new ArrayList<>();

        // Fetch all documents from the userCourseRef collection
        userCourseRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Get the course ID from the user_course document
                        Map<String, Object> data = document.getData();
                        String user_id = data.get("user_id").toString();

                        if (user_id.equals(FirebaseUtil.currentUserId())) {
                            courseList.add(data.get("course_id").toString());
                        }
                    }

                    if (!courseList.isEmpty()) {
                        Query query = courseRef.whereIn("id", courseList)
                                        .whereGreaterThanOrEqualTo("code", searchTerm)
                                        .whereLessThanOrEqualTo("code", searchTerm + "\uf8ff");

                        // Call method to set up the RecyclerView adapter
                        setUpRecyclerView(query);
                    } else {
                        // Handle case when no courses found
                        Log.d("TAG", "No courses found for the user");
                    }
                } else {
                    // Handle errors
                    Log.d("TAG", "Error getting user courses: ", task.getException());
                }
            }
        });
    }


    private void setUpRecyclerView(Query query) {
        // Create a FirestoreRecyclerOptions object with a custom query

        FirestoreRecyclerOptions<Course> options = new FirestoreRecyclerOptions.Builder<Course>()
                .setQuery(query, Course.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        adapter = new SearchChatAdapter(options, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchChatActivity.this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}
