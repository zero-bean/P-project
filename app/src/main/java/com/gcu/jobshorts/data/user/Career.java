package com.gcu.jobshorts.data.user;

import com.google.firebase.database.PropertyName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Career {
    private String company;
    private String content;
    private int period;
}