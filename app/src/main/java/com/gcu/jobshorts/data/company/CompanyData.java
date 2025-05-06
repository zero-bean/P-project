package com.gcu.jobshorts.data.company;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyData {
    // 1. 회사 이름
    private String name;
    // 2. 복지 지표
    private Welfare welfare;
    // 3. 연구 현황
    private Tech techs[];
    // 4. 재무 제표
    private Financial financial;

    public CompanyData(Map<String, Object> companyMap) {
        this.name = (String) companyMap.get("기업명");
        // 1. 복지지표
        Map<String, Object> welfareMap = (Map<String, Object>) companyMap.get("복지지표");
        this.welfare = new Welfare();
        if (welfareMap != null) {
            this.welfare.setAverageSalaryPerPerson((String) welfareMap.get("1인평균급여액"));
            this.welfare.setFlexibleWorkUsage((String) welfareMap.get("유연근무제도 사용 현황"));
            this.welfare.setParentalLeaveUsageMale((String) welfareMap.get("육아육아휴직 사용률(남)"));
            this.welfare.setParentalLeaveUsageFemale((String) welfareMap.get("육아휴직 사용률(여)"));
            this.welfare.setParentalLeaveReturnRate((String) welfareMap.get("육아휴직복귀율"));
            this.welfare.setExecutiveCompensation((String) welfareMap.get("임원의 보수(인원수/보수총액)"));
            this.welfare.setEmployeeCount((String) welfareMap.get("직원수"));
            this.welfare.setAverageTenure((String) welfareMap.get("평균근속연수"));
        }
        // 2.연구현황
        List<Map<String, Object>> techList = (List<Map<String, Object>>) companyMap.get("연구현황");
        if (techList != null) {
            this.techs = new Tech[techList.size()];
            for (int i = 0; i < techList.size(); i++) {
                Map<String, Object> techMap = techList.get(i);
                this.techs[i] = new Tech((String) techMap.get("title"), (String) techMap.get("content"));
            }
        } else {
            this.techs = new Tech[0];
        }
        // 3.요약재무정보
            Map<String, Object> financialMap = (Map<String, Object>) companyMap.get("요약재무정보");
            if (financialMap != null) {
                Financial financial = new Financial();
                financial.setUnit((String) financialMap.get("단위"));

                Map<String, Object> separateMap = (Map<String, Object>) financialMap.get("요약별도재무정보");
                Map<String, Object> consolidatedMap = (Map<String, Object>) financialMap.get("요약연결재무정보");

                if (separateMap != null) {
                    extractFinancialInfo(separateMap, financial, true);
                }
                if (consolidatedMap != null) {
                    extractFinancialInfo(consolidatedMap, financial, false);
                }

                this.financial = financial;
            }
    }

    @SuppressWarnings("unchecked")
    private void extractFinancialInfo(Map<String, Object> sourceMap, Financial financial, boolean isSeparate) {
        setFinancialValues((Map<String, Object>) sourceMap.get("자산총계"), isSeparate, "TotalAssets", financial);
        setFinancialValues((Map<String, Object>) sourceMap.get("영업수익"), isSeparate, "Revenue", financial);
        setFinancialValues((Map<String, Object>) sourceMap.get("영업이익"), isSeparate, "OperatingProfit", financial);
        setFinancialValues((Map<String, Object>) sourceMap.get("당기순이익"), isSeparate, "NetIncome", financial);
        setFinancialValues((Map<String, Object>) sourceMap.get("기본주당이익(원)"), isSeparate, "BasicEPS", financial);
    }

    private void setFinancialValues(Map<String, Object> map, boolean isSeparate, String field, Financial financial) {
        if (map == null) return;

        long y2022 = ((Number) map.get("2022")).longValue();
        long y2023 = ((Number) map.get("2023")).longValue();
        long y2024 = ((Number) map.get("2024")).longValue();
        double increase = ((Number) map.get("전년대비증감률(2024)")).doubleValue();

        String prefix = isSeparate ? "separate" : "consolidated";
        try {
            Field f2022 = Financial.class.getDeclaredField(prefix + field + "2022");
            Field f2023 = Financial.class.getDeclaredField(prefix + field + "2023");
            Field f2024 = Financial.class.getDeclaredField(prefix + field + "2024");
            Field fInc = Financial.class.getDeclaredField(prefix + field + "Increase2024");

            f2022.setAccessible(true);
            f2023.setAccessible(true);
            f2024.setAccessible(true);
            fInc.setAccessible(true);

            f2022.setLong(financial, y2022);
            f2023.setLong(financial, y2023);
            f2024.setLong(financial, y2024);
            fInc.setDouble(financial, increase);
        } catch (Exception e) {
            Log.e("FinancialParser", "Reflection error: " + e.getMessage());
        }
    }
}
