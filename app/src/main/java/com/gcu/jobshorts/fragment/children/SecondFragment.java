package com.gcu.jobshorts.fragment.children;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.data.company.CompanyData;
import com.gcu.jobshorts.data.company.Welfare;
import com.gcu.jobshorts.firebase.SharedViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.NonNull;


public class SecondFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getSelectedCompany().observe(getViewLifecycleOwner(), selectedCompany -> {
            if (selectedCompany != null) {
                showInsights(selectedCompany);
                showFlexibleWorkInfo(selectedCompany.getWelfare().getFlexibleWorkUsage());
            }
        });

    }

    private void showInsights(CompanyData company) {
        if (company.getWelfare() != null) {
            Welfare welfare = company.getWelfare();
            String maleUsage = welfare.getParentalLeaveUsageMale();
            String femaleUsage = welfare.getParentalLeaveUsageFemale();
            int malePercent = extractPercent(maleUsage);
            int femalePercent = extractPercent(femaleUsage);

            Log.d("SecondFragment", "malePercent = " + malePercent);
            Log.d("SecondFragment", "FemalePercent = " + femalePercent);

            // malePieChart와 femalePieChart를 XML에서 가져오기
            PieChart malePieChart = requireView().findViewById(R.id.malePieChart);
            PieChart femalePieChart = requireView().findViewById(R.id.femalePieChart);

            // 차트에 데이터 설정
            setupPieChart(malePieChart, "남성", malePercent, Color.parseColor("#4FC3F7")); // 파란색
            setupPieChart(femalePieChart, "여성", femalePercent, Color.parseColor("#F06292")); // 분홍색

            TextView desc = requireView().findViewById(R.id.parentalLeaveDescription);
            desc.setText("남성은 " + malePercent + "%, 여성은 " + femalePercent + "% 가 이용을 했어요!");
        }
    }
    private void setupPieChart(PieChart chart, String label, int percent, int color) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(percent, label));
        entries.add(new PieEntry(100 - percent, ""));

        PieDataSet dataSet = new PieDataSet(entries, null);
        dataSet.setColors(color, Color.LTGRAY);
        dataSet.setDrawValues(false);

        PieData data = new PieData(dataSet);

        chart.setData(data);
        chart.setUsePercentValues(false);

        // 도넛 스타일 + 가운데 수치
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(60f);
        chart.setTransparentCircleRadius(65f);
        chart.setDrawCenterText(true);
        chart.setCenterText(percent + "%");
        chart.setCenterTextSize(16f);
        chart.setCenterTextColor(Color.BLACK);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.invalidate();
    }
    private int extractPercent(String percentStr) {
        if (percentStr == null) {
            Log.d("SecondFragment: ", "percentStr is null");
            return 0;
        }

        try {
            // 숫자만 남기고 나머지 제거
            String cleaned = percentStr.replaceAll("[^\\d.]+", "");

            if (cleaned.isEmpty()) {
                Log.d("SecondFragment", "extractPercent: cleaned string is empty");
                return 0;
            }

            float value = Float.parseFloat(cleaned);
            return Math.round(value);
        } catch (NumberFormatException e) {
            Log.d("SecondFragment", "extractPercent parsing failed: " + percentStr);
            return 0;
        }
    }


    private void showFlexibleWorkInfo(String status) {
        TextView changeText = requireView().findViewById(R.id.flexibleWorkChangeText);
        TextView comment = requireView().findViewById(R.id.flexibleWorkComment);
        ImageView icon = requireView().findViewById(R.id.flexibleWorkIcon);

        if (status.contains("증가")) {
            icon.setImageResource(R.drawable.trending_up_24px); // 상승 아이콘
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green));
            changeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green));
            changeText.setText(status);
            comment.setText("작년에 비해 유연근무제를 " + extractCount(status) + "명 더 사용하고 있어요!");
        } else if (status.contains("감소")) {
            icon.setImageResource(R.drawable.trending_down_24px);
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.red));
            changeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
            changeText.setText(status);
            comment.setText("작년에 비해 유연근무제를 " + extractCount(status) + "명 덜 사용하고 있어요!");
        } else if (status.contains("변동없음")) {
            icon.setImageResource(R.drawable.pause_24px);
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gray));
            changeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
            changeText.setText("변동 없음 / 전년도 대비");
            comment.setText("변함없이 잘 사용하고 있어요!");
        } else if (status.contains("활용X")) {
            icon.setImageResource(R.drawable.block_24px);
            icon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.lightGray));
            changeText.setTextColor(ContextCompat.getColor(requireContext(), R.color.lightGray));
            changeText.setText("유연근무제 활용 없음");
            comment.setText("이 회사는 유연근무제를 활용하고 있지 않아요!");
        }
    }

    private String extractCount(String status) {
        // "35명 증가/전년도대비" → "35"
        return status.split("명")[0].replaceAll("[^0-9]", "");
    }

}
