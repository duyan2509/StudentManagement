package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.studentmanagement.Utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


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
}