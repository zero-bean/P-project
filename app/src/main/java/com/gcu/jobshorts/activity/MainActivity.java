package com.gcu.jobshorts.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.firebase.SharedViewModel;
import com.gcu.jobshorts.fragment.ChatFragment;
import com.gcu.jobshorts.fragment.HomeFragment;
import com.gcu.jobshorts.fragment.ResultFragment;
import com.gcu.jobshorts.fragment.SearchFragment;
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
                int index = -1;
                int id = menuItem.getItemId();

                if (id == R.id.navigation_bar_item_1) index = 0;
                else if (id == R.id.navigation_bar_item_2) index = 1;
                else if (id == R.id.navigation_bar_item_3) index = 2;
                else if (id == R.id.navigation_bar_item_4) index = 3;

                if (index != -1) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    for (int i = 0; i < fragment.length; i++) {
                        if (i == index) {
                            transaction.show(fragment[i]);
                        } else {
                            transaction.hide(fragment[i]);
                        }
                    }
                    transaction.commit();
                }

                return true;
            }
        });

    }

    // 프래그먼트 초기화 함수
    private void initializeFragments() {
        fragmentManager = getSupportFragmentManager();
        fragment = new Fragment[4];
        fragment[0] = new HomeFragment();
        fragment[1] = new SearchFragment();
        fragment[2] = new ResultFragment();  // 요번에 작업할 것
        fragment[3] = new ChatFragment();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment[0])
                .add(R.id.fragment_container, fragment[1]).hide(fragment[1])
                .add(R.id.fragment_container, fragment[2]).hide(fragment[2])
                .add(R.id.fragment_container, fragment[3]).hide(fragment[3])
                .commit();
    }

}