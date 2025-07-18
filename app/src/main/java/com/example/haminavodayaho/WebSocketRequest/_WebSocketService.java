package com.example.haminavodayaho.WebSocketRequest;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class _WebSocketService {

    public static WebSocket connect(String url, WebSocketCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder().url(url).build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d("WebSocket", "Opened: " + webSocket.request().url());
                callback.onOpen(webSocket);
            }
            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                callback.onMessage(webSocket, text);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d("WebSocket", "Closing: " + reason);
            }
            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d("WebSocket", "Closed: " + reason);
            }
            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.e("WebSocket", "Failure: " + t.getMessage());
            }
        };
        return client.newWebSocket(request, listener);
    }

    public interface WebSocketCallback {
        void onOpen(WebSocket webSocket);
        void onMessage(WebSocket webSocket, String text);
    }
}

/*
String wsUrl = "ws://yourserver.com/ws/chat/123";

WebSocket websocket = _WebSocketService.connect(wsUrl, new _WebSocketClient.WebSocketCallback() {
    @Override
    public void onOpen(WebSocket webSocket) {
        Log.d("WebSocket", "Connected successfully");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d("WebSocket", "Message: " + text);
    }

});

 */
