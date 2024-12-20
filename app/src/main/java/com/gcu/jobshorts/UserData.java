package com.gcu.jobshorts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserData {
    @NonNull
    private String UID;
    @NonNull
    private String userName;
    private Detail userDetail;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserData userData = (UserData) obj;
        return UID.equals(userData.UID) &&
                userName.equals(userData.userName);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {
        private String occupation;
        private String career;
        private String education;
        private String region;
    }
}