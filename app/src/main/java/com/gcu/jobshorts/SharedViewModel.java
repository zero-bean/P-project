package com.gcu.jobshorts;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SharedViewModel extends ViewModel {
    private static final String TAG = "SharedViewModel";

    private final MutableLiveData<UserData> userData = new MutableLiveData<>();
    private final MutableLiveData<List<JobData>> jobDataList = new MutableLiveData<>();

    private final DatabaseReference userRef;

    public SharedViewModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

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
                    Log.e(TAG, "User data is null.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error fetching user data: " + databaseError.getMessage());
            }
        });
    }
}
