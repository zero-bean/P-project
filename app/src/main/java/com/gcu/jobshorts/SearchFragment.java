package com.gcu.jobshorts;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private CardViewAdapter adapter;
    private List<JobData> jobDataList;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        // ViewModel 연결
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 초기 jobDataList 설정
        jobDataList = new ArrayList<>();

        // RecyclerView 설정
        recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter 설정
        adapter = new CardViewAdapter(getContext(), jobDataList);
        recyclerView.setAdapter(adapter);

        // SharedViewModel에서 jobDataList를 관찰
        sharedViewModel.getJobDataList().observe(getViewLifecycleOwner(), new Observer<List<JobData>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<JobData> jobList) {
                if (jobList != null) {
                    jobDataList.clear();
                    jobDataList.addAll(jobList);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
