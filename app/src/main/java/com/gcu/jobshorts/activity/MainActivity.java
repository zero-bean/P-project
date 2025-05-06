package com.gcu.jobshorts.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.data.JobData;
import com.gcu.jobshorts.data.company.CompanyData;
import com.gcu.jobshorts.data.company.Financial;
import com.gcu.jobshorts.data.company.Tech;
import com.gcu.jobshorts.firebase.FirestoreHelper;
import com.gcu.jobshorts.firebase.SharedViewModel;
import com.gcu.jobshorts.fragment.ChatFragment;
import com.gcu.jobshorts.fragment.HomeFragment;
import com.gcu.jobshorts.fragment.ResultFragment;
import com.gcu.jobshorts.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private SharedViewModel sharedViewModel;
    FragmentManager fragmentManager;
    Fragment fragment[];
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SharedViewModel 초기화
        sharedViewModel = new SharedViewModel();

        // 프래그먼트 초기화
        initializeFragments();

        // BottomNavigation 설정
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int index = -1;
                int id = menuItem.getItemId();

                if (id == R.id.navigation_bar_item_1) index = 0;
                else if (id == R.id.navigation_bar_item_2) index = 1;
                else if (id == R.id.navigation_bar_item_3) index = 2;
                else if (id == R.id.navigation_bar_item_4) index = 3;

                if (index != -1) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    for (int i = 0; i < fragment.length; i++) {
                        if (i == index) {
                            transaction.show(fragment[i]);
                            if (i == 2)
                                showCompanySelectionDialog(Objects.requireNonNull(sharedViewModel.getCompanyDataList().getValue()));
                        } else {
                            transaction.hide(fragment[i]);
                        }
                    }
                    transaction.commit();
                }

                return true;
            }
        });

        // sharedModel에게 resultfragment에서 검색할 데이터 호출
        FirestoreHelper firestoreHelper = new FirestoreHelper();
        firestoreHelper.fetchCompanyData(
                new FirestoreHelper.FetchCompanyCallback() {
                    @Override
                    public void onSuccess(List<CompanyData> companyList) {
                        if (!companyList.isEmpty()) {
                            sharedViewModel.setCompanyDataList(companyList);
                            Toast.makeText(MainActivity.this, "회사 데이터 불러오기 성공 (" + companyList.size() + "개)", Toast.LENGTH_SHORT).show();
                            Log.d("CompanyCheck", "불러온 회사 수: " + companyList.size());
                            for (CompanyData company : companyList) {
                                // 회사 이름 확인
                                if (company.getName() != null) {
                                    Log.d("CompanyCheck", "회사명: " + company.getName());
                                }
                                // 복지 지표 확인
                                if (company.getWelfare() != null) {
                                    Log.d("CompanyCheck", "1인평균급여액: " + company.getWelfare().getAverageSalaryPerPerson());
                                }
                                // 연구 현황 확인
                                if (company.getTechs() != null) {
                                    for (Tech tech : company.getTechs()) {
                                        Log.d("CompanyCheck", "연구: " + tech.getTitle() + " - " + tech.getContent());
                                    }
                                }
                                // 재무 정보 확인
                                Financial f = company.getFinancial();
                                if (f != null) {
                                    Log.d("CompanyCheck", "재무 정보 단위: " + f.getUnit());

                                    // 요약별도재무정보
                                    Log.d("CompanyCheck", "별도 - 자산총계(2024): " + f.getSeparateTotalAssets2024());
                                    Log.d("CompanyCheck", "별도 - 영업수익(2024): " + f.getSeparateRevenue2024());
                                    Log.d("CompanyCheck", "별도 - 영업이익(2024): " + f.getSeparateOperatingProfit2024());
                                    Log.d("CompanyCheck", "별도 - 당기순이익(2024): " + f.getSeparateNetIncome2024());
                                    Log.d("CompanyCheck", "별도 - 기본주당이익(2024): " + f.getSeparateBasicEPS2024());

                                    // 요약연결재무정보
                                    Log.d("CompanyCheck", "연결 - 자산총계(2024): " + f.getConsolidatedTotalAssets2024());
                                    Log.d("CompanyCheck", "연결 - 영업수익(2024): " + f.getConsolidatedRevenue2024());
                                    Log.d("CompanyCheck", "연결 - 영업이익(2024): " + f.getConsolidatedOperatingProfit2024());
                                    Log.d("CompanyCheck", "연결 - 당기순이익(2024): " + f.getConsolidatedNetIncome2024());
                                    Log.d("CompanyCheck", "연결 - 기본주당이익(2024): " + f.getConsolidatedBasicEPS2024());
                                }
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "회사 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(MainActivity.this, "회사 데이터 불러오기 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

    }

    // 프래그먼트 초기화 함수
    private void initializeFragments() {
        fragmentManager = getSupportFragmentManager();
        fragment = new Fragment[4];
        fragment[0] = new HomeFragment();
        fragment[1] = new SearchFragment();
        fragment[2] = new ResultFragment();  // 요번에 작업할 것
        fragment[3] = new ChatFragment();

        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment[0])
                .add(R.id.fragment_container, fragment[1]).hide(fragment[1])
                .add(R.id.fragment_container, fragment[2]).hide(fragment[2])
                .add(R.id.fragment_container, fragment[3]).hide(fragment[3])
                .commit();
    }

    private void showCompanySelectionDialog(List<CompanyData> companyList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("회사 선택");

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        // ViewModel은 반드시 ViewModelProvider를 통해 가져와야 함
        Map<Integer, CompanyData> radioButtonMap = new HashMap<>(); // 라디오버튼 id -> 회사 매핑

        for (CompanyData company : companyList) {
            if (company != null) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(company.getName());
                int id = View.generateViewId();
                radioButton.setId(id);
                radioButtonMap.put(id, company); // ID로 회사 연결
                radioGroup.addView(radioButton);
            }
        }

        builder.setView(radioGroup);

        builder.setPositiveButton("확인", (dialog, which) -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                CompanyData selectedCompanyData = radioButtonMap.get(selectedId);
                if (selectedCompanyData != null) {
                    new ViewModelProvider(this).get(SharedViewModel.class)
                            .setSelectedCompany(selectedCompanyData);
                }
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}