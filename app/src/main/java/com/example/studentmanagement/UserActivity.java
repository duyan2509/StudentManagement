package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // Kiểm tra flag trong Intent để hiển thị MyClass fragment
        boolean showFragmentMyClass = getIntent().getBooleanExtra("show_fragment_my_class", false);

        if (savedInstanceState == null) {
            Fragment initialFragment = showFragmentMyClass ? new MyClass() : new Home();
            getSupportFragmentManager().beginTransaction().replace(R.id.user_container, initialFragment).commitAllowingStateLoss();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home)
                    selectedFragment = new Home();
                else if (itemId == R.id.navigation_myclass)
                    selectedFragment = new MyClass();
                else if (itemId == R.id.navigation_inbox)
                    selectedFragment = new Inbox();
                else if (itemId == R.id.navigation_profile)
                    selectedFragment = new Profile();

                if (selectedFragment != null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.user_container, selectedFragment).commitAllowingStateLoss();
                return true;
            }
        });

        // Get the currently logged-in user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("user").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String role = document.getString("role");

                        // Pass role to fragment
                        Bundle bundle = new Bundle();
                        bundle.putString("role", role);
                        MyClass myClassFragment = new MyClass();
                        myClassFragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.user_container, myClassFragment)
                                .commitAllowingStateLoss();
                    }
                }
            });
        }
    }

    public static class DetailClassStudent extends AppCompatActivity{
        Button back;
        @SuppressLint("MissingInflatedId")
        //@Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.detail_class_student);
            ImageButton back = findViewById(R.id.ImageButton);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            Button document = findViewById(R.id.button_text1);
            document.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            Button assignment = findViewById(R.id.button_text2);
            assignment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            Button listClasses = findViewById(R.id.back_list_class);
            listClasses.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }
    }
}