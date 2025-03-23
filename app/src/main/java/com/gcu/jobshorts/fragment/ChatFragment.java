package com.gcu.jobshorts.fragment;

import android.app.AlertDialog;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gcu.jobshorts.Message;
import com.gcu.jobshorts.R;
import com.gcu.jobshorts.firebase.SharedViewModel;
import com.gcu.jobshorts.adapter.MessageAdapter;
import com.gcu.jobshorts.data.JobData;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private List<JobData> jobDataList; // 회사 정보를 담을 리스트
    private String selectedCompany;

    private RecyclerView recyclerView;
    private TextView welcomeTextView;
    private EditText messageEditText;
    private ImageButton sendButton;

    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    private boolean isFirstMessage = true;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS) // 연결 타임아웃 (30초)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)   // 쓰기 타임아웃 (30초)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)    // 읽기 타임아웃 (15초)
            .build();

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // RecyclerView와 기타 뷰 초기화
        recyclerView = rootView.findViewById(R.id.recycler_view);
        welcomeTextView = rootView.findViewById(R.id.welcome_text);
        messageEditText = rootView.findViewById(R.id.message_edit_text);
        sendButton = rootView.findViewById(R.id.send_btn);

        // 메시지 리스트 및 어댑터 초기화
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        jobDataList = new ArrayList<>();

        // ViewModel에서 jobDataList 관찰
        sharedViewModel.getJobDataList().observe(getViewLifecycleOwner(), jobList -> {
            if (jobList != null) {
                jobDataList.clear();
                jobDataList.addAll(jobList);
                showCompanySelectionDialog();
            }
        });

        // 전송 버튼 클릭 이벤트 설정
        sendButton.setOnClickListener(v -> {
            if (selectedCompany == null || selectedCompany.isEmpty()) {
                showCompanySelectionDialog(); // 회사 선택 다이얼로그 표시
            } else {
                handleSendMessage(); // 바로 메시지 전송
            }
        });

        return rootView;
    }

    private void showCompanySelectionDialog() {
        // RadioGroup을 포함한 Dialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("회사 선택");

        RadioGroup radioGroup = new RadioGroup(requireContext());
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        for (JobData job : jobDataList) {
            if (job.getCompany() != null && !job.getCompany().isEmpty()) {
                RadioButton radioButton = new RadioButton(requireContext());
                radioButton.setText(job.getCompany());
                radioButton.setTag(job.getCompany());

                radioGroup.addView(radioButton);
            }
        }

        builder.setView(radioGroup);

        builder.setPositiveButton("확인", (dialog, which) -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
                selectedCompany = (String) selectedRadioButton.getTag();
                handleSendMessage(); // 회사 선택 후 메시지 전송
            } else {
                addToChat("회사를 선택해주세요.", Message.SENT_BY_BOT);
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void handleSendMessage() {
        String question = messageEditText.getText().toString().trim();
        if (question.isEmpty()) {
            addToChat("질문을 입력해주세요.", Message.SENT_BY_BOT);
            return;
        }
        addToChat(question, Message.SENT_BY_ME);
        messageEditText.setText("");
        callAPI(question, selectedCompany);
        welcomeTextView.setVisibility(View.GONE);
    }

    void addToChat(String message, String sentBy) {
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageList.add(new Message(message, sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

            }
        });
    }

    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_BOT);
    }



    // OkHttpClient 생성 시 타임아웃 설정 추가

    void callAPI(String question, String company) {
        messageList.add(new Message("...", Message.SENT_BY_BOT));

        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();

        try {
            if (isFirstMessage) {
                baseAi.put("role", "system");
                baseAi.put("content",
                        "당신은 신중한 AI 어시스턴트입니다. 회사 정보를 분석하고 제공된 데이터를 기반으로 의견이나 조언을 제공합니다. 한국어로 답변하며, 응답은 최대 5줄로 요약하고, 이전에 언급되지 않은 새로운 관점을 추가하세요.");
                arr.put(baseAi);

                String jsonFilePath = "json/company_info/" + company + ".json";
                String jsonFileContent = readJsonFromAssets(jsonFilePath);

                JSONObject jsonObject = new JSONObject(jsonFileContent);
                String totalRating = jsonObject.optString("total_rating", "N/A");
                String welfareBenefits = jsonObject.optString("welfare_benefits", "N/A");
                String workLifeBalance = jsonObject.optString("work_life_balance", "N/A");
                String companyCulture = jsonObject.optString("company_culture", "N/A");
                String promotionOpportunity = jsonObject.optString("Promotion_opportunity", "N/A");

                JSONArray reviews = jsonObject.getJSONArray("review");
                StringBuilder reviewSummary = new StringBuilder();
                for (int i = 0; i < Math.min(reviews.length(), 3); i++) {
                    JSONObject review = reviews.getJSONObject(i);
                    reviewSummary.append("- ").append(review.optString("title", "제목 없음")).append("\n");
                }

                JSONObject companyInfoMessage = new JSONObject();
                companyInfoMessage.put("role", "system");
                companyInfoMessage.put("content",
                        "다음은 회사 정보입니다:\n" +
                                "회사명: " + company + "\n" +
                                "평점: " + totalRating + "\n" +
                                "복지 및 혜택: " + welfareBenefits + "\n" +
                                "워라밸: " + workLifeBalance + "\n" +
                                "회사 문화: " + companyCulture + "\n" +
                                "승진 기회: " + promotionOpportunity + "\n" +
                                "주요 리뷰 요약:\n" + reviewSummary.toString());
                arr.put(companyInfoMessage);
            }

            userMsg.put("role", "user");
            userMsg.put("content",
                    "위의 회사 정보를 기반으로 질문에 집중해서 한국어로 핵십만 집어서 간결하게 답해주세요. 이전에 언급되지 않은 새로운 통찰이나 관점을 추가하세요." + question);
            arr.put(userMsg);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // OpenAI API 요청
        JSONObject object = new JSONObject();
        try {
            object.put("model", "gpt-4"); // GPT-4 모델 사용
            object.put("messages", arr);
            object.put("temperature", 0.5);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions") // OpenAI API 경로
                .header("Authorization", "Bearer ") //OpenAI API 키 입력해야함!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (e instanceof java.net.SocketTimeoutException) {
                    addResponse("Request timed out. Please try again later.");
                } else {
                    addResponse("Failed to load response due to " + e.getMessage());
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        addResponse(result.trim());
                    } catch (JSONException e) {
                        addResponse("Failed to parse response.");
                    }
                } else {
                    addResponse("Failed to load response due to " + response.body().string());
                }
            }
        });
    }

    private String readJsonFromAssets(String fileName) throws IOException {
        AssetManager assetManager = requireContext().getAssets();
        InputStream inputStream = assetManager.open(fileName);
        StringBuilder jsonContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        }
        return jsonContent.toString();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}