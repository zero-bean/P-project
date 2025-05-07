package com.gcu.jobshorts.fragment.children;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.adapter.TechCardAdapter;
import com.gcu.jobshorts.data.company.Tech;
import com.gcu.jobshorts.firebase.SharedViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThirdFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private TechCardAdapter adapter;
    private final List<Tech> techList = new ArrayList<>();

    public ThirdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);

        recyclerView = view.findViewById(R.id.third_fragment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TechCardAdapter(getContext(), techList);
        recyclerView.setAdapter(adapter);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedCompany().observe(getViewLifecycleOwner(), company -> {
            if (company != null && company.getTechs() != null) {
                techList.clear();
                techList.addAll(Arrays.asList(company.getTechs()));
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}