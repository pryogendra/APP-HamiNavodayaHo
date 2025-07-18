package com.example.haminavodayaho;

import static com.example.haminavodayaho.MainActivity.MAIN_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.haminavodayaho.ServerRequest._HttpPostRequest;
import com.example.haminavodayaho.WebSocketRequest._WebSocketForegroundService;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Login extends AppCompatActivity {
    public static String USER = null;
    //String url = MAIN_URL + "sendMobileOTP/";
    String url = MAIN_URL + "sendEmailOTP/";
    String registerUrl = MAIN_URL + "registerMobile/";
    private TextView countryCode, phone, otp;
    String fullNumber, OTP;
    private Button btnContinue;
    private AppCompatButton btnSendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        countryCode = findViewById(R.id.countryCode);
        phone = findViewById(R.id.phoneNumber);
        otp = findViewById(R.id.otp);
        btnContinue = findViewById(R.id.btnContinue);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnContinue.setVisibility(Button.GONE);

        btnSendOtp.setOnClickListener(v -> {
            fullNumber = ""+countryCode.getText()+phone.getText();
            if (countryCode.getText() != "" && phone.getText() != ""){
                Random random = new Random();
                OTP = ""+(100000 + random.nextInt(900000));
                try {
                    Map<String, String> data = new HashMap<>();
                    data.put("phone", fullNumber);
                    data.put("otp", OTP);
                    if (!fullNumber.isEmpty()) {
                        btnSendOtp.setEnabled(false);
                        startTimer();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                btnSendOtp.setEnabled(true);
                            }
                        }, 120000);
                    new _HttpPostRequest.PostRequestTask(url, data, new _HttpPostRequest.PostRequestCallback() {
                        @Override
                        public void onPostRequestComplete(String response) {
                            showCustomToast("OTP sent successfully.");
                        }
                    }).execute();
                        //otp.setText(OTP);
                    } else {
                        showCustomToast("Enter the correct data.");
                    }
                } catch (Exception e) {
                    Log.e("error", "Error 65 "+ e);
                }
                btnContinue.setVisibility(Button.VISIBLE);
            } else {
                showCustomToast("Enter the correct data.");
            }
        });
        btnContinue.setOnClickListener(v -> {
            if (OTP.contentEquals(otp.getText()) && !OTP.isEmpty() && !otp.getText().equals("")){
                registerUser();
            } else{
                showCustomToast("Otp is not correct.");
            }
        });
    }
    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View customView = inflater.inflate(R.layout.custom_toast, null);

        TextView toastMessage = customView.findViewById(R.id.toast_message);
        toastMessage.setText(message);

        Toast customToast = new Toast(getApplicationContext());
        customToast.setDuration(Toast.LENGTH_LONG);
        customToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,0,10);
        customToast.setView(customView);
        customToast.show();
    }

    private void startTimer(){
        CountDownTimer countDownTimer = new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerTest(millisUntilFinished);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                btnSendOtp.setText("Resend OTP");
            }
        }.start();
    }
    private  void updateTimerTest(long millisUntilFinished){
        int minutes = (int) (millisUntilFinished / 1000) / 60;
        int seconds = (int) (millisUntilFinished / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),
                "%02d:%02d", minutes, seconds);
        btnSendOtp.setText(timeLeftFormatted);
    }

    private void registerUser(){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("phone", fullNumber);
            Log.d("taga", "138");
            new _HttpPostRequest.PostRequestTask(registerUrl, data, new _HttpPostRequest.PostRequestCallback() {
                @Override
                public void onPostRequestComplete(String response) {
                    Log.d("taga", "Response: " + response);

                    SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("user", true);
                    editor.putString("phone", fullNumber);
                    USER = fullNumber;
                    editor.apply();
                    if (_WebSocketForegroundService.customWebSocket == null) {
                        _WebSocketForegroundService.startService(getApplicationContext());
                    }
                    startActivity(new Intent(getApplicationContext(), FragmentManager.class));
                    finish();
                    Log.d("taga", "153");
                }
            }).execute();
        } catch (Exception e) {
            Log.e("taga", "Login 133 Error: "+e);
        }
    }
}