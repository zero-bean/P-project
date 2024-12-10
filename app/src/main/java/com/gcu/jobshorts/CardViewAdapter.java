package com.gcu.jobshorts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
        CardView cardView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            // 레이아웃 내부의 뷰 연결
            cardView = itemView.findViewById(R.id.cardView);
            companyTextView = itemView.findViewById(R.id.companyTextView);
            selectionTextView = itemView.findViewById(R.id.selectionTextView);
            dueTextView = itemView.findViewById(R.id.dueTextView);
            techniqueTextView = itemView.findViewById(R.id.techniqueTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
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
        // 현재 position에 해당하는 JobData 가져오기
        JobData currentJob = jobDataList.get(position);

        // 데이터를 뷰에 바인딩
        holder.companyTextView.setText(currentJob.getCompany());
        holder.selectionTextView.setText(currentJob.getSelection1());
        holder.dueTextView.setText(currentJob.getDue());
        holder.techniqueTextView.setText(currentJob.getTechnique());
        holder.locationTextView.setText(currentJob.getLocation());
        // 필요에 따라 추가 데이터 바인딩
    }

    @Override
    public int getItemCount() {
        // 데이터 리스트 크기 반환
        Log.e("test", "size = " + jobDataList.size());
        return jobDataList.size();
    }

}
