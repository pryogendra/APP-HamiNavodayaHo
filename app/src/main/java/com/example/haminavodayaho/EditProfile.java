package com.example.haminavodayaho;

import static com.example.haminavodayaho.MainActivity.MAIN_URL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.haminavodayaho.ServerRequest._HttpPostRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {
    String url = MAIN_URL + "updateProfile/";
    private Uri selectedImageUri = null;
    ImageView profileImage;
    TextInputEditText inputName, inputCategory, inputBio, inputLocation, inputContact, inputEmail, inputDescription,
            inputPGInstitute, inputPGYear, inputUGInstitute, inputUGYear, inputSchool, inputSchoolYear;
    Button btnSave;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->{
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    selectedImageUri = result.getData().getData();
                    profileImage.setImageURI(selectedImageUri);
                }
            });
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        profileImage = findViewById(R.id.profileImage);
        inputName = findViewById(R.id.inputName);
        inputCategory = findViewById(R.id.inputCategory);
        inputBio = findViewById(R.id.inputBio);
        inputLocation = findViewById(R.id.inputLocation);
        inputContact = findViewById(R.id.inputContact);
        inputEmail = findViewById(R.id.inputEmail);
        inputDescription = findViewById(R.id.inputDescription);
        inputPGInstitute = findViewById(R.id.inputPGInstitute);
        inputPGYear = findViewById(R.id.inputPGYear);
        inputUGInstitute = findViewById(R.id.inputUGInstitute);
        inputUGYear = findViewById(R.id.inputUGYear);
        inputSchool = findViewById(R.id.inputSchool);
        inputSchoolYear = findViewById(R.id.inputSchoolYear);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        if (intent != null){
            addData(intent);
        }

        profileImage.setOnClickListener(v -> showProfileImageDialog());
        btnSave.setOnClickListener(v -> {
            updateProfile();
        });
    }
    private void showProfileImageDialog() {
        String[] options = {"View Profile", "Change Profile"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option")
                .setItems(options, (dialog, which) -> {
                    if (which ==0){
                        showImageDialog();
                    } else{
                        selectNewImage();
                    }
                }).show();
    }
    private void showImageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.layout_dialog_image, null);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView fullImage = dialogView.findViewById(R.id.dialogProfileImage);

        if (selectedImageUri != null){
            fullImage.setImageURI(selectedImageUri);
        } else{
            fullImage.setImageDrawable(profileImage.getDrawable());
        }
        builder.setView(dialogView)
                .setPositiveButton("Close", null)
                .show();
    }
    private void selectNewImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    private void updateProfile() {
        String name = Objects.requireNonNull(inputName.getText()).toString().trim();
        String category = Objects.requireNonNull(inputCategory.getText()).toString().trim();
        String bio = Objects.requireNonNull(inputBio.getText()).toString().trim();
        String location = Objects.requireNonNull(inputLocation.getText()).toString().trim();
        String contact = Objects.requireNonNull(inputContact.getText()).toString().trim();
        String email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
        String description = Objects.requireNonNull(inputDescription.getText()).toString().trim();
        String pgInstitute = Objects.requireNonNull(inputPGInstitute.getText()).toString().trim();
        String pgYear = Objects.requireNonNull(inputPGYear.getText()).toString().trim();
        String ugInstitute = Objects.requireNonNull(inputUGInstitute.getText()).toString().trim();
        String ugYear = Objects.requireNonNull(inputUGYear.getText()).toString().trim();
        String school = Objects.requireNonNull(inputSchool.getText()).toString().trim();
        String schoolYear = Objects.requireNonNull(inputSchoolYear.getText()).toString().trim();

        try{
            Map<String, String> profileData = new HashMap<>();
            profileData.put("phone", contact);
            if (selectedImageUri != null) {
                profileData.put("profile", getRealPathFromURI(selectedImageUri));
            } else {
                profileData.put("profile", "");
            }
            profileData.put("username", name);
            profileData.put("category", category);
            profileData.put("bio", bio);
            profileData.put("location", location);
            profileData.put("email", email);
            profileData.put("description", description);
            profileData.put("pgInstitute", pgInstitute);
            profileData.put("pgYear", pgYear);
            profileData.put("ugInstitute", ugInstitute);
            profileData.put("ugYear", ugYear);
            profileData.put("school", school);
            profileData.put("schoolYear", schoolYear);

            new _HttpPostRequest.PostRequestTask(url, profileData, new _HttpPostRequest.PostRequestCallback() {
                @Override
                public void onPostRequestComplete(String response) {
                    try {
                        startActivity(new Intent(getApplicationContext(), FragmentManager.class));
                    } catch (Exception e) {
                        Log.e("taga", "161 Error "+e);
                    }
                }
            }).execute();

        } catch (Exception e) {
            Log.e("taga", "EditProfile 163 Error: "+e);
        }
    }
    private void addData(Intent intent){
        try {
            byte[] byteArray = getIntent().getByteArrayExtra("profileImage");
            if (byteArray != null) {
                profileImage.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));
            }
            inputName.setText(intent.getStringExtra("username"));
            inputCategory.setText(intent.getStringExtra("category"));
            inputBio.setText(intent.getStringExtra("bio"));
            inputLocation.setText(intent.getStringExtra("location"));
            inputContact.setText(intent.getStringExtra("contact"));
            inputEmail.setText(intent.getStringExtra("email"));
            inputDescription.setText(intent.getStringExtra("description"));
            inputPGInstitute.setText(intent.getStringExtra("pgInstitute"));
            inputPGYear.setText(intent.getStringExtra("pgYear"));
            inputUGInstitute.setText(intent.getStringExtra("ugInstitute"));
            inputUGYear.setText(intent.getStringExtra("ugYear"));
            inputSchool.setText(intent.getStringExtra("school"));
            inputSchoolYear.setText(intent.getStringExtra("schoolYear"));
        } catch (Exception e) {
            Log.e("taga", "EditProfile 186 Error: "+e);
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }
}