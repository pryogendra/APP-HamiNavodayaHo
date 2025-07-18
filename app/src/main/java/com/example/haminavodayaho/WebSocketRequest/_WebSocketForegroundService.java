package com.example.haminavodayaho.WebSocketRequest;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.IP;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.haminavodayaho.FragmentManager;
import com.example.haminavodayaho.IncomingCall;
import com.example.haminavodayaho.R;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class _WebSocketForegroundService extends Service {

    private static final String CHANNEL_ID = "haminavodayaho";
    public static WebSocket customWebSocket;
    private final String socketUrl = "ws://" + IP + "/ws/custom/" + USER;
    @SuppressLint("StaticFieldLeak")
    static Context context;

    public static void startService(Context context) {
        _WebSocketForegroundService.context = context;
        Intent serviceIntent = new Intent(context, _WebSocketForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        connectWebSocket(socketUrl);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "pryog",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(null)
                .setContentText(null)
                .setSmallIcon(R.drawable.haminavodayaho)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .build();
        startForeground(1, notification);

        return START_STICKY;
    }

    private void connectWebSocket(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d("WebSocket", "WebSocket opened: " + webSocket.request().url());
                customWebSocket = webSocket;
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.d("WebSocket", "onMessage: " + text);
                try {
                    JSONObject json = new JSONObject(text);
                    String message = json.getString("message");
                    if (message.equals("calling")){
                        String caller = json.getString("caller");
                        String name = json.getString("name");
                        String profile = json.optString("profile", null);

                        Intent intent = new Intent(context, IncomingCall.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("caller", caller);
                        intent.putExtra("name", name);
                        intent.putExtra("profile", profile);

                        context.startActivity(intent);
                    } else {
                        Log.d("WebSocket", "onMessage: " + message);
                        showIncomingNotification(message);
                    }
                } catch (JSONException e) {
                    Log.e("WebSocket", "Error parsing JSON: " + e.getMessage());
                }

                Intent intent = new Intent("com.example.haminavodayaho.WEBSOCKET_MESSAGE");
                intent.putExtra("message", text);
                sendBroadcast(intent);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.e("WebSocket", "onClosed: " + reason);
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.e("WebSocket", "onClosing: " + reason);
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
                Log.e("WebSocket", "onFailure: " + (response != null ? response.toString() : "No Response") + " Throwable: " + t.toString());
                super.onFailure(webSocket, t, response);
            }
        };

        customWebSocket = client.newWebSocket(request, listener);
    }

    private void showIncomingNotification(String message) {
        Intent intent = new Intent(this, FragmentManager.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.haminavodayaho)
                .setContentTitle("New Message")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), notification);
        }
    }

    @Override
    public void onDestroy() {
        if (customWebSocket != null) {
            customWebSocket.close(1000, "Service destroyed");
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
