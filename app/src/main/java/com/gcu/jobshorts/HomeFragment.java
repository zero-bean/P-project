package com.gcu.jobshorts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.gcu.jobshorts.resume.CareerFragment;
import com.gcu.jobshorts.resume.SpecificationFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private FirebaseAuth mAuth;
    private CardView cardEducation, cardCareer, cardSpecification, cardJobRequirement;
    private Button logoutBtn, decideBtn;
    private ImageButton careerBtn, specBtn;
    private EditText etextRegion;
    private Spinner spinnerEducation, spinnerJobType, spinnerField;
    private List<String> educationOptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //  초기화 //  //  //  //  //  //  //  //
        cardEducation = rootView.findViewById(R.id.card_education);
        cardCareer = rootView.findViewById(R.id.card_career);
        cardSpecification = rootView.findViewById(R.id.card_specification);
        cardJobRequirement = rootView.findViewById(R.id.card_requirement);

        logoutBtn = rootView.findViewById(R.id.button_logout);
        careerBtn = rootView.findViewById(R.id.button_add_career);
        decideBtn = rootView.findViewById(R.id.button_getsolution);
        specBtn = rootView.findViewById(R.id.button_add_specification);

        etextRegion = rootView.findViewById(R.id.edittext_location);

        spinnerEducation = rootView.findViewById(R.id.spinner_education);
        spinnerJobType = rootView.findViewById(R.id.spinner_employment_type);
        spinnerField = rootView.findViewById(R.id.spinner_field);
        //  //  //  //  //  //  //  //  //  //

        educationOptions = Arrays.asList(
                "선택하기",
                "고등학교",
                "대학교(2,3년)",
                "대학교(4년)",
                "대학원"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                educationOptions
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEducation.setAdapter(adapter);

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

        // 리스너
        careerBtn.setOnClickListener(v -> navigateToFragment(new CareerFragment()));
        specBtn.setOnClickListener(v -> navigateToFragment(new SpecificationFragment()));
        logoutBtn.setOnClickListener(v -> logout());
        // 결정 및 검색 버튼 리스너
        /* decideBtn.setOnClickListener(v -> {
            String selectedEducation = spinnerEducation.getSelectedItem().toString();
            String selectedRegion = spinnerRegion.getSelectedItem().toString();
            String experienceInput = editTextExperience.getText().toString();
            String selectedJob = spinnerJob.getSelectedItem().toString();

            if (selectedEducation.equals(educationOptions.get(0)) ||
                    selectedRegion.equals(regionOptions.get(0)) ||
                    experienceInput.isEmpty() ||
                    selectedJob.equals(jobOptions.get(0))) {
                Snackbar.make(rootView, "모든 항목을 선택하세요. ", Snackbar.LENGTH_SHORT).show();
            } else {
                int minExperience = Integer.parseInt(experienceInput);
                FirestoreHelper firestoreHelper = new FirestoreHelper();

                firestoreHelper.fetchFilteredData(
                        spinnerEducation.getSelectedItemPosition(),
                        minExperience,
                        selectedRegion,
                        selectedJob,
                        new FirestoreHelper.FirestoreCallback() {
                            @Override
                            public void onSuccess(List<JobData> jobList) {
                                if (!jobList.isEmpty()) {
                                    // 예: RecyclerView에 데이터 표시
                                    sharedViewModel.setJobDataList(jobList);
                                    Snackbar.make(rootView, "검색 성공! 총 " + jobList.size() + "건", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Snackbar.make(rootView, "조건에 맞는 결과가 없습니다.", Snackbar.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Snackbar.make(rootView, "데이터 검색 중 오류 발생: " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        }); */

        return rootView;
    }

    // 프래그먼트로 이동하는 함수
    private void navigateToFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // 기존 프래그먼트를 새로운 프래그먼트로 교체
        transaction.addToBackStack(null);  // 뒤로가기 가능하게 설정
        transaction.commit();
    }

    // 레거시 코드
    /* private void sendUserDataToModel(String experienceInput) {
        int selectedEducationIndex = spinnerEducation.getSelectedItemPosition();
        String selectedRegion = spinnerRegion.getSelectedItem().toString();
        String selectedJob = spinnerJob.getSelectedItem().toString();

        UserData tmpUserData = sharedViewModel.getUserData().getValue();

        if (tmpUserData != null) {
            UserData.Detail tmpDetail = new UserData.Detail(
                    selectedJob,
                    experienceInput,
                    String.valueOf(selectedEducationIndex),
                    selectedRegion
            );

            tmpUserData.setUserDetail(tmpDetail);

            sharedViewModel.updateUserData(tmpUserData);
        } else {
            // 예외 처리
            Snackbar.make(requireView(), "사용자 데이터를 불러오지 못했습니다.", Snackbar.LENGTH_SHORT).show();
        }
    } */

    private void updateUserProfile(UserData userData) {
        // 여기에 모든 항목 불러오기 추가!
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
