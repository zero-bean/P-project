package com.gcu.jobshorts.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.fragment.chat.ChatFragment_first;
import com.gcu.jobshorts.fragment.chat.ChatFragment_second;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ChatFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // 어댑터 설정: 첫 번째는 회사 정보 챗봇, 두 번째는 직업 추천 챗봇
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @androidx.annotation.NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 1) {
                    return new ChatFragment_second();  // 직업 추천용
                } else {
                    return new ChatFragment_first(); // 회사 정보용
                }
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        // 탭 연결
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("회사 질문");
                    break;
                case 1:
                    tab.setText("직업 추천");
                    break;
            }
        }).attach();

        return view;
    }
}
