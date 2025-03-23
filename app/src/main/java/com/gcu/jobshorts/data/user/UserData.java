package com.gcu.jobshorts.data.user;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserData {

    @NonNull
    @EqualsAndHashCode.Include
    private String UID;

    @NonNull
    @EqualsAndHashCode.Include
    private String userName;

    private UserInfo userInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Preference preference;
        private List<Specification> specifications;
        private List<Career> careers;
        private String education;
    }
}
