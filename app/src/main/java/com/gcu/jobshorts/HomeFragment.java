package com.gcu.jobshorts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private TextView profileNameTextView;
    private Button logoutBtn;
    private Button decideBtn;
    private Spinner spinnerEducation;
    private Spinner spinnerRegion;
    private Spinner spinnerExperience;
    private Spinner spinnerJob;
    private List<String> educationOptions = Arrays.asList("최종 학력을 선택하세요", "고졸", "초대졸", "대졸", "석사", "박사");
    private List<String> regionOptions = Arrays.asList("희망 근무 지역을 선택하세요", "서울", "부산", "대구", "인천", "광주");
    private List<String> experienceOptions = Arrays.asList("경력을 선택하세요", "없음", "1년 미만", "1~3년", "3~5년", "5년 이상");
    private List<String> jobOptions = Arrays.asList("직종을 선택하세요", "프론트엔드", "백엔드", "게임개발", "임베디드", "클라우드", "인공지능", "모바일", "기타");

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // 스피너 초기화
        initializeSpinners(rootView);

        // UI 요소 초기화
        profileNameTextView = rootView.findViewById(R.id.textview_username);
        logoutBtn = rootView.findViewById(R.id.logoutButton);
        decideBtn = rootView.findViewById(R.id.button_saveNsearch);

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

        // 로그아웃 버튼 리스너
        logoutBtn.setOnClickListener(v -> {
            logout();
        });

        // 결정 및 검색 버튼 리스너
        decideBtn.setOnClickListener(v -> {
            String selectedEducation = spinnerEducation.getSelectedItem().toString();
            String selectedRegion = spinnerRegion.getSelectedItem().toString();
            String selectedExperience = spinnerExperience.getSelectedItem().toString();
            String selectedJob = spinnerJob.getSelectedItem().toString();

            if (selectedEducation.equals(educationOptions.get(0)) ||
                    selectedRegion.equals(regionOptions.get(0)) ||
                    selectedExperience.equals(experienceOptions.get(0)) ||
                    selectedJob.equals(jobOptions.get(0))) {
                Snackbar.make(rootView, "모든 항목을 선택하세요. ", Snackbar.LENGTH_SHORT).show();
            } else {
                String result = String.format("선택: %s, %s, %s, %s", selectedEducation, selectedRegion, selectedExperience, selectedJob);
                Snackbar.make(rootView, "검색을 시작합니다. ", Snackbar.LENGTH_SHORT).show();
                sendUserDataToModel();
                // 서치프래그먼트 전환
            }
        });

        return rootView;
    }

    private void initializeSpinners(View rootView) {
        // 스피너 변수 초기화
        spinnerEducation = rootView.findViewById(R.id.spinner_education);
        spinnerRegion = rootView.findViewById(R.id.spinner_region);
        spinnerExperience = rootView.findViewById(R.id.spinner_experience);
        spinnerJob = rootView.findViewById(R.id.spinner_job);

        // Adapter 설정
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, educationOptions);
        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, regionOptions);
        ArrayAdapter<String> experienceAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, experienceOptions);
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, jobOptions);

        spinnerEducation.setAdapter(educationAdapter);
        spinnerRegion.setAdapter(regionAdapter);
        spinnerExperience.setAdapter(experienceAdapter);
        spinnerJob.setAdapter(jobAdapter);
    }

    // 사용자 프로필 업데이트
    private void updateUserProfile(UserData userData) {
        profileNameTextView.setText(userData.getUserName());
    }

    private void sendUserDataToModel() {
        String selectedEducation = spinnerEducation.getSelectedItem().toString();
        String selectedRegion = spinnerRegion.getSelectedItem().toString();
        String selectedExperience = spinnerExperience.getSelectedItem().toString();
        String selectedJob = spinnerJob.getSelectedItem().toString();

        UserData tmpUserData = sharedViewModel.getUserData().getValue();

        if (tmpUserData != null) {
            UserData.Detail tmpDetail = new UserData.Detail(
                    selectedJob,
                    selectedExperience,
                    selectedEducation,
                    selectedRegion
            );

            tmpUserData.setUserDetail(tmpDetail);

            sharedViewModel.updateUserData(tmpUserData);
        } else {
            // 예외 처리
            Snackbar.make(requireView(), "사용자 데이터를 불러오지 못했습니다.", Snackbar.LENGTH_SHORT).show();
        }
    }



    // 로그아웃
    private void logout() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();  // Firebase 로그아웃

        // 로그인 화면으로 돌아가기
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
