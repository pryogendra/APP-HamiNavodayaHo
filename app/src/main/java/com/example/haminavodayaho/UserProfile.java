package com.example.haminavodayaho;

import static com.example.haminavodayaho.MainActivity.MAIN_URL;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.haminavodayaho.ServerRequest._HttpPostRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {
    String url = MAIN_URL + "getProfile/";
    String profileUrl;
    ImageView userProfile;
    Button chatWithMe;
    TextView userUsername, userCategory, userBio, userLocation, UserContact, userEmail, userDescription,
            userPgDetail, userUgDetail, userSchoolDetail;
    TableRow userPgCollege, userUgCollege, userSchool;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);

        userProfile = findViewById(R.id.userProfile);
        userUsername = findViewById(R.id.userUsername);
        userCategory = findViewById(R.id.userCategory);
        userBio = findViewById(R.id.userBio);
        userLocation = findViewById(R.id.userLocation);
        UserContact = findViewById(R.id.userContact);
        userEmail = findViewById(R.id.userEmail);
        userDescription = findViewById(R.id.userDescription);
        userPgCollege = findViewById(R.id.userPgCollege);
        userPgDetail = findViewById(R.id.userPgDetail);
        userUgCollege = findViewById(R.id.userUgCollege);
        userUgDetail = findViewById(R.id.userUgDetail);
        userSchool = findViewById(R.id.userSchool);
        userSchoolDetail = findViewById(R.id.userSchoolDetail);
        chatWithMe = findViewById(R.id.chatWithMe);

        Intent intent = getIntent();
        String contact = intent.getStringExtra("contact");
        viewProfile(contact);

        userProfile.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            @SuppressLint("InflateParams")
            View dialogView = inflater.inflate(R.layout.layout_dialog_image, null);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView fullImage = dialogView.findViewById(R.id.dialogProfileImage);
            fullImage.setImageDrawable(userProfile.getDrawable());
            builder.setView(dialogView)
                    .setPositiveButton("Close", null)
                    .show();
        });

        chatWithMe.setOnClickListener(v -> {
            Intent intent1 = new Intent(UserProfile.this, Chat.class);
            intent1.putExtra("contact", contact);
            intent1.putExtra("username", userUsername.getText().toString());
            intent1.putExtra("profile", profileUrl);
            startActivity(intent1);
        });
    }
    private void viewProfile(String contact) {
        try{
            Map<String, String> data = new HashMap<>();
            data.put("phone", contact);

            new _HttpPostRequest.PostRequestTask(url, data, new _HttpPostRequest.PostRequestCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onPostRequestComplete(String response) {
                    try{
                        JsonParser parser = new JsonParser();
                        JsonObject rootObject = parser.parse(response).getAsJsonObject();

                        JsonObject jsonObject = rootObject.getAsJsonObject("data");

                        profileUrl = jsonObject.get("profile").isJsonNull() ? null : jsonObject.get("profile").getAsString();
                        String username = jsonObject.get("username").isJsonNull() ? null : jsonObject.get("username").getAsString();
                        String category = jsonObject.get("category").isJsonNull() ? null : jsonObject.get("category").getAsString();
                        String bio = jsonObject.get("bio").isJsonNull() ? null : jsonObject.get("bio").getAsString();
                        String location = jsonObject.get("location").isJsonNull() ? null : jsonObject.get("location").getAsString();
                        String contact = jsonObject.get("contact").isJsonNull() ? null : jsonObject.get("contact").getAsString();
                        String email = jsonObject.get("email").isJsonNull() ? null : jsonObject.get("email").getAsString();
                        String description = jsonObject.get("description").isJsonNull() ? null : jsonObject.get("description").getAsString();
                        String ugInstitute = jsonObject.get("ugInstitute").isJsonNull() ? null : jsonObject.get("ugInstitute").getAsString();
                        String ugYear = jsonObject.get("ugYear").isJsonNull() ? null : jsonObject.get("ugYear").getAsString();
                        String pgInstitute = jsonObject.get("pgInstitute").isJsonNull() ? null : jsonObject.get("pgInstitute").getAsString();
                        String pgYear = jsonObject.get("pgYear").isJsonNull() ? null : jsonObject.get("pgYear").getAsString();
                        String school = jsonObject.get("school").isJsonNull() ? null : jsonObject.get("school").getAsString();
                        String schoolYear = jsonObject.get("schoolYear").isJsonNull() ? null : jsonObject.get("schoolYear").getAsString();

                        if (!TextUtils.isEmpty(profileUrl) || profileUrl != null) {
                            if (profileUrl.startsWith("/")) {
                                File imageFile = new File(profileUrl);
                                Glide.with(UserProfile.this)
                                        .load(imageFile)
                                        .into(userProfile);
                            } else {
                                Glide.with(UserProfile.this)
                                        .load(profileUrl)
                                        .into(userProfile);
                            }
                        }
                        userUsername.setText(username);
                        userCategory.setText(category);
                        userBio.setText(bio);
                        userLocation.setText(location);
                        UserContact.setText(contact);
                        userEmail.setText(email);
                        userDescription.setText(description);
                        userPgDetail.setText(pgInstitute + "\n"+ pgYear);
                        userUgDetail.setText(ugInstitute + "\n" + ugYear);
                        userSchoolDetail.setText(school + "\n" + schoolYear);
                    } catch (Exception e) {
                        Log.e("error", "UserProfile 139 Error: "+e);
                    }
                }
            }).execute();
        } catch (Exception e) {
            Log.e("error", "UserProfile 139 Error: "+e);
        }
    }
    private void openDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}