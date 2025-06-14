//package com.example.viewnews.chatgpt;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.viewnews.R;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.IOException;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.MediaType;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//public class ChatActivity extends AppCompatActivity {
//    private EditText inputEditText;
//    private TextView outputTextView;
//    private Button sendButton;
//
//    //deepseek版本
////    private static final String API_KEY = "sk-1dc7e2b8e65b451baafbb8db1e0a64b0";
////    private static final String API_URL = "https://api.deepseek.com/chat/completions";
//
//    //chatgpt版本
//    private static final String API_KEY = "sk-PMqQFrJdFfespfZtfkjkOlmW1KpryRTCgbYB8uAnbT6fG5eT";
//    private static final String API_URL = "https://api.chatanywhere.tech/v1/chat/completions";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat);
//
//        inputEditText = findViewById(R.id.inputEditText);
//        outputTextView = findViewById(R.id.outputTextView);
//        sendButton = findViewById(R.id.sendButton);
//
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String userInput = inputEditText.getText().toString();
//                callChatGPT(userInput);
//            }
//        });
//    }
//
//    private void callChatGPT(String userInput) {
//        OkHttpClient client = new OkHttpClient();
//
//        JSONArray messages = new JSONArray();
//        JSONObject userMessage = new JSONObject();
//        try {
//            userMessage.put("role", "user");
//            userMessage.put("content", userInput);
//            messages.put(userMessage);
//
//            JSONObject jsonBody = new JSONObject();
//
//            //deepseek-chat
//            //chatgpt-3.5-turbo
//            // GPT-4o-mini
//            jsonBody.put("model", "gpt-3.5-turbo");
//            jsonBody.put("messages", messages);
//
//            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));
//
//            Request request = new Request.Builder()
//                    .url(API_URL)
//                    .header("Authorization", "Bearer " + API_KEY)
//                    .post(body)
//                    .build();
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    runOnUiThread(() -> outputTextView.setText("请求失败: " + e.getMessage()));
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (response.isSuccessful()) {
//                        String responseBody = response.body().string();
//                        try {
//                            JSONObject jsonObject = new JSONObject(responseBody);
//                            JSONArray choices = jsonObject.getJSONArray("choices");
//                            String reply = choices.getJSONObject(0).getJSONObject("message").getString("content");
//                            runOnUiThread(() -> outputTextView.setText(reply.trim()));
//                        } catch (Exception e) {
//                            runOnUiThread(() -> outputTextView.setText("解析错误: " + e.getMessage()));
//                        }
//                    } else {
//                        runOnUiThread(() -> outputTextView.setText("请求失败: " + response.message()));
//                    }
//                }
//            });
//        } catch (Exception e) {
//            outputTextView.setText("构建请求失败: " + e.getMessage());
//        }
//    }
//}

