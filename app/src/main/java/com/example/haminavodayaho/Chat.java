package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.IP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.haminavodayaho.WebSocketRequest._WebSocketService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.WebSocket;

public class Chat extends AppCompatActivity {
    String chatWebsocketUrl = "ws://"+IP+"/ws/chat/"+USER;
    private static WebSocket chatWebSocket;
    Boolean isLoading = false;
    Toolbar toolbar;
    ImageView backButton, profile, menu, selectFile, btnSend, btnAudioCall;
    TextView username, timestamp, happyTextView;
    public RecyclerView recyclerChat;
    ProgressBar chatProgressBar;
    LinearLayout layoutInputChat;
    EditText textMessage;
    ArrayList<ModelChat> arrChat = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static AdapterChat adapterChat;
    private String contact;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chatToolbar);
        backButton = findViewById(R.id.chatToolBarBackButton);
        profile = findViewById(R.id.chatToolbarProfile);
        username = findViewById(R.id.chatToolbarUsername);
        timestamp = findViewById(R.id.chatToolbarTimestamp);
        happyTextView = findViewById(R.id.happyTextView);
        btnAudioCall = findViewById(R.id.chatToolbarAudioCall);
        menu = findViewById(R.id.chatToolbarMenu);

        recyclerChat = findViewById(R.id.chatRecyclerView);
        chatProgressBar = findViewById(R.id.chatProgressBar);
        layoutInputChat = findViewById(R.id.chatLayoutInput);
        selectFile = findViewById(R.id.chatSelectFile);
        textMessage = findViewById(R.id.chatTextMessage);
        btnSend = findViewById(R.id.chatButtonSend);

        Intent intent = getIntent();
        String name = intent.getStringExtra("username");
        String profileUrl = intent.getStringExtra("profile");
        if (!TextUtils.isEmpty(profileUrl) || profileUrl != null) {
            if (profileUrl.startsWith("/")) {
                File imageFile = new File(profileUrl);
                Glide.with(getApplicationContext())
                        .load(imageFile)
                        .into(profile);
            } else {
                Glide.with(getApplicationContext())
                        .load(profileUrl)
                        .into(profile);
            }
        }
        contact = intent.getStringExtra("contact");
        username.setText(name);
        timestamp.setText("Offline");
        timestamp.setVisibility(View.GONE);
        updateChat();

        btnSend.setOnClickListener(v -> {
            String message = textMessage.getText().toString().trim();
            if (!message.isEmpty() && chatWebSocket != null) {
                try {
                    JSONObject jsonMsg = new JSONObject();
                    jsonMsg.put("type", "chat");
                    jsonMsg.put("sender", USER);
                    jsonMsg.put("receiver", contact);
                    jsonMsg.put("send_data", message);

                    chatWebSocket.send(jsonMsg.toString());
                    textMessage.setText("");
                } catch (Exception e) {
                    Log.e("error", "Failed to send message : ", e);
                }
            } else {
                Toast.makeText(this, "Please wait we are connection with the Receiver..", Toast.LENGTH_LONG).show();
            }
        });

        recyclerChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && !isLoading) {
                    fetchPreviousMessages();
                }
            }
        });

        btnAudioCall.setOnClickListener(v -> {
            Intent intent1 = new Intent(Chat.this, AudioCall.class);
            intent1.putExtra("contact", contact);
            intent1.putExtra("username", name);
            intent1.putExtra("profile", profileUrl);
            startActivity(intent1);
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    void updateChat(){
        try{
            chatWebSocket = _WebSocketService.connect(chatWebsocketUrl+"/"+contact, new _WebSocketService.WebSocketCallback() {
                @Override
                public void onOpen(WebSocket webSocket) {
                    Log.d("WebSocket", "Connected successfully");
                }
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    Log.d("WebSocket", "Message: " + text);

                    try {
                        JsonParser parser = new JsonParser();
                        JsonElement parsedElement = parser.parse(text);
                        JsonObject response = parsedElement.getAsJsonObject();

                        String type = response.has("type") ? response.get("type").getAsString() : "chat";
                        JsonArray messagesArray = new JsonArray();

                        if (type.equals("initial") || type.equals("scroll")) {
                            messagesArray = response.getAsJsonArray("messages");
                        } else if (type.equals("chat")) {
                            messagesArray.add(response);
                        }

                        // Prepare messages
                        ArrayList<ModelChat> newMessages = new ArrayList<>();
                        for (JsonElement element : messagesArray) {
                            JsonObject jsonObject = element.getAsJsonObject();

                            String messageId = jsonObject.get("message_id").getAsString();
                            String sender = jsonObject.get("sender").getAsString();
                            String receiver = jsonObject.get("receiver").getAsString();
                            String textMessage = jsonObject.get("send_data").getAsString();
                            String timestamp = jsonObject.get("timestamp").getAsString();
                            String dataUrl = jsonObject.has("url") ? jsonObject.get("url").getAsString() : null;

                            boolean status = false;
                            ModelChat chatMessage = new ModelChat(messageId, sender, receiver, textMessage, dataUrl, timestamp, status);
                            newMessages.add(chatMessage);
                            Chat.this.runOnUiThread(() -> {
                                chatProgressBar.setVisibility(ProgressBar.GONE);
                                happyTextView.setVisibility(View.VISIBLE);
                            });
                        }

                        // Updating
                        Chat.this.runOnUiThread(() -> {
                            if (!newMessages.isEmpty() || !arrChat.isEmpty()){
                                chatProgressBar.setVisibility(ProgressBar.GONE);
                                happyTextView.setVisibility(View.GONE);
                            } else {
                                happyTextView.setVisibility(View.VISIBLE);
                                chatProgressBar.setVisibility(ProgressBar.GONE);
                            }
                            for (ModelChat chatMessage : newMessages) {
                                if (type.equals("scroll")) {
                                    arrChat.add(0, chatMessage);
                                    adapterChat.notifyItemInserted(0);
                                } else {
                                    arrChat.add(chatMessage);
                                    adapterChat.notifyItemInserted(arrChat.size() - 1);
                                    recyclerChat.scrollToPosition(arrChat.size() - 1);
                                }
                            }
                        });

                    } catch (Exception e) {
                        Log.e("WebSocket", "Failed to parse message: " + e.getMessage());
                    }
                }
            });
            adapterChat = new AdapterChat(Chat.this, arrChat);
            recyclerChat.setLayoutManager(new LinearLayoutManager(Chat.this));
            recyclerChat.setAdapter(adapterChat);
            adapterChat.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("WebSocket", "Chat 191 Error :"+e.getMessage());
        }
    }

    private void fetchPreviousMessages() {
        isLoading = true;
        int offset = arrChat.size();
        try {
            JSONObject jsonMsg = new JSONObject();
            jsonMsg.put("type", "scroll");
            jsonMsg.put("sender", USER);
            jsonMsg.put("receiver", contact);
            jsonMsg.put("offset", offset);
            if (chatWebSocket != null) {
                chatWebSocket.send(jsonMsg.toString());
            }
        } catch (Exception e) {
            Log.e("error", "Chat 219 Error : ", e);
        }
        Log.d("WebSocket", "Sent scroll request with offset: " + offset);
    }
}