package com.gcu.jobshorts.fragment.children;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.adapter.CompanyCardAdapter;
import com.gcu.jobshorts.data.company.Financial;
import com.gcu.jobshorts.data.company.Welfare;
import com.gcu.jobshorts.firebase.SharedViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private RecyclerView recyclerView;
    private CompanyCardAdapter adapter;
    private final List<Financial> financialList = new ArrayList<>();
    private final List<Welfare> welfareList = new ArrayList<>();

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        recyclerView = view.findViewById(R.id.first_fragment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CompanyCardAdapter(getContext(), financialList, welfareList);
        recyclerView.setAdapter(adapter);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getSelectedCompany().observe(getViewLifecycleOwner(), company -> {
            if (company != null) {
                if (company.getFinancial() != null) {
                    financialList.clear();
                    financialList.addAll(Arrays.asList(company.getFinancial()));
                }
                if (company.getWelfare() != null) {
                    welfareList.clear();
                    welfareList.addAll(Arrays.asList(company.getWelfare()));
                }
                adapter.notifyDataSetChanged(); // 한 번만 호출
            }
        });

        return view;
    }
}
