package com.gcu.jobshorts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.data.user.Career;

import java.util.List;

import lombok.NonNull;

public class CareerAdapter extends RecyclerView.Adapter<CareerAdapter.CareerViewHolder> {

    private List<Career> careerList;
    private Context context;

    public CareerAdapter(List<Career> careerList, Context context) {
        this.careerList = careerList;
        this.context = context;
    }

    @NonNull
    @Override
    public CareerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.career_item, parent, false);
        return new CareerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CareerViewHolder holder, int position) {
        Career career = careerList.get(position);
        holder.careerText.setText(career.getCompany() + " / " + career.getPeriod() + "개월");

        holder.deleteBtn.setOnClickListener(v -> {
            careerList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, careerList.size());
        });
    }

    @Override
    public int getItemCount() {
        return careerList.size();
    }

    public static class CareerViewHolder extends RecyclerView.ViewHolder {

        TextView careerText;
        ImageButton deleteBtn;

        public CareerViewHolder(View itemView) {
            super(itemView);
            careerText = itemView.findViewById(R.id.text_career);
            deleteBtn = itemView.findViewById(R.id.btn_delete_career);
        }
    }
}
