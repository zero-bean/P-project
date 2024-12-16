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
        TextView companyTextView, selectionTextView, dueTextView, techniqueTextView, locationTextView, qualificationsView, treatView;
        CardView cardView;

        View cardFront, cardMiddle, cardBack; // 3개 카드뷰 추가
        int isFrontVisible = 1; // 초기 상태

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);

            // 레이아웃 내부의 뷰 연결
            cardView = itemView.findViewById(R.id.cardView);
            cardFront = itemView.findViewById(R.id.cardFront);
            cardMiddle = itemView.findViewById(R.id.cardMiddle);
            cardBack = itemView.findViewById(R.id.cardBack);

            companyTextView = itemView.findViewById(R.id.companyTextView);
            selectionTextView = itemView.findViewById(R.id.selectionTextView);
            dueTextView = itemView.findViewById(R.id.dueTextView);
            techniqueTextView = itemView.findViewById(R.id.techniqueTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            qualificationsView = itemView.findViewById(R.id.qualificationsView);
            treatView = itemView.findViewById(R.id.treatView);
        }

        public void flipCard() {
            if (isFrontVisible == 1) {
                animateFlip(cardFront, cardMiddle);
            } else if (isFrontVisible == 2) {
                animateFlip(cardMiddle, cardBack);
            } else if (isFrontVisible == 3) {
                animateFlip(cardBack, cardFront);
            }

            // 상태 업데이트
            isFrontVisible++;
            if (isFrontVisible == 4) {
                isFrontVisible = 1;
            }
        }

        private void animateFlip(View currentView, View nextView) {
            currentView.animate()
                    .rotationY(90f) // 현재 뷰를 90도 회전
                    .setDuration(1000) // 애니메이션 시간 설정
                    .withEndAction(() -> {
                        currentView.setVisibility(View.GONE); // 현재 뷰 숨기기
                        nextView.setVisibility(View.VISIBLE); // 다음 뷰 표시
                        nextView.setRotationY(90f); // 다음 뷰 초기 상태 설정
                        nextView.animate()
                                .rotationY(0f) // 다음 뷰를 0도로 회전
                                .setDuration(1000) // 애니메이션 시간 설정
                                .start(); // 애니메이션 시작
                    }).start(); // 애니메이션 시작
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
        holder.qualificationsView.setText(currentJob.getQualifications());
        holder.treatView.setText(currentJob.getTreat());
        // 필요에 따라 추가 데이터 바인딩

        holder.cardView.setOnClickListener(v -> holder.flipCard());
    }

    @Override
    public int getItemCount() {
        // 데이터 리스트 크기 반환
        Log.e("test", "size = " + jobDataList.size());
        return jobDataList.size();
    }
}

