package com.gcu.jobshorts.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gcu.jobshorts.activity.LoginActivity;
import com.gcu.jobshorts.R;
import com.gcu.jobshorts.adapter.CareerAdapter;
import com.gcu.jobshorts.adapter.SpecificationAdapter;
import com.gcu.jobshorts.data.JobData;
import com.gcu.jobshorts.firebase.FirestoreHelper;
import com.gcu.jobshorts.firebase.SharedViewModel;
import com.gcu.jobshorts.data.user.Career;
import com.gcu.jobshorts.data.user.Preference;
import com.gcu.jobshorts.data.user.Specification;
import com.gcu.jobshorts.data.user.UserData;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private FirebaseAuth mAuth;
    private CardView cardEducation, cardCareer, cardSpecification, cardJobRequirement;
    private Button logoutBtn, decideBtn;
    private ImageButton careerBtn, specBtn;
    private EditText etextRegion;
    private Spinner spinnerEducation, spinnerJobType, spinnerField;
    private List<Career> careerList;
    private List<Specification> specList;
    private CareerAdapter careerAdapter;
    private SpecificationAdapter specificationAdapter;
    private RecyclerView careerRecyclerView, specRecyclerView;
    private LinearLayoutManager careerLayoutManager, specLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //  초기화 //  //  //  //  //  //  //  //
        careerLayoutManager = new LinearLayoutManager(requireContext());
        specLayoutManager = new LinearLayoutManager(requireContext());

        careerList = new ArrayList<>();
        specList = new ArrayList<>();
        careerAdapter = new CareerAdapter(careerList, requireContext());
        specificationAdapter = new SpecificationAdapter(specList, requireContext());

        careerRecyclerView = rootView.findViewById(R.id.recyclerview_career);
        specRecyclerView = rootView.findViewById(R.id.recyclerview_specification);

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

        // //   //  //  //  //  //  //
        careerRecyclerView.setAdapter(careerAdapter);
        specRecyclerView.setAdapter(specificationAdapter);

        careerRecyclerView.setLayoutManager(careerLayoutManager);
        specRecyclerView.setLayoutManager(specLayoutManager);
        //  //  //  //  //  //  //  //

        // ViewModel 연결
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mAuth = FirebaseAuth.getInstance();

        // 데이터 관찰
        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), userData -> {
            if (userData != null) {
                updateUserProfile(userData);
            }
        });

        // 리스너  //  //  //  //  //  //  //  //  //  //  //
        // 경력 추가 버튼 클릭 리스너
        careerBtn.setOnClickListener(v -> {
            LinearLayout dialogLayout = new LinearLayout(requireContext());
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(16, 16, 16, 16);

            final EditText inputCompany = new EditText(requireContext());
            inputCompany.setHint("회사명");
            inputCompany.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            inputCompany.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            dialogLayout.addView(inputCompany);

            final EditText inputContent = new EditText(requireContext());
            inputContent.setHint("담당 업무 내용");
            inputContent.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            inputContent.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            dialogLayout.addView(inputContent);

            final EditText inputPeriod = new EditText(requireContext());
            inputPeriod.setHint("근무 기간 (개월)");
            inputPeriod.setInputType(InputType.TYPE_CLASS_NUMBER);
            inputPeriod.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            inputPeriod.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            dialogLayout.addView(inputPeriod);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("경력 추가");
            builder.setView(dialogLayout);

            builder.setPositiveButton("추가", (dialog, which) -> {
                String company = inputCompany.getText().toString();
                String content = inputContent.getText().toString();
                String periodStr = inputPeriod.getText().toString();

                if (!company.isEmpty() && !content.isEmpty() && !periodStr.isEmpty()) {
                    int period = Integer.parseInt(periodStr);
                    Career newCareer = new Career(company, content, period);
                    careerList.add(newCareer);
                    careerAdapter.notifyDataSetChanged();
                }
            });

            builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        specBtn.setOnClickListener(v -> {
            LinearLayout dialogLayout = new LinearLayout(requireContext());
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(16, 16, 16, 16);

            final EditText inputTitle = new EditText(requireContext());
            inputTitle.setHint("스펙 제목 (ex. 자격증명)");
            inputTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            inputTitle.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            dialogLayout.addView(inputTitle);

            final EditText inputContent = new EditText(requireContext());
            inputContent.setHint("상세 설명");
            inputContent.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            inputContent.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.primary));
            dialogLayout.addView(inputContent);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("스펙 추가");
            builder.setView(dialogLayout);

            builder.setPositiveButton("추가", (dialog, which) -> {
                String title = inputTitle.getText().toString();
                String content = inputContent.getText().toString();

                if (!title.isEmpty() && !content.isEmpty()) {
                    Specification newSpecification = new Specification(title, content);
                    specList.add(newSpecification);
                    specificationAdapter.notifyDataSetChanged();
                }
            });

            builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        decideBtn.setOnClickListener(v -> {
            String selectedRegion = etextRegion.getText().toString();
            String selectedField = spinnerField.getSelectedItem().toString();

            if (spinnerEducation.getSelectedItemPosition() == 0 || selectedRegion.isEmpty() || selectedField.isEmpty()) {
                Snackbar.make(rootView, "모든 항목을 선택 및 입력하세요.", Snackbar.LENGTH_SHORT).show();
            }
            else {
                sendUserDataToModel();

                // 1. educationLevel = spinner 인덱스 + 1
                int educationLevel = spinnerEducation.getSelectedItemPosition() + 1;

                // 2. careerList의 period 값 합산
                int minExperience = 0;
                for (Career career : careerList) {
                    minExperience += career.getPeriod(); // period = 개월 수
                }
                minExperience /= 12;

                // FirestoreHelper 호출
                FirestoreHelper firestoreHelper = new FirestoreHelper();
                firestoreHelper.fetchFilteredData(
                        educationLevel,
                        minExperience,
                        selectedRegion,
                        selectedField,
                        new FirestoreHelper.FirestoreCallback() {
                            @Override
                            public void onSuccess(List<JobData> jobList) {
                                if (!jobList.isEmpty()) {
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
        });


        logoutBtn.setOnClickListener(v -> logout());
        //  //  //  //  //  //  //  //  //  //  //  //  //  //  //

        return rootView;
    }

    private void sendUserDataToModel() {
        UserData myData = sharedViewModel.getUserData().getValue();

        if (myData != null) {
            // Preference 설정
            String selectedJob = spinnerJobType.getSelectedItem().toString();
            String selectedRegion = etextRegion.getText().toString();
            String selectedField = spinnerField.getSelectedItem().toString();
            Preference tmpPref = new Preference(selectedJob, selectedField, selectedRegion);

            // RecyclerView에서 현재 리스트 사용
            List<Specification> tmpSpecs = specList;
            List<Career> tmpCareers = careerList;

            // 교육 정보
            String tmpEdu = spinnerEducation.getSelectedItem().toString();

            // UserData 객체 구성
            myData.setUID(Objects.requireNonNull(mAuth.getUid()));
            myData.setUserName(Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName()));
            myData.setUserInfo(new UserData.UserInfo(tmpPref, tmpSpecs, tmpCareers, tmpEdu));

            sharedViewModel.updateUserData(myData);
        } else {
            Snackbar.make(requireView(), "사용자 데이터를 불러오지 못했습니다.", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void updateUserProfile(UserData userData) {
        if (userData != null && userData.getUserInfo() != null) {
            // Preference
            Preference pref = userData.getUserInfo().getPreference();
            if (pref != null) {
                // 스피너 및 지역 입력값 설정
                setSpinnerSelection(spinnerJobType, pref.getJobType());
                setSpinnerSelection(spinnerField, pref.getField());
                etextRegion.setText(pref.getRegion());
            }

            // Education
            String education = userData.getUserInfo().getEducation();
            if (education != null) {
                setSpinnerSelection(spinnerEducation, education);
            }

            // Career
            List<Career> careers = userData.getUserInfo().getCareers();
            if (careers != null) {
                for (Career newCareer : careers) {
                    if (!careerList.contains(newCareer)) {
                        careerList.add(newCareer);
                    }
                }
            } else {
                careerList.clear();
            }

            careerAdapter.notifyDataSetChanged();

            // Specification
            List<Specification> specs = userData.getUserInfo().getSpecifications();
            if (specs != null) {
                for (Specification newSpec : specs) {
                    if (!specList.contains(newSpec)) {
                        specList.add(newSpec);
                    }
                }
            } else {
                specList.clear();
            }

            specificationAdapter.notifyDataSetChanged();
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                if (adapter.getItem(i).toString().equals(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
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
