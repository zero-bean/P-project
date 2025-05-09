package com.gcu.jobshorts.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.data.company.Financial;
import com.gcu.jobshorts.data.company.Welfare;

import java.util.List;

public class CompanyCardAdapter extends RecyclerView.Adapter<CompanyCardAdapter.CompanyViewHolder>{
    private final Context context;
    private final List<Financial> financialList;
    private final List<Welfare> welfareList;

    public CompanyCardAdapter(Context context, List<Financial> financialList, List<Welfare> welfareList) {
        this.context = context;
        this.financialList = financialList;
        this.welfareList = welfareList;
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {
        final TextView[] tv;
        final TextView[] tv2;
        CardView cardView;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.companyCardView);

            this.tv = new TextView[] {
                    itemView.findViewById(R.id.salaryContent),
                    itemView.findViewById(R.id.work_systemContent),
                    itemView.findViewById(R.id.man_parental_leaveContent),
                    itemView.findViewById(R.id.woman_parental_leaveContent),
                    itemView.findViewById(R.id.parental_leave_longevityContent),
                    itemView.findViewById(R.id.parental_leave_reinstatementContent),
                    itemView.findViewById(R.id.remunerationContent),
                    itemView.findViewById(R.id.number_employeesContent),
                    itemView.findViewById(R.id.years_serviceContent),
            };
            this.tv2 = new TextView[] {
                    itemView.findViewById(R.id.Total_assets_2024Content),
                    itemView.findViewById(R.id.Total_assets_2023Content),
                    itemView.findViewById(R.id.Total_assets_2022Content),
                    itemView.findViewById(R.id.Total_assets_rateContent)
            };
        }
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(context).inflate(R.layout.cardview_company, parent, false);
        return new CompanyViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        Financial financial = financialList.get(position);
        Welfare welfare = welfareList.get(position);

        holder.tv[0].setText(welfare.getAverageSalaryPerPerson());
        holder.tv[1].setText(welfare.getFlexibleWorkUsage());
        holder.tv[2].setText(welfare.getParentalLeaveUsageMale());
        holder.tv[3].setText(welfare.getParentalLeaveUsageFemale());
        holder.tv[4].setText(String.valueOf(welfare.getPostParentalLeaveRetention()));
        holder.tv[5].setText(welfare.getParentalLeaveReturnRate());
        holder.tv[6].setText(welfare.getExecutiveCompensation());
        holder.tv[7].setText(welfare.getEmployeeCount());
        holder.tv[8].setText(welfare.getAverageTenure());

        holder.tv2[0].setText(String.valueOf(financial.getSeparateBasicEPS2024()));
        holder.tv2[1].setText(String.valueOf(financial.getSeparateBasicEPS2023()));
        holder.tv2[2].setText(String.valueOf(financial.getSeparateBasicEPS2022()));
        holder.tv2[3].setText(String.valueOf(financial.getSeparateBasicEPSIncrease2024()));

    }

    @Override
    public int getItemCount() {
        return welfareList.size();
    }
}
