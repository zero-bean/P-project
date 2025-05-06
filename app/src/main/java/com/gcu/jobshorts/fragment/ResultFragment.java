package com.gcu.jobshorts.fragment;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.data.JobData;
import com.gcu.jobshorts.data.company.CompanyData;
import com.gcu.jobshorts.data.company.Tech;
import com.gcu.jobshorts.firebase.FirestoreHelper;
import com.gcu.jobshorts.firebase.SharedViewModel;
import com.gcu.jobshorts.fragment.children.FirstFragment;
import com.gcu.jobshorts.fragment.children.SecondFragment;
import com.gcu.jobshorts.fragment.children.ThirdFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.NonNull;

public class ResultFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private CompanyData selectedCompany;

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
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

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("탭 1"); break;
                        case 1: tab.setText("탭 2"); break;
                        case 2: tab.setText("탭 3"); break;
                    }
                }).attach();

        // 회사 선택 감지
        sharedViewModel.getSelectedCompany().observe(getViewLifecycleOwner(), company -> {
            if (company != null) {
                selectedCompany = company;
                Log.d("ResultFragment...", "선택된 회사: " + selectedCompany.getName());
                if (company.getWelfare() != null) {
                    Log.d("CompanyCheck", "1인평균급여액: " + selectedCompany.getWelfare().getAverageSalaryPerPerson());
                }
                if (company.getTechs() != null) {
                    for (Tech tech : selectedCompany.getTechs()) {
                        Log.d("CompanyCheck", "연구: " + tech.getTitle() + " - " + tech.getContent());
                    }
                }
            }
            else {
                Log.e("ResultFragment...", "실패");
            }
        });

        return view;
    }
}


