package com.gcu.jobshorts.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class HomeFragment extends Fragment {
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SharedViewModel sharedViewModel;
    private FirebaseAuth mAuth;
    private Button logoutBtn, decideBtn, pdfSelectBtn;
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
        PDFBoxResourceLoader.init(requireContext());

        careerLayoutManager = new LinearLayoutManager(requireContext());
        specLayoutManager = new LinearLayoutManager(requireContext());

        careerList = new ArrayList<>();
        specList = new ArrayList<>();
        careerAdapter = new CareerAdapter(careerList, requireContext());
        specificationAdapter = new SpecificationAdapter(specList, requireContext());

        careerRecyclerView = rootView.findViewById(R.id.recyclerview_career);
        specRecyclerView = rootView.findViewById(R.id.recyclerview_specification);

        logoutBtn = rootView.findViewById(R.id.button_logout);
        careerBtn = rootView.findViewById(R.id.button_add_career);
        decideBtn = rootView.findViewById(R.id.button_getsolution);
        specBtn = rootView.findViewById(R.id.button_add_specification);
        pdfSelectBtn = rootView.findViewById(R.id.button_upload_pdf);

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

        Button selectPdfButton = rootView.findViewById(R.id.button_upload_pdf);
        selectPdfButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            pdfPickerLauncher.launch(intent);
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

        // PDF 피커 런처 초기화
        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri pdfUri = result.getData().getData();
                        if (pdfUri != null) {
                            processPdfResume(pdfUri);
                        }
                    } else {
                        Toast.makeText(requireContext(), "PDF 파일 선택이 취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // PDF 선택 버튼 클릭 리스너 설정
        pdfSelectBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            pdfPickerLauncher.launch(intent);
        });

        return rootView;
    }

    private void processPdfResume(Uri pdfUri) {
        Toast.makeText(requireContext(), "PDF 파일을 분석 중입니다...", Toast.LENGTH_LONG).show();
        executorService.execute(() -> {
            try (ParcelFileDescriptor pfd = requireContext().getContentResolver().openFileDescriptor(pdfUri, "r")) {
                assert pfd != null;
                try (InputStream inputStream = new FileInputStream(pfd.getFileDescriptor());
                     PDDocument document = PDDocument.load(inputStream)) {

                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    String pdfText = pdfStripper.getText(document);
                    Log.d("PDF_CONTENT", "추출된 PDF 텍스트:\n" + pdfText);

                    // 파싱된 데이터를 위한 임시 변수
                    String parsedEducation = "";
                    List<Career> parsedCareers = new ArrayList<>();
                    List<Specification> parsedSpecifications = new ArrayList<>();

                    // --- 1. 학력 추출 (최종 학력) ---
                    parsedEducation = extractEducation(pdfText);

                    // --- 2. 경력 추출 ---
                    parsedCareers = extractCareers(pdfText);

                    // --- 3. 스펙 추출 (대외활동, 봉사활동, 자격증, 외국어, 자기소개서) ---
                    parsedSpecifications = extractSpecifications(pdfText);

                    // UserData 객체 및 UI를 메인 스레드에서 업데이트
                    String finalParsedEducation = parsedEducation;
                    List<Career> finalParsedCareers = parsedCareers;
                    List<Specification> finalParsedSpecifications = parsedSpecifications;
                    requireActivity().runOnUiThread(() -> {
                        UserData currentUserData = sharedViewModel.getUserData().getValue();
                        if (currentUserData == null) {
                            // UserData가 아직 로드되지 않은 경우, 새로 생성 (최소한의 정보로)
                            String uid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                            String userName = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getDisplayName() : "새 사용자";
                            currentUserData = new UserData(uid, userName, new UserData.UserInfo(null, new ArrayList<>(), new ArrayList<>(), null));
                        }

                        UserData.UserInfo userInfo = currentUserData.getUserInfo();
                        if (userInfo == null) {
                            userInfo = new UserData.UserInfo();
                            currentUserData.setUserInfo(userInfo);
                        }

                        // 추출된 데이터로 UserInfo 업데이트
                        userInfo.setEducation(finalParsedEducation);
                        userInfo.setCareers(finalParsedCareers);
                        userInfo.setSpecifications(finalParsedSpecifications);

                        // ViewModel을 통해 데이터 업데이트 및 UI 반영
                        sharedViewModel.updateUserData(currentUserData);
                        Toast.makeText(requireContext(), "이력서 내용 불러오기 완료!", Toast.LENGTH_SHORT).show();
                    });

                }
            } catch (IOException e) {
                Log.e("PDF_ERROR", "PDF 처리 중 오류: " + e.getMessage());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "PDF 파일 처리 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    private String extractEducation(String pdfText) {
        String education = "";
        // "학력사항" 섹션을 찾고 그 이후의 텍스트에서 최종 학력을 추출
        // PDF 텍스트 추출 시 표 헤더가 반복될 수 있어 이를 제거
        String cleanedPdfText = pdfText.replaceAll("재학기간\\s*,?학교\\s*,?학과/전공\\s*,?학점\\s*,?졸업구분", "").trim();

        Pattern educationSectionPattern = Pattern.compile("학력사항\\s*\\n([\\s\\S]*?)(?:\\n\\s*\\n|경력사항|대외활동|자격증|외국어|자기소개서)", Pattern.DOTALL);
        Matcher sectionMatcher = educationSectionPattern.matcher(cleanedPdfText);

        if (sectionMatcher.find()) {
            String eduBlock = sectionMatcher.group(1).trim();
            String[] lines = eduBlock.split("\n");
            // 마지막 유효한 학력 관련 줄을 최종 학력으로 간주
            for (int i = lines.length - 1; i >= 0; i--) {
                String line = lines[i].trim();
                // "대학교", "졸업", "재학" 등의 키워드를 포함하는 줄을 찾음
                if (!line.isEmpty() && (line.contains("대학교") || line.contains("전문대학") || line.contains("고등학교") || line.contains("졸업") || line.contains("재학") || line.contains("수료"))) {
                    education = line;
                    break;
                }
            }
            // 만약 위의 조건으로 찾지 못했다면, 가장 마지막 줄을 최종 학력으로 설정
            if (education.isEmpty() && lines.length > 0) {
                education = lines[lines.length - 1].trim();
            }
        }
        Log.d("PDF_PARSE", "추출된 학력: " + education);
        return education;
    }

    private List<Career> extractCareers(String pdfText) {
        List<Career> careers = new ArrayList<>();
        // "경력사항" 섹션을 찾고 그 이후의 텍스트에서 경력 항목을 추출
        Pattern careerSectionPattern = Pattern.compile("경력사항\\(인턴, 주요 아르바이트 등\\)\\s*\\n([\\s\\S]*?)(?:\\n\\s*\\n|대외활동 및 봉사활동|자격증/면허증|외국어|자기소개서)", Pattern.DOTALL);
        Matcher sectionMatcher = careerSectionPattern.matcher(pdfText);

        if (sectionMatcher.find()) {
            String careerBlock = sectionMatcher.group(1);
            // 헤더 제거: "직장명", "근무기간", "고용형태", "담당 업무"
            careerBlock = careerBlock.replaceAll("직장명\\s*,?근무기간\\s*,?고용형태\\s*,?담당 업무", "").trim();

            // 각 경력 항목을 파싱하는 정규식
            // PDF의 텍스트 추출 방식에 따라 패턴 조정이 필요할 수 있음
            // 예시: 회사명 \n 근무기간 \n 고용형태 \n 담당업무
            Pattern careerEntryPattern = Pattern.compile(
                    "([^\\n]+?)\\n" + // 회사명 (첫 번째 캡처 그룹)
                            "([^\\n]+?)\\n" + // 근무기간 (두 번째 캡처 그룹)
                            "(?:[^\\n]+?)\\n" + // 고용형태 (캡처하지 않음)
                            "([^\\n]+?)(?=\\n\\n|\\n직장명|\\Z)", // 담당 업무 (세 번째 캡처 그룹), 다음 경력 시작 또는 끝까지
                    Pattern.DOTALL
            );
            Matcher entryMatcher = careerEntryPattern.matcher(careerBlock);

            while (entryMatcher.find()) {
                String company = entryMatcher.group(1).trim();
                String periodStr = entryMatcher.group(2).trim();
                String content = entryMatcher.group(3).trim();

                int periodInMonths = 0; // 근무 기간을 월 단위로 저장
                // "근무기간"에서 년/월 정보를 추출하여 월 단위로 변환
                // "YYYY.MM ~ YYYY.MM" 형식 또는 "X년 Y개월" 형식 파싱
                Pattern dateRangePattern = Pattern.compile("(\\d{4})\\.(\\d{2})\\s*~\\s*(\\d{4})\\.(\\d{2})");
                Matcher dateRangeMatcher = dateRangePattern.matcher(periodStr);
                if (dateRangeMatcher.find()) {
                    try {
                        int startYear = Integer.parseInt(dateRangeMatcher.group(1));
                        int startMonth = Integer.parseInt(dateRangeMatcher.group(2));
                        int endYear = Integer.parseInt(dateRangeMatcher.group(3));
                        int endMonth = Integer.parseInt(dateRangeMatcher.group(4));

                        periodInMonths = (endYear - startYear) * 12 + (endMonth - startMonth);
                    } catch (NumberFormatException e) {
                        Log.e("PDF_PARSE", "날짜 범위 파싱 오류: " + periodStr);
                    }
                } else {
                    Pattern yearMonthPattern = Pattern.compile("(?:(\\d+)년)?\\s*(?:(\\d+)개월)?");
                    Matcher yearMonthMatcher = yearMonthPattern.matcher(periodStr);
                    if (yearMonthMatcher.find()) {
                        int years = 0;
                        int months = 0;
                        if (yearMonthMatcher.group(1) != null) {
                            years = Integer.parseInt(yearMonthMatcher.group(1));
                        }
                        if (yearMonthMatcher.group(2) != null) {
                            months = Integer.parseInt(yearMonthMatcher.group(2));
                        }
                        periodInMonths = years * 12 + months;
                    }
                }
                careers.add(new Career(company, content, periodInMonths));
            }
        }
        Log.d("PDF_PARSE", "추출된 경력: " + careers.size() + "개");
        return careers;
    }

    private List<Specification> extractSpecifications(String pdfText) {
        List<Specification> specifications = new ArrayList<>();

        // 대외활동 및 봉사활동
        Pattern externalActivityPattern = Pattern.compile("대외활동 및 봉사활동\\s*\\n([\\s\\S]*?)(?:\\n\\s*\\n|자격증/면허증|외국어|자기소개서)", Pattern.DOTALL);
        Matcher externalActivityMatcher = externalActivityPattern.matcher(pdfText);
        if (externalActivityMatcher.find()) {
            String content = externalActivityMatcher.group(1).trim();
            content = content.replaceAll("활동명\\s*기간\\s*활동구분\\(간략한 내용\\)\\s*기관 및 장소\\s*", "").trim();
            if (!content.isEmpty()) {
                specifications.add(new Specification("대외활동 및 봉사활동", content));
            }
        }

        // 자격증/면허증
        Pattern certificatePattern = Pattern.compile("자격증/면허증\\s*\\n([\\s\\S]*?)(?:\\n\\s*\\n|외국어|자기소개서)", Pattern.DOTALL);
        Matcher certificateMatcher = certificatePattern.matcher(pdfText);
        if (certificateMatcher.find()) {
            String content = certificateMatcher.group(1).trim();
            content = content.replaceAll("취득일\\s*,?자격증/면허증\\s*,?등급\\(점수\\)\\s*,?발행처\\s*", "").trim();
            if (!content.isEmpty()) {
                specifications.add(new Specification("자격증/면허증", content));
            }
        }

        // 외국어
        Pattern foreignLanguagePattern = Pattern.compile("외국어\\s*\\n([\\s\\S]*?)(?:\\n\\s*\\n|자기소개서)", Pattern.DOTALL);
        Matcher foreignLanguageMatcher = foreignLanguagePattern.matcher(pdfText);
        if (foreignLanguageMatcher.find()) {
            String content = foreignLanguageMatcher.group(1).trim();
            content = content.replaceAll("공인시험\\s*,?점수\\(등급\\)\\s*,?취득일\\s*,?발행처\\s*", "").trim();
            if (!content.isEmpty()) {
                specifications.add(new Specification("외국어", content));
            }
        }

        // 자기소개서
        Pattern selfIntroPattern = Pattern.compile("자기소개서\\s*\\n([\\s\\S]*?)(?:\\n\\s*\\n|$)");
        Matcher selfIntroMatcher = selfIntroPattern.matcher(pdfText);
        if (selfIntroMatcher.find()) {
            String selfIntroContent = selfIntroMatcher.group(1).trim();
            // 질문 헤더 제거 (예: "1. 기업(회사) 지원동기 및 포부 (500자 내외)")
            selfIntroContent = selfIntroContent.replaceAll("\\d+\\.\\s*[^\\n]+\\s*\\(\\d+자 내외\\)\\s*\\n", "").trim();
            if (!selfIntroContent.isEmpty()) {
                specifications.add(new Specification("자기소개서", selfIntroContent));
            }
        }
        Log.d("PDF_PARSE", "추출된 스펙: " + specifications.size() + "개");
        return specifications;
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
    private void logout() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();  // Firebase 로그아웃

        // 로그인 화면으로 돌아가기
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
