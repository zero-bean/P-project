package com.gcu.jobshorts.firebase;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.gcu.jobshorts.data.JobData;
import com.gcu.jobshorts.data.company.CompanyData;
import com.gcu.jobshorts.data.user.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private static final String TAG = "SharedViewModel";

    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<List<JobData>> jobDataList = new MutableLiveData<>();
    private final MutableLiveData<List<CompanyData>> companyDataList = new MutableLiveData<>();
    private final MutableLiveData<CompanyData> selectedCompany = new MutableLiveData<>();
    private final DatabaseReference userRef;
    private FirebaseUser currentUser;

    public SharedViewModel() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.e(TAG, currentUser.getUid() + " / " + currentUser.getDisplayName());

        if (currentUser != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            userRef = database.getReference("users").child(currentUser.getUid());
            fetchUserData();
        } else {
            userRef = null;
            Log.e(TAG, "No authenticated user found.");
        }
    }

    public void updateUserData(UserData updatedUserData) {
        if (userRef == null) {
            Log.e(TAG, "Database reference is null. Cannot update user data.");
            return;
        }

        userRef.setValue(updatedUserData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userData.setValue(updatedUserData);
                Log.d(TAG, "User data updated successfully.");
            } else {
                Log.e(TAG, "Failed to update user data.", task.getException());
            }
        });
    }

    public LiveData<UserData> getUserData() {
        return userData;
    }

    public void setJobDataList(List<JobData> jobList) {
        jobDataList.setValue(jobList);
    }

    public LiveData<List<JobData>> getJobDataList() {
        return jobDataList;
    }

    public void setCompanyDataList(List<CompanyData> companyList) { companyDataList.setValue(companyList); }
    public LiveData<List<CompanyData>> getCompanyDataList() {
        return companyDataList;
    }

    public void setSelectedCompany(CompanyData company) {
        selectedCompany.setValue(company);
    }
    public LiveData<CompanyData> getSelectedCompany() {
        return selectedCompany;
    }

    private void fetchUserData() {
        if (userRef == null) {
            Log.e(TAG, "Database reference is null. Cannot fetch user data.");
            return;
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                if (data != null) {
                    userData.setValue(data);
                    Log.d(TAG, "User data fetched successfully: " + data.getUserName());
                } else {
                    // 새 사용자 데이터 생성
                    UserData newUserData = new UserData(currentUser.getUid(), currentUser.getDisplayName(), null);
                    userRef.setValue(newUserData).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "New user data created and added to Firebase.");
                            userData.setValue(newUserData);
                        } else {
                            Log.e(TAG, "Failed to add new user data to Firebase.");
                        }
                    });
                    Log.e(TAG, "User data is null, creating new user.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user data: " + databaseError.getMessage());
            }
        });
    }
}
