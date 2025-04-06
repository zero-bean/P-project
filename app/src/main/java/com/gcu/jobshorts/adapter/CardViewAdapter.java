package com.gcu.jobshorts.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.data.JobData;

import java.util.List;

import lombok.Setter;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
    private final Context context;
    @Setter
    private List<JobData> jobDataList; // JobData 리스트로 변경


    // 어댑터 생성자
    public CardViewAdapter(Context context, List<JobData> jobDataList) {
        this.context = context;
        this.jobDataList = jobDataList;
    }

    // ViewHolder 클래스
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView companyTextView, selectionTextView, dueTextView, techniqueTextView, locationTextView;
        TextView qualificationsTitle, qualificationsView, treatTitle, treatView;
        CardView cardView;
        LinearLayout cardContentLayout;

        boolean isExpanded = false;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            cardContentLayout = itemView.findViewById(R.id.cardContentLayout);

            companyTextView = itemView.findViewById(R.id.companyTextView);
            selectionTextView = itemView.findViewById(R.id.selectionTextView);
            dueTextView = itemView.findViewById(R.id.dueTextView);
            techniqueTextView = itemView.findViewById(R.id.techniqueTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);

            qualificationsTitle = itemView.findViewById(R.id.qualificationsTitle);
            qualificationsView = itemView.findViewById(R.id.qualificationsView);
            treatTitle = itemView.findViewById(R.id.treatTitle);
            treatView = itemView.findViewById(R.id.treatView);
        }

        public void toggleExpand() {
            if (isExpanded) {
                // 축소
                qualificationsTitle.setVisibility(View.GONE);
                qualificationsView.setVisibility(View.GONE);
                treatTitle.setVisibility(View.GONE);
                treatView.setVisibility(View.GONE);

                cardView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(300)
                        .start();

            } else {
                // 확대 + 상세 정보 보여주기
                qualificationsTitle.setVisibility(View.VISIBLE);
                qualificationsView.setVisibility(View.VISIBLE);
                treatTitle.setVisibility(View.VISIBLE);
                treatView.setVisibility(View.VISIBLE);

                cardView.animate()
                        .scaleX(1.05f)
                        .scaleY(1.05f)
                        .setDuration(300)
                        .start();
            }

            isExpanded = !isExpanded;
        }
    }


    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // cardview 레이아웃을 inflate
        View cardView = LayoutInflater.from(context).inflate(R.layout.cardview, parent, false);
        return new CardViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        JobData currentJob = jobDataList.get(position);

        holder.companyTextView.setText(currentJob.getCompany());
        holder.selectionTextView.setText(currentJob.getSelection1());
        holder.dueTextView.setText(currentJob.getDue());
        holder.techniqueTextView.setText(currentJob.getTechnique());
        holder.locationTextView.setText(currentJob.getLocation());
        holder.qualificationsView.setText(currentJob.getQualifications());
        holder.treatView.setText(currentJob.getTreat());

        holder.cardView.setScaleX(1f);
        holder.cardView.setScaleY(1f);
        holder.qualificationsTitle.setVisibility(View.GONE);
        holder.qualificationsView.setVisibility(View.GONE);
        holder.treatTitle.setVisibility(View.GONE);
        holder.treatView.setVisibility(View.GONE);
        holder.isExpanded = false;

        holder.cardView.setOnClickListener(v -> holder.toggleExpand());
    }


    @Override
    public int getItemCount() {
        // 데이터 리스트 크기 반환
        Log.e("test", "size = " + jobDataList.size());
        return jobDataList.size();
    }
}

