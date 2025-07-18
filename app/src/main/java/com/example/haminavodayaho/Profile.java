package com.example.haminavodayaho;

import static com.example.haminavodayaho.Login.USER;
import static com.example.haminavodayaho.MainActivity.MAIN_URL;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.haminavodayaho.ServerRequest._HttpPostRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Profile extends Fragment {
    String url = MAIN_URL + "getProfile/";
    ImageView profile;
    TextView username, category, bio, location, contact, email, description, pgInstitute, pgYear, ugInstitute, ugYear, school, schoolYear;
    Button btnUpdate;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public Profile() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profile = view.findViewById(R.id.profile);
        username = view.findViewById(R.id.username);
        category = view.findViewById(R.id.category);
        bio = view.findViewById(R.id.bio);
        location = view.findViewById(R.id.location);
        contact = view.findViewById(R.id.contact);
        email = view.findViewById(R.id.email);
        description = view.findViewById(R.id.description);
        pgInstitute = view.findViewById(R.id.pgInstitute);
        pgYear = view.findViewById(R.id.pgYear);
        ugInstitute = view.findViewById(R.id.ugInstitute);
        ugYear = view.findViewById(R.id.ugYear);
        school = view.findViewById(R.id.schoolInstitute);
        schoolYear = view.findViewById(R.id.schoolYear);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        viewProfile();

        btnUpdate.setOnClickListener(v -> {
            Bitmap bitmap = ((BitmapDrawable) profile.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent = new Intent(getContext(), EditProfile.class);

            intent.putExtra("profile", byteArray);
            intent.putExtra("username", username.getText().toString());
            intent.putExtra("category", category.getText().toString());
            intent.putExtra("bio", bio.getText().toString());
            intent.putExtra("location", location.getText().toString());
            intent.putExtra("contact", contact.getText().toString());
            intent.putExtra("email", email.getText().toString());
            intent.putExtra("description", description.getText().toString());
            intent.putExtra("pgInstitute", pgInstitute.getText().toString());
            intent.putExtra("pgYear", pgYear.getText().toString());
            intent.putExtra("ugInstitute", ugInstitute.getText().toString());
            intent.putExtra("ugYear", ugYear.getText().toString());
            intent.putExtra("school", school.getText().toString());
            intent.putExtra("schoolYear", schoolYear.getText().toString());
            startActivity(intent);
        });
        return view;
    }
    private void viewProfile(){
        try{
            Map<String, String> data = new HashMap<>();
            data.put("phone", USER);

            new _HttpPostRequest.PostRequestTask(url, data, new _HttpPostRequest.PostRequestCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onPostRequestComplete(String response) {
                    try{
                        JsonParser parser = new JsonParser();
                        JsonObject rootObject = parser.parse(response).getAsJsonObject();

                        JsonObject jsonObject = rootObject.getAsJsonObject("data");
                        String profileUrl = jsonObject.get("profile").isJsonNull() ? null : jsonObject.get("profile").getAsString();
                        String username = jsonObject.get("username").getAsString();
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
                        if (!TextUtils.isEmpty(profileUrl)) {
                            if (profileUrl.startsWith("/")) {
                                File imageFile = new File(profileUrl);
                                Glide.with(requireContext())
                                        .load(imageFile)
                                        .into(profile);
                            } else {
                                Glide.with(requireContext())
                                        .load(profileUrl)
                                        .into(profile);
                            }
                        }
                        profile.setOnClickListener(v -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            LayoutInflater inflater = getLayoutInflater();
                            @SuppressLint("InflateParams")
                            View dialogView = inflater.inflate(R.layout.layout_dialog_image, null);
                            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView fullImage = dialogView.findViewById(R.id.dialogProfileImage);
                            fullImage.setImageDrawable(profile.getDrawable());
                            builder.setView(dialogView)
                                    .setPositiveButton("Close", null)
                                    .show();
                        });
                        Profile.this.username.setText(username);
                        Profile.this.category.setText(category);
                        Profile.this.bio.setText(bio);
                        Profile.this.location.setText(location);
                        Profile.this.contact.setText(contact);
                        Profile.this.email.setText(email);
                        Profile.this.description.setText(description);
                        Profile.this.pgInstitute.setText(pgInstitute);
                        Profile.this.pgYear.setText(pgYear);
                        Profile.this.ugInstitute.setText(ugInstitute);
                        Profile.this.ugYear.setText(ugYear);
                        Profile.this.school.setText(school);
                        Profile.this.schoolYear.setText(schoolYear);
                    } catch (Exception e) {
                        Log.e("error", "Profile 176 Error: "+response);
                    }
                    }
                }).execute();
        } catch (Exception e){
            Log.e("error", "Profile 107 Error: "+e);
        }
    }
    private void openDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).create();
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