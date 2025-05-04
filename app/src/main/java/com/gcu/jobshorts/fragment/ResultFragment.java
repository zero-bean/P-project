package com.gcu.jobshorts.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.fragment.children.FirstFragment;
import com.gcu.jobshorts.fragment.children.SecondFragment;
import com.gcu.jobshorts.fragment.children.ThirdFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import lombok.NonNull;

public class ResultFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 1: return new SecondFragment();
                    case 2: return new ThirdFragment();
                    default: return new FirstFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        // 탭 연결
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("탭 1"); break;
                        case 1: tab.setText("탭 2"); break;
                        case 2: tab.setText("탭 3"); break;
                    }
                }).attach();

        return view;
    }
}


