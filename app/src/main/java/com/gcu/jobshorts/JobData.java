package com.gcu.jobshorts;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobData {
    private String company;
    private String selection1;
    private String selection1_url;
    private String due;
    private String technique;
    private String career_min;
    private String carrer_max;
    private String education_level;
    private String employment_type;
    private String location;
    private String skill;

    public JobData(Map<String, Object> jobMap) {
        this.company = (String) jobMap.get("company");
        this.selection1 = (String) jobMap.get("selection1");
        this.selection1_url = (String) jobMap.get("selection1_url");
        this.due = (String) jobMap.get("due");
        this.technique = (String) jobMap.get("technique");
        this.career_min = (String) jobMap.get("career_min");
        this.carrer_max = (String) jobMap.get("carrer_max");
        this.education_level = (String) jobMap.get("education_level");
        this.employment_type = (String) jobMap.get("employment_type");
        this.location = (String) jobMap.get("location");
        this.skill = (String) jobMap.get("skill");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        JobData jobData = (JobData) obj;

        return company.equals(jobData.company)
                && technique.equals(jobData.technique)
                && employment_type.equals(jobData.employment_type);
    }
}
