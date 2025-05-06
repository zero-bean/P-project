package com.gcu.jobshorts.data.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Welfare {
    // 1인 평균 급여액 (문자열 예: "94,802(천원)")
    private String averageSalaryPerPerson;
    // 유연근무제도 사용 현황 (예: "200명증가/전년도대비")
    private String flexibleWorkUsage;
    // 육아휴직 사용률 (남성, 문자열 예: "16.0%")
    private String parentalLeaveUsageMale;
    // 육아휴직 사용률 (여성, 문자열 예: "100.0%")
    private String parentalLeaveUsageFemale;
    // 육아휴직 복귀 후 12개월 이상 근속자 수 (숫자)
    private int postParentalLeaveRetention;
    // 육아휴직 복귀율 (예: "??" 혹은 "95.0%")
    private String parentalLeaveReturnRate;
    // 임원 보수 정보 (예: "7명 / 1,344,161(천원)")
    private String executiveCompensation;
    // 전체 직원 수 (문자열 예: "1,459명")
    private String employeeCount;
    // 평균 근속 연수 (예: "3년 4개월")
    private String averageTenure;

}
