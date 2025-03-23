package com.gcu.jobshorts.data.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Preference {
    private String jobType;
    private String field;
    private String region;
}