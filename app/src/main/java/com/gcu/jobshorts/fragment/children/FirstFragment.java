package com.gcu.jobshorts.fragment.children;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gcu.jobshorts.R;

public class FirstFragment extends Fragment {
    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // 레이아웃 XML 연결
        return inflater.inflate(R.layout.fragment_first, container, false);
    }
}
