package com.gcu.jobshorts;

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
