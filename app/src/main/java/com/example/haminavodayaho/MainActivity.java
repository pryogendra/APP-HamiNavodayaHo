package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.haminavodayaho.WebSocketRequest._WebSocketForegroundService;

public class MainActivity extends AppCompatActivity {
    //public static String MAIN_URL = "http://192.168.31.107:8000/api/";
    //public static String IP = "192.168.167.130:8000";
    public static String IP = "192.168.31.107:8000";
    public static String MAIN_URL = "http://"+IP+"/api/";

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_IS_DARK_MODE = "is_dark_mode";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applySavedTheme();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ImageView splashIcon = findViewById(R.id.splashicon);

        Animation alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        splashIcon.setAnimation(alphaAnim);

        if (!isNetworkConnected()) {
            showNoInternetDialog();
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
                    boolean token = pref.getBoolean("user", false);
                    USER = pref.getString("phone", null);
                    if (token) {
                        if (_WebSocketForegroundService.customWebSocket == null) {
                            _WebSocketForegroundService.startService(getApplicationContext());
                        }
                        startActivity(new Intent(getApplicationContext(), FragmentManager.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                    finish();
                }
            }, 1100);
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
    private void showNoInternetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setCancelable(false)
                .setPositiveButton("Retry", (dialog, which) -> {
                    if (!isNetworkConnected()) {
                        showNoInternetDialog();
                    } else {
                        dialog.dismiss();
                        recreate();
                    }
                })
                .setNegativeButton("Exit", (dialog, which) -> finish())
                .show();
    }
    private void applySavedTheme() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_IS_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}