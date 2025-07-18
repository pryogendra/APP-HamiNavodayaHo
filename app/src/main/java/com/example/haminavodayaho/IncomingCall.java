package com.example.haminavodayaho;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.haminavodayaho.WebSocketRequest._WebSocketForegroundService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class IncomingCall extends AppCompatActivity {
    private TextView callerText;
    private ImageView callProfile;
    private Button acceptButton, rejectButton;
    private PowerManager.WakeLock wakeLock;
    private String caller, name, profile;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_incoming_call);

        callProfile = findViewById(R.id.callProfile);
        callerText = findViewById(R.id.callText);
        acceptButton = findViewById(R.id.accept_button);
        rejectButton = findViewById(R.id.reject_button);

        // Turn on screen and show on lock screen
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (keyguardManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                keyguardManager.requestDismissKeyguard(this, null);
            }
        }

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK |
                            PowerManager.ACQUIRE_CAUSES_WAKEUP |
                            PowerManager.ON_AFTER_RELEASE,
                    "haminavodayaho::IncomingCallWakelock"
            );
            wakeLock.acquire(30 * 1000L);
        }
        caller = getIntent().getStringExtra("caller");
        name = getIntent().getStringExtra("name");
        profile = getIntent().getStringExtra("profile");

        if (name != null) {
            callerText.setText("Call from: " + name);
        } else {
            callerText.setText("Call from: " + caller);
        }
        if (profile != null) {
            if (profile.startsWith("/")) {
                File imageFile = new File(profile);
                Glide.with(getApplicationContext())
                        .load(imageFile)
                        .into(callProfile);
            } else {
                Glide.with(getApplicationContext())
                        .load(profile)
                        .into(callProfile);
            }
            callProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        acceptButton.setOnClickListener(view -> {
            //sendCallAction("accept");

            Intent intent = new Intent(this, AudioCall.class);
            intent.putExtra("contact", caller);
            intent.putExtra("username", name);
            intent.putExtra("profile", profile);
            intent.putExtra("isCaller", false);
            startActivity(intent);
            finish();
        });

        rejectButton.setOnClickListener(view -> {
            sendCallAction("reject");
            Toast.makeText(IncomingCall.this, "Call Rejected", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @SuppressLint("Wakelock")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
    private void sendCallAction(String action) {
        if (_WebSocketForegroundService.customWebSocket != null) {
            JSONObject json = new JSONObject();
            try {
                json.put("type", "call");
                json.put("action", action);
                json.put("caller", caller);
                _WebSocketForegroundService.customWebSocket.send(json.toString());
            } catch (JSONException e) {
                Log.e("WebSocket", "Error sending call action: " + e.getMessage());
            }
        }
    }
}