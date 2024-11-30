package com.gcu.jobshorts;

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

public class SharedViewModel extends ViewModel {
    // MutableLiveData -> 외부에서 읽고 쓰기가 가능함
    // LiveData -> 외부에서 읽기만 가능함
    private final MutableLiveData<UserData> userData = new MutableLiveData<UserData>();
    private final DatabaseReference userRef;

    // 생성자
    public SharedViewModel() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
            fetchUserData();
        } else {
            userRef = null;
        }
    }

    public void updateUserData(UserData updatedUserData) {
        if (userRef == null) return;

        userRef.setValue(updatedUserData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userData.setValue(updatedUserData);
            } else {
                // Log error
            }
        });
    }

    public LiveData<UserData> getUserData() {
        return userData;
    }

    private void fetchUserData() {
        if (userRef == null) return;

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserData data = dataSnapshot.getValue(UserData.class);
                userData.setValue(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log error
            }
        });
    }
}

