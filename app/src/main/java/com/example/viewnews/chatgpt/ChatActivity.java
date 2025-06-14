package com.example.viewnews.chatgpt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.viewnews.R;

public class ChatActivity extends AppCompatActivity {
    private EditText inputEditText;
    private TextView outputTextView;
    private Button sendButton;

    // 定义广播接收器
    private final BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String reply = intent.getStringExtra(ChatService.EXTRA_RESPONSE);
            outputTextView.setText(reply);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        inputEditText = findViewById(R.id.inputEditText);
        outputTextView = findViewById(R.id.outputTextView);
        sendButton = findViewById(R.id.sendButton);

        // 注册广播接收器
        registerReceiver(chatReceiver, new IntentFilter(ChatService.ACTION_CHAT_RESPONSE));

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput = inputEditText.getText().toString();
                Intent intent = new Intent(ChatActivity.this, ChatService.class);
                intent.putExtra(ChatService.EXTRA_USER_INPUT, userInput);
                startService(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(chatReceiver);
    }
}
