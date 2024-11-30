package com.gcu.jobshorts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private TextView profileNameTextView;
    private Button logoutBtn;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // UI 요소 초기화
        profileNameTextView = rootView.findViewById(R.id.profileNameTextView);
        logoutBtn = rootView.findViewById(R.id.logoutButton);

        // ViewModel 연결
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // 데이터 관찰
        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<UserData>() {
            @Override
            public void onChanged(UserData userData) {
                if (userData != null) {
                    updateUserProfile(userData);
                }
            }
        });

        // 로그아웃 버튼 클릭 리스너 설정
        logoutBtn.setOnClickListener(v -> {
            logout();
        });

        return rootView;
    }

    // 사용자 프로필 업데이트
    private void updateUserProfile(UserData userData) {
        profileNameTextView.setText(userData.getUserName());
    }

    // 로그아웃
    private void logout() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();  // Firebase 로그아웃

        // 로그인 화면으로 돌아가기
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();  // 현재 액티비티 종료
    }
}
