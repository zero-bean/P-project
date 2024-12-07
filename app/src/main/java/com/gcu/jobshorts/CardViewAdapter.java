package com.gcu.jobshorts;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {
    private List<CardModel> cardModels;

    public CardViewAdapter(List<CardModel> cardModels) {
        this.cardModels = cardModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shortcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItems(cardModels.get(position));
    }

    @Override
    public int getItemCount() {
        return cardModels.size();
    }

    // 화면에 표시 될 뷰를 저장하는 역할
    // 뷰를 재활용 하기 위해 각 요소를 저장해두고 사용한다.
    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        void bindItems(CardModel cardModel) {
            ImageView imageArea = itemView.findViewById(R.id.imageArea);
            TextView amountArea = itemView.findViewById(R.id.amountArea);
            TextView titleArea = itemView.findViewById(R.id.titleArea);

            imageArea.setImageResource(cardModel.getImage());
            amountArea.setText("근무지 " + cardModel.getAmount());
            titleArea.setText(cardModel.getTitle());

            // SpannableStringBuilder 타입으로 변환
            SpannableStringBuilder spannable = new SpannableStringBuilder(amountArea.getText().toString());
            // 인덱스(0~3)에 해당 되는 텍스트에 회색 적용
            spannable.setSpan(
                    new ForegroundColorSpan(Color.GRAY),
                    0,
                    4,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            // TextView builder 적용
            amountArea.setText(spannable);
        }
    }
}