package com.gcu.jobshorts.data.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Financial {
    private String unit;

    // 요약별도재무정보
    private long separateBasicEPS2022;
    private long separateBasicEPS2023;
    private long separateBasicEPS2024;
    private double separateBasicEPSIncrease2024;

    private long separateNetIncome2022;
    private long separateNetIncome2023;
    private long separateNetIncome2024;
    private double separateNetIncomeIncrease2024;

    private long separateRevenue2022;
    private long separateRevenue2023;
    private long separateRevenue2024;
    private double separateRevenueIncrease2024;

    private long separateOperatingProfit2022;
    private long separateOperatingProfit2023;
    private long separateOperatingProfit2024;
    private double separateOperatingProfitIncrease2024;

    private long separateTotalAssets2022;
    private long separateTotalAssets2023;
    private long separateTotalAssets2024;
    private double separateTotalAssetsIncrease2024;

    // 요약연결재무정보
    private long consolidatedBasicEPS2022;
    private long consolidatedBasicEPS2023;
    private long consolidatedBasicEPS2024;
    private double consolidatedBasicEPSIncrease2024;

    private long consolidatedNetIncome2022;
    private long consolidatedNetIncome2023;
    private long consolidatedNetIncome2024;
    private double consolidatedNetIncomeIncrease2024;

    private long consolidatedRevenue2022;
    private long consolidatedRevenue2023;
    private long consolidatedRevenue2024;
    private double consolidatedRevenueIncrease2024;

    private long consolidatedOperatingProfit2022;
    private long consolidatedOperatingProfit2023;
    private long consolidatedOperatingProfit2024;
    private double consolidatedOperatingProfitIncrease2024;

    private long consolidatedTotalAssets2022;
    private long consolidatedTotalAssets2023;
    private long consolidatedTotalAssets2024;
    private double consolidatedTotalAssetsIncrease2024;
}


