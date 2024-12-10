package com.gcu.jobshorts;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {
    private final FirebaseFirestore firestore;

    public FirestoreHelper() {
        firestore = FirebaseFirestore.getInstance();
    }

    // 조건에 맞는 데이터를 검색
    public void fetchFilteredData(int educationLevel, int minExperience,
                                  String location, String jobType, FirestoreCallback callback) {
        firestore.collection("company").document("3j1eEri0XmlMugVtt2u4")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        List<Map<String, Object>> allJobs = (List<Map<String, Object>>) document.get("company"); // 데이터 배열 필드 이름 확인 필요

                        if (allJobs != null) {
                            List<JobData> filteredJobs = new ArrayList<>();

                            for (Map<String, Object> job : allJobs) {
                                // 데이터 매칭
                                int jobEducation = Integer.parseInt((String) job.get("education_level"));
                                int jobCareerMin = Integer.parseInt((String) job.get("career_min"));
                                int jobCareerMax = Integer.parseInt((String) job.get("carrer_max"));
                                String jobLocation = (String) job.get("location");
                                String jobTechnique = (String) job.get("technique");

                                if (jobEducation <= educationLevel
                                        && jobCareerMin <= minExperience
                                        && jobCareerMax >= minExperience
                                        && jobLocation.equals(location)
                                        && (jobTechnique.contains(jobType)
                                        || jobType.contains(jobTechnique))) {
                                    filteredJobs.add(new JobData(job)); // JobData 생성자로 변환
                                }
                            }

                            callback.onSuccess(filteredJobs);
                        } else {
                            callback.onSuccess(new ArrayList<>()); // 데이터가 없으면 빈 리스트 반환
                        }
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public interface FirestoreCallback {
        void onSuccess(List<JobData> jobList);

        void onFailure(Exception e);
    }
}


