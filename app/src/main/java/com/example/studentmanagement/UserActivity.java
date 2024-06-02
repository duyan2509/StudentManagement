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

    }

    public static class DetailClassStudent extends AppCompatActivity{
        Button back;
        @SuppressLint("MissingInflatedId")
        //@Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.detail_class_student);


        }
    }
}