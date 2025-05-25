package com.gcu.jobshorts.fragment.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gcu.jobshorts.R;
import com.gcu.jobshorts.adapter.MessageAdapter;
import com.gcu.jobshorts.Message;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment_first extends Fragment {
    private RecyclerView recyclerView;
    private TextView welcomeTextView;
    private EditText messageEditText;
    private ImageButton sendButton;

    private List<Message> messageList;
    private MessageAdapter messageAdapter;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    public ChatFragment_first() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);
        welcomeTextView = rootView.findViewById(R.id.welcome_text);
        messageEditText = rootView.findViewById(R.id.message_edit_text);
        sendButton = rootView.findViewById(R.id.send_btn);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        sendButton.setOnClickListener(v -> handleSendMessage());

        return rootView;
    }

    private void handleSendMessage() {
        String question = messageEditText.getText().toString().trim();
        if (question.isEmpty()) {
            addToChat("질문을 입력해주세요.", Message.SENT_BY_BOT);
            return;
        }
        addToChat(question, Message.SENT_BY_ME);
        messageEditText.setText("");
        callServer(question);
        welcomeTextView.setVisibility(View.GONE);
    }

    void addToChat(String message, String sentBy) {
        requireActivity().runOnUiThread(() -> {
            messageList.add(new Message(message, sentBy));
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        });
    }

    void addResponse(String response) {
        messageList.remove(messageList.size() - 1);
        addToChat(response, Message.SENT_BY_BOT);
    }

    void callServer(String question) {
        messageList.add(new Message("...", Message.SENT_BY_BOT));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("question", question);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://192.168.0.25:5000/ask")  // ⭐ 서버 URL 입력
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("서버 요청 실패: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject responseJson = new JSONObject(response.body().string());
                        String summary = responseJson.getString("summary");
                        addResponse(summary);
                    } catch (JSONException e) {
                        addResponse("서버 응답 파싱 실패.");
                    }
                } else {
                    addResponse("서버 응답 실패: " + response.code());
                }
            }
        });
    }
}
