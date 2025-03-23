package com.gcu.jobshorts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.data.user.Specification;

import java.util.List;

import lombok.NonNull;

public class SpecificationAdapter extends RecyclerView.Adapter<SpecificationAdapter.SpecificationViewHolder> {

    private List<Specification> specList;
    private Context context;

    public SpecificationAdapter(List<Specification> specList, Context context) {
        this.specList = specList;
        this.context = context;
    }

    @NonNull
    @Override
    public SpecificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.spec_item, parent, false);
        return new SpecificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecificationViewHolder holder, int position) {
        Specification specification = specList.get(position);
        holder.specText.setText(specification.getTitle());

        holder.deleteBtn.setOnClickListener(v -> {
            specList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, specList.size());
        });
    }

    @Override
    public int getItemCount() {
        return specList.size();
    }

    public static class SpecificationViewHolder extends RecyclerView.ViewHolder {

        TextView specText;
        ImageButton deleteBtn;

        public SpecificationViewHolder(View itemView) {
            super(itemView);
            specText = itemView.findViewById(R.id.text_spec);
            deleteBtn = itemView.findViewById(R.id.btn_delete_spec);
        }
    }
}

