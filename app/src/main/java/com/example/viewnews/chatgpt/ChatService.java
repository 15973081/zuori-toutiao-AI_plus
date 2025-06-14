// ChatService.java
package com.example.viewnews.chatgpt;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;
/**
 * ChatService：后台服务，继承IntentService，负责异步调用ChatGPT接口。
 * 处理完成后通过广播发送结果，解耦UI与网络请求。
 */
public class ChatService extends IntentService {
    public static final String ACTION_CHAT_RESPONSE = "com.example.chat.RESPONSE";

    // 🌟定义广播动作常量
    public static final String EXTRA_USER_INPUT = "user_input";

    //🌟Intent中传递的用户输入Key
    public static final String EXTRA_RESPONSE = "response";
    //🌟广播中传递的AI回复Key

    public ChatService() {
        super("ChatService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userInput = intent.getStringExtra(EXTRA_USER_INPUT);
        String reply = callChatGPT(userInput);

        // 发广播
        Intent responseIntent = new Intent(ACTION_CHAT_RESPONSE);
        responseIntent.putExtra(EXTRA_RESPONSE, reply);
        sendBroadcast(responseIntent);
    }

    //🌟调用ChatGPT接口的同步方法，使用OkHttp完成网络请求

    private String callChatGPT(String userInput) {
        OkHttpClient client = new OkHttpClient();
        try {
            //请求JSON体
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", userInput);
            messages.put(userMessage);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", messages);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.parse("application/json"));

            // //deepseek版本
            ////    private static final String API_KEY = "sk-1dc7e2b8e65b451baafbb8db1e0a64b0";
            ////    private static final String API_URL = "https://api.deepseek.com/chat/completions";
            //
            //    //chatgpt版本


            //构造HTTP请求

            Request request = new Request.Builder()
                    .url("https://api.chatanywhere.tech/v1/chat/completions")
                    .header("Authorization", "Bearer sk-PMqQFrJdFfespfZtfkjkOlmW1KpryRTCgbYB8uAnbT6fG5eT")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                JSONArray choices = json.getJSONArray("choices");
                return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
            } else {
                return "请求失败: " + response.message();
            }
        } catch (Exception e) {
            return "异常: " + e.getMessage();
        }
    }
}
