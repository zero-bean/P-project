package com.gcu.jobshorts;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel;
    FragmentManager fragmentManager;
    Fragment fragment[];
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedViewModel 초기화
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // 프래그먼트 초기화
        initializeFragments();

        // BottomNavigation 설정
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.navigation_bar_item_1) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment[0]).commit();
                } else if (id == R.id.navigation_bar_item_2) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment[1]).commit();
                } else if (id == R.id.navigation_bar_item_3) {
                    //
                }

                return true;
            }
        });
    }

    // 프래그먼트 초기화 함수
    private void initializeFragments() {
        fragmentManager = getSupportFragmentManager();
        fragment = new Fragment[3];
        fragment[0] = new HomeFragment();
        fragment[1] = new SearchFragment();
        fragment[2] = null;

        fragmentManager.beginTransaction().add(R.id.fragment_container, fragment[0]).commit();
    }
}