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
import com.gcu.jobshorts.data.company.Tech;

import java.util.List;

import lombok.Setter;

public class TechCardAdapter extends RecyclerView.Adapter<TechCardAdapter.TechViewHolder> {
    private final Context context;
    private final List<Tech> techList;

    public TechCardAdapter(Context context, List<Tech> techList) {
        this.context = context;
        this.techList = techList;
    }

    public static class TechViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView;
        CardView cardView;

        public TechViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.techCardView);
            titleTextView = itemView.findViewById(R.id.techTitle);
            contentTextView = itemView.findViewById(R.id.techContent);
        }
    }

    @NonNull
    @Override
    public TechViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(context).inflate(R.layout.cardview_tech, parent, false);
        return new TechViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull TechViewHolder holder, int position) {
        Tech tech = techList.get(position);
        holder.titleTextView.setText(tech.getTitle());
        holder.contentTextView.setText(tech.getContent());
    }

    @Override
    public int getItemCount() {
        return techList.size();
    }
}
