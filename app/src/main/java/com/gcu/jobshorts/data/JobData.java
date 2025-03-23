package com.gcu.jobshorts.data;

import java.util.Map;
import java.util.Objects;

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
    private String career_max;
    private String education_level;
    private String employment_type;
    private String location;
    private String skill;
    private String qualifications;
    private String treat;


    public JobData(Map<String, Object> jobMap) {
        this.company = (String) jobMap.get("company");
        this.selection1 = (String) jobMap.get("selection1");
        this.selection1_url = (String) jobMap.get("selection1_url");
        this.due = (String) jobMap.get("due");
        this.technique = (String) jobMap.get("technique");
        this.career_min = (String) jobMap.get("career_min");
        this.career_max = (String) jobMap.get("carrer_max");
        this.education_level = (String) jobMap.get("education_level");
        this.employment_type = (String) jobMap.get("employment_type");
        this.location = (String) jobMap.get("location");
        this.skill = (String) jobMap.get("skill");
        this.qualifications = (String) jobMap.get("qualifications");
        this.treat = (String) jobMap.get("treat");
    }

    // equals 메서드 재정의
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        JobData jobData = (JobData) obj;
        return Objects.equals(company, jobData.company)
                && Objects.equals(technique, jobData.technique)
                && Objects.equals(employment_type, jobData.employment_type);
    }

    // hashCode 메서드 재정의
    @Override
    public int hashCode() {
        return Objects.hash(company, technique, employment_type);
    }

    // toString 메서드 재정의
    @Override
    public String toString() {
        return "JobData{" +
                "company='" + company + '\'' +
                ", selection1='" + selection1 + '\'' +
                ", selection1_url='" + selection1_url + '\'' +
                ", due='" + due + '\'' +
                ", technique='" + technique + '\'' +
                ", career_min='" + career_min + '\'' +
                ", career_max='" + career_max + '\'' +
                ", education_level='" + education_level + '\'' +
                ", employment_type='" + employment_type + '\'' +
                ", location='" + location + '\'' +
                ", skill='" + skill + '\'' +
                ", qualifications='" + location + '\'' +
                ", treat='" + location + '\'' +
                '}';
    }
}
